package ru.auctionservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@Schema(description = "Current price of a lot")
public class CurrentPriceResponse {

    @Schema(description = "Lot ID", example = "1")
    private Long lotId;

    @Schema(description = "Current actual price", example = "2500.00")
    private BigDecimal currentPrice;
}
