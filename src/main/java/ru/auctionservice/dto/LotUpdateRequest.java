package ru.auctionservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import lombok.Getter;
import lombok.Setter;
import ru.auctionservice.entity.LotStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "Request to update a lot. Only the provided fields will be changed.")
public class LotUpdateRequest {

    private String title;

    private String description;

    @DecimalMin(value = "0.01", message = "Starting price must be greater than 0")
    private BigDecimal startingPrice;

    private LotStatus status;

    private String imageUrl;

    private LocalDateTime endsAt;
}
