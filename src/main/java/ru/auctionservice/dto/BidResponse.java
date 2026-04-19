package ru.auctionservice.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class BidResponse {

    private Long id;
    private Long lotId;
    private Long bidderId;
    private BigDecimal amount;
    private LocalDateTime createdAt;
}
