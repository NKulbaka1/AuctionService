package ru.auctionservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.auctionservice.dto.LotShortResponse;
import ru.auctionservice.dto.SubscriptionRequest;
import ru.auctionservice.service.SubscriptionService;

import java.util.List;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
@Tag(name = "Subscriptions", description = "Lot subscription management")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @Operation(summary = "Get subscribed lots",
            description = "Returns brief info for all lots the user is subscribed to.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of subscribed lots",
                    content = @Content(schema = @Schema(implementation = LotShortResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<LotShortResponse>> getSubscribedLots(
            @Parameter(description = "User ID", required = true)
            @RequestParam Long userId) {
        return ResponseEntity.ok(subscriptionService.getSubscribedLots(userId));
    }

    @Operation(summary = "Subscribe to a lot",
            description = "Subscribes a user to a lot. Sellers are auto-subscribed on lot creation.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Subscribed successfully"),
            @ApiResponse(responseCode = "404", description = "Lot not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Already subscribed", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Void> subscribe(@Valid @RequestBody SubscriptionRequest request) {
        subscriptionService.subscribe(request.getUserId(), request.getLotId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Unsubscribe from a lot",
            description = "Unsubscribes a user from a lot. Sellers cannot unsubscribe from their own lots.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Unsubscribed successfully"),
            @ApiResponse(responseCode = "404", description = "Lot not found or not subscribed", content = @Content),
            @ApiResponse(responseCode = "409", description = "Cannot unsubscribe from own lot", content = @Content)
    })
    @DeleteMapping
    public ResponseEntity<Void> unsubscribe(@Valid @RequestBody SubscriptionRequest request) {
        subscriptionService.unsubscribe(request.getUserId(), request.getLotId());
        return ResponseEntity.noContent().build();
    }
}
