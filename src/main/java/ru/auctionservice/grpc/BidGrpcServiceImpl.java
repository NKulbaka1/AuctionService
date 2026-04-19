package ru.auctionservice.grpc;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.auctionservice.dto.BidRequest;
import ru.auctionservice.dto.BidResponse;
import ru.auctionservice.dto.CurrentPriceResponse;
import ru.auctionservice.exception.BidTooLowException;
import ru.auctionservice.exception.LotNotActiveException;
import ru.auctionservice.exception.LotNotFoundException;
import ru.auctionservice.grpc.proto.BidGrpcServiceGrpc;
import ru.auctionservice.grpc.proto.BidProto;
import ru.auctionservice.grpc.proto.CurrentPriceProto;
import ru.auctionservice.grpc.proto.GetCurrentPriceRequest;
import ru.auctionservice.grpc.proto.PlaceBidRequest;
import ru.auctionservice.service.BidService;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class BidGrpcServiceImpl extends BidGrpcServiceGrpc.BidGrpcServiceImplBase {

    private final BidService bidService;

    @Override
    public void placeBid(PlaceBidRequest request, StreamObserver<BidProto> responseObserver) {
        try {
            BidRequest dto = new BidRequest();
            dto.setBidderId(request.getBidderId());
            dto.setAmount(new BigDecimal(request.getAmount()));

            BidResponse response = bidService.placeBid(request.getLotId(), dto);

            responseObserver.onNext(BidProto.newBuilder()
                    .setId(response.getId())
                    .setLotId(response.getLotId())
                    .setBidderId(response.getBidderId())
                    .setAmount(response.getAmount().toPlainString())
                    .setCreatedAt(response.getCreatedAt() != null ? response.getCreatedAt().toString() : "")
                    .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(mapException(e));
        }
    }

    @Override
    public void getCurrentPrice(GetCurrentPriceRequest request, StreamObserver<CurrentPriceProto> responseObserver) {
        try {
            CurrentPriceResponse response = bidService.getCurrentPrice(request.getLotId());

            responseObserver.onNext(CurrentPriceProto.newBuilder()
                    .setLotId(response.getLotId())
                    .setCurrentPrice(response.getCurrentPrice().toPlainString())
                    .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(mapException(e));
        }
    }

    private io.grpc.StatusRuntimeException mapException(Exception e) {
        if (e instanceof LotNotFoundException) {
            return Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException();
        } else if (e instanceof BidTooLowException || e instanceof LotNotActiveException) {
            return Status.FAILED_PRECONDITION.withDescription(e.getMessage()).asRuntimeException();
        } else if (e instanceof IllegalArgumentException) {
            return Status.INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException();
        }
        return Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException();
    }
}
