package ru.auctionservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Schema(description = "Request to place a bid")
public class BidRequest {

    @Schema(description = "ID of the user placing the bid", example = "7", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Bidder ID is required")
    private Long bidderId;

    @Schema(description = "Bid amount (must be greater than the current lot price)", example = "2500.00", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Bid amount is required")
    @DecimalMin(value = "0.01", message = "Bid amount must be greater than 0")
    private BigDecimal amount;
}
