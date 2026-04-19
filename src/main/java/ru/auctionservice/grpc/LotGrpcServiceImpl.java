package ru.auctionservice.grpc;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import ru.auctionservice.dto.LotCreateRequest;
import ru.auctionservice.dto.LotFullResponse;
import ru.auctionservice.dto.LotShortResponse;
import ru.auctionservice.dto.LotUpdateRequest;
import ru.auctionservice.dto.PageResponse;
import ru.auctionservice.exception.LotNotFoundException;
import ru.auctionservice.grpc.proto.CreateLotRequest;
import ru.auctionservice.grpc.proto.DeleteLotRequest;
import ru.auctionservice.grpc.proto.DeleteLotResponse;
import ru.auctionservice.grpc.proto.GetLotRequest;
import ru.auctionservice.grpc.proto.GetLotsRequest;
import ru.auctionservice.grpc.proto.GetLotsResponse;
import ru.auctionservice.grpc.proto.LotFull;
import ru.auctionservice.grpc.proto.LotGrpcServiceGrpc;
import ru.auctionservice.grpc.proto.LotShort;
import ru.auctionservice.grpc.proto.UpdateLotRequest;
import ru.auctionservice.service.LotService;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class LotGrpcServiceImpl extends LotGrpcServiceGrpc.LotGrpcServiceImplBase {

    private final LotService lotService;

    @Override
    public void createLot(CreateLotRequest request, StreamObserver<LotFull> responseObserver) {
        try {
            LotCreateRequest dto = new LotCreateRequest();
            dto.setTitle(request.getTitle());
            dto.setDescription(request.getDescription().isEmpty() ? null : request.getDescription());
            dto.setStartingPrice(new BigDecimal(request.getStartingPrice()));
            dto.setStatus(mapStatusToEntity(request.getStatus()));
            dto.setImageUrl(request.getImageUrl().isEmpty() ? null : request.getImageUrl());
            dto.setSellerId(request.getSellerId());
            dto.setEndsAt(request.getEndsAt().isEmpty() ? null : LocalDateTime.parse(request.getEndsAt()));

            responseObserver.onNext(toProto(lotService.createLot(dto)));
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(mapException(e));
        }
    }

    @Override
    public void getLot(GetLotRequest request, StreamObserver<LotFull> responseObserver) {
        try {
            responseObserver.onNext(toProto(lotService.getLotById(request.getId())));
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(mapException(e));
        }
    }

    @Override
    public void getLots(GetLotsRequest request, StreamObserver<GetLotsResponse> responseObserver) {
        try {
            int pageSize = request.getSize() > 0 ? request.getSize() : 10;
            Sort sort = request.getSort().isEmpty()
                    ? Sort.by("createdAt").descending()
                    : Sort.by(request.getSort());
            Pageable pageable = PageRequest.of(request.getPage(), pageSize, sort);

            ru.auctionservice.entity.LotStatus status =
                    request.getFilterByStatus() ? mapStatusToEntity(request.getStatus()) : null;

            PageResponse<LotShortResponse> page = lotService.getLots(status, pageable);

            GetLotsResponse.Builder builder = GetLotsResponse.newBuilder()
                    .setPage(page.getPage())
                    .setSize(page.getSize())
                    .setTotalElements(page.getTotalElements())
                    .setTotalPages(page.getTotalPages())
                    .setFirst(page.isFirst())
                    .setLast(page.isLast());

            for (LotShortResponse lot : page.getContent()) {
                builder.addContent(toShortProto(lot));
            }

            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(mapException(e));
        }
    }

    @Override
    public void updateLot(UpdateLotRequest request, StreamObserver<LotFull> responseObserver) {
        try {
            LotUpdateRequest dto = new LotUpdateRequest();
            if (request.hasTitle()) dto.setTitle(request.getTitle());
            if (request.hasDescription()) dto.setDescription(request.getDescription());
            if (request.hasStartingPrice()) dto.setStartingPrice(new BigDecimal(request.getStartingPrice()));
            if (request.hasStatus()) dto.setStatus(mapStatusToEntity(request.getStatus()));
            if (request.hasImageUrl()) dto.setImageUrl(request.getImageUrl());
            if (request.hasEndsAt()) dto.setEndsAt(LocalDateTime.parse(request.getEndsAt()));

            responseObserver.onNext(toProto(lotService.updateLot(request.getId(), dto)));
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(mapException(e));
        }
    }

    @Override
    public void deleteLot(DeleteLotRequest request, StreamObserver<DeleteLotResponse> responseObserver) {
        try {
            lotService.deleteLot(request.getId());
            responseObserver.onNext(DeleteLotResponse.newBuilder().build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(mapException(e));
        }
    }

    // ─── Mappers ──────────────────────────────────────────────────────────────

    private LotFull toProto(LotFullResponse dto) {
        return LotFull.newBuilder()
                .setId(dto.getId())
                .setTitle(dto.getTitle())
                .setDescription(dto.getDescription() != null ? dto.getDescription() : "")
                .setStartingPrice(dto.getStartingPrice().toPlainString())
                .setCurrentPrice(dto.getCurrentPrice().toPlainString())
                .setStatus(mapStatusToProto(dto.getStatus()))
                .setImageUrl(dto.getImageUrl() != null ? dto.getImageUrl() : "")
                .setSellerId(dto.getSellerId())
                .setCreatedAt(dto.getCreatedAt() != null ? dto.getCreatedAt().toString() : "")
                .setUpdatedAt(dto.getUpdatedAt() != null ? dto.getUpdatedAt().toString() : "")
                .setEndsAt(dto.getEndsAt() != null ? dto.getEndsAt().toString() : "")
                .build();
    }

    private LotShort toShortProto(LotShortResponse dto) {
        return LotShort.newBuilder()
                .setId(dto.getId())
                .setTitle(dto.getTitle())
                .setCurrentPrice(dto.getCurrentPrice().toPlainString())
                .setStatus(mapStatusToProto(dto.getStatus()))
                .setImageUrl(dto.getImageUrl() != null ? dto.getImageUrl() : "")
                .setEndsAt(dto.getEndsAt() != null ? dto.getEndsAt().toString() : "")
                .build();
    }

    private ru.auctionservice.entity.LotStatus mapStatusToEntity(ru.auctionservice.grpc.proto.LotStatus proto) {
        return switch (proto) {
            case ACTIVE -> ru.auctionservice.entity.LotStatus.ACTIVE;
            case CLOSED -> ru.auctionservice.entity.LotStatus.CLOSED;
            default     -> ru.auctionservice.entity.LotStatus.DRAFT;
        };
    }

    private ru.auctionservice.grpc.proto.LotStatus mapStatusToProto(ru.auctionservice.entity.LotStatus entity) {
        if (entity == null) return ru.auctionservice.grpc.proto.LotStatus.DRAFT;
        return switch (entity) {
            case ACTIVE -> ru.auctionservice.grpc.proto.LotStatus.ACTIVE;
            case CLOSED -> ru.auctionservice.grpc.proto.LotStatus.CLOSED;
            default     -> ru.auctionservice.grpc.proto.LotStatus.DRAFT;
        };
    }

    private io.grpc.StatusRuntimeException mapException(Exception e) {
        if (e instanceof LotNotFoundException) {
            return Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException();
        } else if (e instanceof IllegalArgumentException) {
            return Status.INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException();
        }
        return Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException();
    }
}
