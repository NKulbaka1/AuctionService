package ru.auctionservice.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.auctionservice.entity.Lot;
import ru.auctionservice.entity.LotStatus;

import java.util.Optional;

public interface LotRepository extends JpaRepository<Lot, Long> {

    Page<Lot> findAllByStatus(LotStatus status, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT l FROM Lot l WHERE l.id = :id")
    Optional<Lot> findByIdForUpdate(@Param("id") Long id);
}
