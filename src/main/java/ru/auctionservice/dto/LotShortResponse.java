package ru.auctionservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import ru.auctionservice.entity.LotStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "Brief lot information for list view")
public class LotShortResponse {

    @Schema(description = "Lot ID", example = "1")
    private Long id;

    @Schema(description = "Lot title", example = "Vintage Omega Watch")
    private String title;

    @Schema(description = "Current price", example = "2300.00")
    private BigDecimal currentPrice;

    @Schema(description = "Lot status", example = "ACTIVE")
    private LotStatus status;

    @Schema(description = "Preview image URL", example = "https://example.com/images/lot1.jpg")
    private String imageUrl;

    @Schema(description = "Auction end timestamp")
    private LocalDateTime endsAt;
}
