package ru.auctionservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import ru.auctionservice.entity.LotStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "Full information about a lot")
public class LotFullResponse {

    @Schema(description = "Lot ID", example = "1")
    private Long id;

    @Schema(description = "Lot title", example = "Vintage Omega Watch")
    private String title;

    @Schema(description = "Lot description", example = "Swiss mechanical watch from 1965")
    private String description;

    @Schema(description = "Starting price", example = "1500.00")
    private BigDecimal startingPrice;

    @Schema(description = "Current price (latest winning bid)", example = "2300.00")
    private BigDecimal currentPrice;

    @Schema(description = "Lot status", example = "ACTIVE")
    private LotStatus status;

    @Schema(description = "Image URL", example = "https://example.com/images/lot1.jpg")
    private String imageUrl;

    @Schema(description = "Seller ID", example = "42")
    private Long sellerId;

    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;

    @Schema(description = "Auction end timestamp")
    private LocalDateTime endsAt;
}
