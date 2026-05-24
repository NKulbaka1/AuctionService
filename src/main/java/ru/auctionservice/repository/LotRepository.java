package ru.auctionservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.auctionservice.entity.Lot;
import ru.auctionservice.entity.LotStatus;

import java.math.BigDecimal;

public interface LotRepository extends JpaRepository<Lot, Long> {

    Page<Lot> findAllByStatus(LotStatus status, Pageable pageable);

    @Modifying
    @Query("UPDATE Lot l SET l.currentPrice = :price " +
           "WHERE l.id = :id " +
           "AND l.currentPrice < :price " +
           "AND l.status = ru.auctionservice.entity.LotStatus.ACTIVE " +
           "AND (l.endsAt IS NULL OR l.endsAt > CURRENT_TIMESTAMP)")
    int updateCurrentPriceIfHigher(@Param("id") Long id, @Param("price") BigDecimal price);
}
