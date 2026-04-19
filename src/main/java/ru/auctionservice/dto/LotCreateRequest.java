package ru.auctionservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import ru.auctionservice.entity.LotStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "Request to create a lot")
public class LotCreateRequest {

    @Schema(description = "Lot title", example = "Vintage Omega Watch", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Title is required")
    private String title;

    @Schema(description = "Detailed description of the lot", example = "Swiss mechanical watch from 1965")
    private String description;

    @Schema(description = "Starting price of the lot", example = "1500.00", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Starting price is required")
    @DecimalMin(value = "0.01", message = "Starting price must be greater than 0")
    private BigDecimal startingPrice;

    @Schema(description = "Initial status of the lot. Defaults to DRAFT", example = "DRAFT")
    private LotStatus status;

    @Schema(description = "URL of the lot image", example = "https://example.com/images/lot1.jpg")
    private String imageUrl;

    @Schema(description = "Seller ID", example = "42", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Seller ID is required")
    private Long sellerId;

    @Schema(description = "Auction end date and time", example = "2026-05-01T18:00:00")
    private LocalDateTime endsAt;
}
