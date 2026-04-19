package ru.auctionservice.exception;

public class LotNotActiveException extends RuntimeException {

    public LotNotActiveException(Long lotId) {
        super("Lot with id " + lotId + " is not active for bidding");
    }
}
