package ru.auctionservice.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class CurrentPriceResponse {

    private Long lotId;
    private BigDecimal currentPrice;
}
