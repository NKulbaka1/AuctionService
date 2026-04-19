package ru.auctionservice.dto;

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
public class LotCreateRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Starting price is required")
    @DecimalMin(value = "0.01", message = "Starting price must be greater than 0")
    private BigDecimal startingPrice;

    private LotStatus status;

    private String imageUrl;

    @NotNull(message = "Seller ID is required")
    private Long sellerId;

    private LocalDateTime endsAt;
}
