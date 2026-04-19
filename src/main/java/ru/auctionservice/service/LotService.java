package ru.auctionservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.auctionservice.dto.*;
import ru.auctionservice.entity.Lot;
import ru.auctionservice.entity.LotStatus;
import ru.auctionservice.exception.LotNotFoundException;
import ru.auctionservice.repository.LotRepository;

@Service
@RequiredArgsConstructor
public class LotService {

    private final LotRepository lotRepository;

    @Transactional
    public LotFullResponse createLot(LotCreateRequest request) {
        Lot lot = Lot.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .startingPrice(request.getStartingPrice())
                .currentPrice(request.getStartingPrice())
                .status(request.getStatus() != null ? request.getStatus() : LotStatus.DRAFT)
                .imageUrl(request.getImageUrl())
                .sellerId(request.getSellerId())
                .endsAt(request.getEndsAt())
                .build();

        Lot saved = lotRepository.save(lot);
        return toFullResponse(saved);
    }

    @Transactional(readOnly = true)
    public LotFullResponse getLotById(Long id) {
        Lot lot = findById(id);
        return toFullResponse(lot);
    }

    @Transactional(readOnly = true)
    public PageResponse<LotShortResponse> getLots(LotStatus status, Pageable pageable) {
        Page<Lot> lots = (status != null)
                ? lotRepository.findAllByStatus(status, pageable)
                : lotRepository.findAll(pageable);
        return new PageResponse<>(lots.map(this::toShortResponse));
    }

    @Transactional
    public LotFullResponse updateLot(Long id, LotUpdateRequest request) {
        Lot lot = findById(id);

        if (request.getTitle() != null) {
            lot.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            lot.setDescription(request.getDescription());
        }
        if (request.getStartingPrice() != null) {
            lot.setStartingPrice(request.getStartingPrice());
        }
        if (request.getStatus() != null) {
            lot.setStatus(request.getStatus());
        }
        if (request.getImageUrl() != null) {
            lot.setImageUrl(request.getImageUrl());
        }
        if (request.getEndsAt() != null) {
            lot.setEndsAt(request.getEndsAt());
        }

        Lot saved = lotRepository.save(lot);
        return toFullResponse(saved);
    }

    @Transactional
    public void deleteLot(Long id) {
        if (!lotRepository.existsById(id)) {
            throw new LotNotFoundException(id);
        }
        lotRepository.deleteById(id);
    }

    public Lot findById(Long id) {
        return lotRepository.findById(id)
                .orElseThrow(() -> new LotNotFoundException(id));
    }

    private LotFullResponse toFullResponse(Lot lot) {
        return LotFullResponse.builder()
                .id(lot.getId())
                .title(lot.getTitle())
                .description(lot.getDescription())
                .startingPrice(lot.getStartingPrice())
                .currentPrice(lot.getCurrentPrice())
                .status(lot.getStatus())
                .imageUrl(lot.getImageUrl())
                .sellerId(lot.getSellerId())
                .createdAt(lot.getCreatedAt())
                .updatedAt(lot.getUpdatedAt())
                .endsAt(lot.getEndsAt())
                .build();
    }

    private LotShortResponse toShortResponse(Lot lot) {
        return LotShortResponse.builder()
                .id(lot.getId())
                .title(lot.getTitle())
                .currentPrice(lot.getCurrentPrice())
                .status(lot.getStatus())
                .imageUrl(lot.getImageUrl())
                .endsAt(lot.getEndsAt())
                .build();
    }
}
