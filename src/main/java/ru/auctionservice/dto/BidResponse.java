package ru.auctionservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "Information about a placed bid")
public class BidResponse {

    @Schema(description = "Bid ID", example = "15")
    private Long id;

    @Schema(description = "Lot ID", example = "1")
    private Long lotId;

    @Schema(description = "ID of the user who placed the bid", example = "7")
    private Long bidderId;

    @Schema(description = "Bid amount", example = "2500.00")
    private BigDecimal amount;

    @Schema(description = "Timestamp when the bid was placed")
    private LocalDateTime createdAt;
}
