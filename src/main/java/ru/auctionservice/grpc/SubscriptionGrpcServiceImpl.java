package ru.auctionservice.grpc;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.auctionservice.dto.LotShortResponse;
import ru.auctionservice.exception.AlreadySubscribedException;
import ru.auctionservice.exception.CannotUnsubscribeException;
import ru.auctionservice.exception.LotNotFoundException;
import ru.auctionservice.exception.NotSubscribedException;
import ru.auctionservice.grpc.proto.GetSubscribedLotsRequest;
import ru.auctionservice.grpc.proto.GetSubscribedLotsResponse;
import ru.auctionservice.grpc.proto.LotShort;
import ru.auctionservice.grpc.proto.LotStatus;
import ru.auctionservice.grpc.proto.SubscribeRequest;
import ru.auctionservice.grpc.proto.SubscribeResponse;
import ru.auctionservice.grpc.proto.SubscriptionGrpcServiceGrpc;
import ru.auctionservice.grpc.proto.UnsubscribeRequest;
import ru.auctionservice.grpc.proto.UnsubscribeResponse;
import ru.auctionservice.service.SubscriptionService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SubscriptionGrpcServiceImpl extends SubscriptionGrpcServiceGrpc.SubscriptionGrpcServiceImplBase {

    private final SubscriptionService subscriptionService;

    @Override
    public void getSubscribedLots(GetSubscribedLotsRequest request,
                                  StreamObserver<GetSubscribedLotsResponse> responseObserver) {
        try {
            List<LotShortResponse> lots = subscriptionService.getSubscribedLots(request.getUserId());

            GetSubscribedLotsResponse.Builder builder = GetSubscribedLotsResponse.newBuilder();
            for (LotShortResponse lot : lots) {
                builder.addLots(LotShort.newBuilder()
                        .setId(lot.getId())
                        .setTitle(lot.getTitle())
                        .setCurrentPrice(lot.getCurrentPrice().toPlainString())
                        .setStatus(mapStatus(lot.getStatus()))
                        .setImageUrl(lot.getImageUrl() != null ? lot.getImageUrl() : "")
                        .setEndsAt(lot.getEndsAt() != null ? lot.getEndsAt().toString() : "")
                        .build());
            }

            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(mapException(e));
        }
    }

    @Override
    public void subscribe(SubscribeRequest request, StreamObserver<SubscribeResponse> responseObserver) {
        try {
            subscriptionService.subscribe(request.getUserId(), request.getLotId());
            responseObserver.onNext(SubscribeResponse.newBuilder().build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(mapException(e));
        }
    }

    @Override
    public void unsubscribe(UnsubscribeRequest request, StreamObserver<UnsubscribeResponse> responseObserver) {
        try {
            subscriptionService.unsubscribe(request.getUserId(), request.getLotId());
            responseObserver.onNext(UnsubscribeResponse.newBuilder().build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(mapException(e));
        }
    }

    private LotStatus mapStatus(ru.auctionservice.entity.LotStatus status) {
        return switch (status) {
            case ACTIVE -> LotStatus.ACTIVE;
            case CLOSED -> LotStatus.CLOSED;
            default     -> LotStatus.DRAFT;
        };
    }

    private io.grpc.StatusRuntimeException mapException(Exception e) {
        if (e instanceof LotNotFoundException || e instanceof NotSubscribedException) {
            return Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException();
        } else if (e instanceof AlreadySubscribedException || e instanceof CannotUnsubscribeException) {
            return Status.FAILED_PRECONDITION.withDescription(e.getMessage()).asRuntimeException();
        }
        return Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException();
    }
}
