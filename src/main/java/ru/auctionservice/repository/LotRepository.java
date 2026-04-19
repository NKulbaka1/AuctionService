package ru.auctionservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.auctionservice.entity.Lot;
import ru.auctionservice.entity.LotStatus;

public interface LotRepository extends JpaRepository<Lot, Long> {

    Page<Lot> findAllByStatus(LotStatus status, Pageable pageable);
}
