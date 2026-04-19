package ru.auctionservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.auctionservice.entity.Bid;

import java.util.List;

public interface BidRepository extends JpaRepository<Bid, Long> {

    List<Bid> findAllByLotIdOrderByCreatedAtDesc(Long lotId);
}
