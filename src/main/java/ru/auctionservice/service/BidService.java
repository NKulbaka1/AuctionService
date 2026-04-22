package ru.auctionservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.auctionservice.dto.BidRequest;
import ru.auctionservice.dto.BidResponse;
import ru.auctionservice.dto.CurrentPriceResponse;
import ru.auctionservice.entity.Bid;
import ru.auctionservice.entity.Lot;
import ru.auctionservice.entity.LotStatus;
import ru.auctionservice.exception.BidTooLowException;
import ru.auctionservice.exception.LotNotActiveException;
import ru.auctionservice.kafka.BidEventPublisher;
import ru.auctionservice.kafka.BidPlacedEvent;
import ru.auctionservice.repository.BidRepository;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@RequiredArgsConstructor
public class BidService {

    private final BidRepository bidRepository;
    private final LotService lotService;
    private final BidEventPublisher bidEventPublisher;

    @Transactional
    public BidResponse placeBid(Long lotId, BidRequest request) {
        Lot lot = lotService.findById(lotId);

        if (lot.getStatus() != LotStatus.ACTIVE) {
            throw new LotNotActiveException(lotId);
        }

        if (request.getAmount().compareTo(lot.getCurrentPrice()) <= 0) {
            throw new BidTooLowException(
                    "Bid amount " + request.getAmount() + " must be greater than current price " + lot.getCurrentPrice()
            );
        }

        lot.setCurrentPrice(request.getAmount());

        Bid bid = Bid.builder()
                .lot(lot)
                .bidderId(request.getBidderId())
                .amount(request.getAmount())
                .build();

        Bid saved = bidRepository.save(bid);

        BidPlacedEvent event = BidPlacedEvent.builder()
                .bidId(saved.getId())
                .lotId(lotId)
                .bidderId(saved.getBidderId())
                .amount(saved.getAmount())
                .newCurrentPrice(lot.getCurrentPrice())
                .placedAt(saved.getCreatedAt())
                .build();

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                bidEventPublisher.publish(event);
            }
        });

        return BidResponse.builder()
                .id(saved.getId())
                .lotId(lotId)
                .bidderId(saved.getBidderId())
                .amount(saved.getAmount())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public CurrentPriceResponse getCurrentPrice(Long lotId) {
        Lot lot = lotService.findById(lotId);
        return CurrentPriceResponse.builder()
                .lotId(lotId)
                .currentPrice(lot.getCurrentPrice())
                .build();
    }
}
