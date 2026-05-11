package ru.auctionservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.auctionservice.entity.Lot;
import ru.auctionservice.entity.LotSubscription;

import java.util.List;
import java.util.Optional;

public interface LotSubscriptionRepository extends JpaRepository<LotSubscription, Long> {

    boolean existsByUserIdAndLotId(Long userId, Long lotId);

    Optional<LotSubscription> findByUserIdAndLotId(Long userId, Long lotId);

    @Query("SELECT l FROM Lot l WHERE l.id IN " +
           "(SELECT s.lotId FROM LotSubscription s WHERE s.userId = :userId)")
    List<Lot> findSubscribedLotsByUserId(@Param("userId") Long userId);
}
