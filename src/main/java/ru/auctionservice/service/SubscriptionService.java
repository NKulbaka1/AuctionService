package ru.auctionservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.auctionservice.dto.LotShortResponse;
import ru.auctionservice.entity.Lot;
import ru.auctionservice.entity.LotSubscription;
import ru.auctionservice.exception.AlreadySubscribedException;
import ru.auctionservice.exception.CannotUnsubscribeException;
import ru.auctionservice.exception.NotSubscribedException;
import ru.auctionservice.repository.LotSubscriptionRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final LotSubscriptionRepository subscriptionRepository;
    private final LotService lotService;

    @Transactional(readOnly = true)
    public List<LotShortResponse> getSubscribedLots(Long userId) {
        return subscriptionRepository.findSubscribedLotsByUserId(userId)
                .stream()
                .map(this::toShortResponse)
                .toList();
    }

    @Transactional
    public void subscribe(Long userId, Long lotId) {
        lotService.findById(lotId);
        if (subscriptionRepository.existsByUserIdAndLotId(userId, lotId)) {
            throw new AlreadySubscribedException(userId, lotId);
        }
        subscriptionRepository.save(
                LotSubscription.builder().userId(userId).lotId(lotId).build()
        );
    }

    @Transactional
    public void unsubscribe(Long userId, Long lotId) {
        Lot lot = lotService.findById(lotId);
        if (lot.getSellerId().equals(userId)) {
            throw new CannotUnsubscribeException(lotId);
        }
        LotSubscription subscription = subscriptionRepository.findByUserIdAndLotId(userId, lotId)
                .orElseThrow(() -> new NotSubscribedException(userId, lotId));
        subscriptionRepository.delete(subscription);
    }

    @Transactional
    public void autoSubscribeSeller(Long sellerId, Long lotId) {
        if (!subscriptionRepository.existsByUserIdAndLotId(sellerId, lotId)) {
            subscriptionRepository.save(
                    LotSubscription.builder().userId(sellerId).lotId(lotId).build()
            );
        }
    }

    private LotShortResponse toShortResponse(Lot lot) {
        return LotShortResponse.builder()
                .id(lot.getId())
                .title(lot.getTitle())
                .currentPrice(lot.getCurrentPrice())
                .status(lot.getStatus())
                .imageUrl(lot.getImageUrl())
                .endsAt(lot.getEndsAt())
                .build();
    }
}
