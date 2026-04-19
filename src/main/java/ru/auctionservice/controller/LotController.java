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
import org.springdoc.core.annotations.ParameterObject;
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
@Tag(name = "Lots", description = "CRUD operations for managing auction lots")
public class LotController {

    private final LotService lotService;

    @Operation(summary = "Create a lot", description = "Creates a new lot. Default status is DRAFT.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Lot created successfully",
                    content = @Content(schema = @Schema(implementation = LotFullResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<LotFullResponse> createLot(@Valid @RequestBody LotCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(lotService.createLot(request));
    }

    @Operation(summary = "Get lot by ID", description = "Returns full information about a lot.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lot found",
                    content = @Content(schema = @Schema(implementation = LotFullResponse.class))),
            @ApiResponse(responseCode = "404", description = "Lot not found",
                    content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<LotFullResponse> getLot(
            @Parameter(description = "Lot ID", required = true) @PathVariable Long id) {
        return ResponseEntity.ok(lotService.getLotById(id));
    }

    @Operation(summary = "Get lots list",
            description = "Returns brief information about lots with pagination. " +
                    "Can be filtered by status. Pagination params: page, size, sort.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lots retrieved successfully",
                    content = @Content(schema = @Schema(implementation = PageResponse.class)))
    })
    @GetMapping
    public ResponseEntity<PageResponse<LotShortResponse>> getLots(
            @Parameter(description = "Filter by lot status (DRAFT, ACTIVE, CLOSED)")
            @RequestParam(required = false) LotStatus status,
            @ParameterObject @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(lotService.getLots(status, pageable));
    }

    @Operation(summary = "Update a lot", description = "Partially updates a lot (only provided fields are changed).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lot updated successfully",
                    content = @Content(schema = @Schema(implementation = LotFullResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Lot not found",
                    content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<LotFullResponse> updateLot(
            @Parameter(description = "Lot ID", required = true) @PathVariable Long id,
            @Valid @RequestBody LotUpdateRequest request) {
        return ResponseEntity.ok(lotService.updateLot(id, request));
    }

    @Operation(summary = "Delete a lot", description = "Deletes a lot by ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Lot deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Lot not found",
                    content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLot(
            @Parameter(description = "Lot ID", required = true) @PathVariable Long id) {
        lotService.deleteLot(id);
        return ResponseEntity.noContent().build();
    }
}

