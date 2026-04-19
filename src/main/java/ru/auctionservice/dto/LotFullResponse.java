package ru.auctionservice.dto;

import lombok.Builder;
import lombok.Getter;
import ru.auctionservice.entity.LotStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class LotFullResponse {

    private Long id;
    private String title;
    private String description;
    private BigDecimal startingPrice;
    private BigDecimal currentPrice;
    private LotStatus status;
    private String imageUrl;
    private Long sellerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime endsAt;
}
