package ru.auctionservice.exception;

public class LotNotFoundException extends RuntimeException {

    public LotNotFoundException(Long id) {
        super("Lot not found with id: " + id);
    }
}
