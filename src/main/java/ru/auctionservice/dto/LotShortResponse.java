package ru.auctionservice.dto;

import lombok.Builder;
import lombok.Getter;
import ru.auctionservice.entity.LotStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class LotShortResponse {

    private Long id;
    private String title;
    private BigDecimal currentPrice;
    private LotStatus status;
    private String imageUrl;
    private LocalDateTime endsAt;
}
