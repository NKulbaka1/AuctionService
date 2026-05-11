package ru.auctionservice.exception;

public class CannotUnsubscribeException extends RuntimeException {
    public CannotUnsubscribeException(Long lotId) {
        super("Cannot unsubscribe from own lot " + lotId);
    }
}
