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
import ru.auctionservice.dto.BidRequest;
import ru.auctionservice.dto.BidResponse;
import ru.auctionservice.dto.CurrentPriceResponse;
import ru.auctionservice.service.BidService;

@RestController
@RequestMapping("/api/bids")
@RequiredArgsConstructor
@Tag(name = "Bids", description = "Bid operations: place a bid and get the current lot price")
public class BidController {

    private final BidService bidService;

    @Operation(summary = "Place a bid",
            description = "Places a bid on a lot. The lot must be in ACTIVE status " +
                    "and the bid amount must be strictly greater than the current price.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Bid placed successfully",
                    content = @Content(schema = @Schema(implementation = BidResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Lot not found",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "Bid amount does not exceed the current price",
                    content = @Content),
            @ApiResponse(responseCode = "422", description = "Lot is not active for bidding",
                    content = @Content)
    })
    @PostMapping("/{lotId}")
    public ResponseEntity<BidResponse> placeBid(
            @Parameter(description = "Lot ID", required = true) @PathVariable Long lotId,
            @Valid @RequestBody BidRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bidService.placeBid(lotId, request));
    }

    @Operation(summary = "Get current lot price",
            description = "Returns the current price of the specified lot.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Current price retrieved successfully",
                    content = @Content(schema = @Schema(implementation = CurrentPriceResponse.class))),
            @ApiResponse(responseCode = "404", description = "Lot not found",
                    content = @Content)
    })
    @GetMapping("/{lotId}/current-price")
    public ResponseEntity<CurrentPriceResponse> getCurrentPrice(
            @Parameter(description = "Lot ID", required = true) @PathVariable Long lotId) {
        return ResponseEntity.ok(bidService.getCurrentPrice(lotId));
    }
}

