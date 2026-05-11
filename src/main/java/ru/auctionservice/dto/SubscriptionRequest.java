package ru.auctionservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Request to subscribe or unsubscribe from a lot")
public class SubscriptionRequest {

    @NotNull
    @Schema(description = "User ID", example = "42")
    private Long userId;

    @NotNull
    @Schema(description = "Lot ID", example = "7")
    private Long lotId;
}
