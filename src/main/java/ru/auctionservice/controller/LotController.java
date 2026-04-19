package ru.auctionservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.auctionservice.dto.*;
import ru.auctionservice.entity.LotStatus;
import ru.auctionservice.service.LotService;

@RestController
@RequestMapping("/api/lots")
@RequiredArgsConstructor
public class LotController {

    private final LotService lotService;

    @PostMapping
    public ResponseEntity<LotFullResponse> createLot(@Valid @RequestBody LotCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(lotService.createLot(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LotFullResponse> getLot(@PathVariable Long id) {
        return ResponseEntity.ok(lotService.getLotById(id));
    }

    @GetMapping
    public ResponseEntity<Page<LotShortResponse>> getLots(
            @RequestParam(required = false) LotStatus status,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(lotService.getLots(status, pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LotFullResponse> updateLot(
            @PathVariable Long id,
            @Valid @RequestBody LotUpdateRequest request) {
        return ResponseEntity.ok(lotService.updateLot(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLot(@PathVariable Long id) {
        lotService.deleteLot(id);
        return ResponseEntity.noContent().build();
    }
}
