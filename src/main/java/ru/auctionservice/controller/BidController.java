package ru.auctionservice.controller;

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
public class BidController {

    private final BidService bidService;

    @PostMapping("/{lotId}")
    public ResponseEntity<BidResponse> placeBid(
            @PathVariable Long lotId,
            @Valid @RequestBody BidRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bidService.placeBid(lotId, request));
    }

    @GetMapping("/{lotId}/current-price")
    public ResponseEntity<CurrentPriceResponse> getCurrentPrice(@PathVariable Long lotId) {
        return ResponseEntity.ok(bidService.getCurrentPrice(lotId));
    }
}
