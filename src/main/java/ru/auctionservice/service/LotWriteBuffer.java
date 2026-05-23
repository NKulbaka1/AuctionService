package ru.auctionservice.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import ru.auctionservice.dto.LotCreateRequest;
import ru.auctionservice.entity.LotStatus;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class LotWriteBuffer {

    private record PendingLot(
            LotCreateRequest request,
            LocalDateTime now,
            CompletableFuture<Long> idFuture
    ) {}

    private static final int MAX_BATCH = 10;

    private static final long BATCH_WINDOW_MS = 10;

    private final BlockingQueue<PendingLot> queue = new LinkedBlockingQueue<>();
    private final JdbcTemplate jdbcTemplate;
    private final TransactionTemplate transactionTemplate;
    private volatile boolean running = true;
    private Thread worker;

    public LotWriteBuffer(JdbcTemplate jdbcTemplate, PlatformTransactionManager txManager) {
        this.jdbcTemplate = jdbcTemplate;
        this.transactionTemplate = new TransactionTemplate(txManager);
    }

    @PostConstruct
    public void start() {
        worker = new Thread(this::run, "lot-write-buffer");
        worker.setDaemon(true);
        worker.start();
        log.info("LotWriteBuffer started");
    }

    @PreDestroy
    public void stop() {
        running = false;
        worker.interrupt();
        List<PendingLot> remaining = new ArrayList<>();
        queue.drainTo(remaining);
        if (!remaining.isEmpty()) {
            flush(remaining);
        }
    }

    public Long enqueue(LotCreateRequest request) {
        CompletableFuture<Long> future = new CompletableFuture<>();
        queue.offer(new PendingLot(request, LocalDateTime.now(), future));
        try {
            return future.get();
        } catch (Exception e) {
            future.cancel(true);
            throw new RuntimeException("Lot write failed", e);
        }
    }

    private void run() {
        while (running) {
            try {
                List<PendingLot> batch = new ArrayList<>(MAX_BATCH);
                batch.add(queue.take());

                long deadline = System.currentTimeMillis() + BATCH_WINDOW_MS;
                while (batch.size() < MAX_BATCH) {
                    long remaining = deadline - System.currentTimeMillis();
                    if (remaining <= 0) break;
                    PendingLot next = queue.poll(remaining, TimeUnit.MILLISECONDS);
                    if (next == null) break;
                    batch.add(next);
                }
                flush(batch);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void flush(List<PendingLot> batch) {
        try {
            List<Long> ids = transactionTemplate.execute(status -> {
                StringBuilder sql = new StringBuilder(
                        "INSERT INTO lots (title, description, starting_price, current_price, " +
                        "status, image_url, seller_id, created_at, updated_at, ends_at) VALUES "
                );
                List<Object> params = new ArrayList<>(batch.size() * 10);

                for (int i = 0; i < batch.size(); i++) {
                    if (i > 0) sql.append(",");
                    sql.append("(?,?,?,?,?,?,?,?,?,?)");
                    PendingLot p = batch.get(i);
                    LotCreateRequest r = p.request();
                    LotStatus st = r.getStatus() != null ? r.getStatus() : LotStatus.DRAFT;
                    params.add(r.getTitle());
                    params.add(r.getDescription());
                    params.add(r.getStartingPrice());
                    params.add(r.getStartingPrice());
                    params.add(st.name());
                    params.add(r.getImageUrl());
                    params.add(r.getSellerId());
                    params.add(Timestamp.valueOf(p.now()));
                    params.add(Timestamp.valueOf(p.now()));
                    params.add(r.getEndsAt() != null ? Timestamp.valueOf(r.getEndsAt()) : null);
                }
                sql.append(" RETURNING id");

                List<Long> lotIds = jdbcTemplate.query(
                        sql.toString(),
                        (rs, rowNum) -> rs.getLong("id"),
                        params.toArray()
                );

                List<Object[]> subParams = new ArrayList<>(batch.size());
                for (int i = 0; i < batch.size(); i++) {
                    subParams.add(new Object[]{batch.get(i).request().getSellerId(), lotIds.get(i)});
                }
                jdbcTemplate.batchUpdate(
                        "INSERT INTO lot_subscriptions (user_id, lot_id) VALUES (?, ?) ON CONFLICT DO NOTHING",
                        subParams
                );

                return lotIds;
            });

            for (int i = 0; i < batch.size(); i++) {
                batch.get(i).idFuture().complete(ids.get(i));
            }
            if (batch.size() > 1) {
                log.debug("Flushed batch of {} lots, ids={}", batch.size(), ids);
            }
        } catch (Exception e) {
            log.error("Lot batch flush failed: {}", e.getMessage(), e);
            batch.forEach(p -> p.idFuture().completeExceptionally(e));
        }
    }
}
