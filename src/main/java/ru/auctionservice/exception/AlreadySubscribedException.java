package ru.auctionservice.exception;

public class AlreadySubscribedException extends RuntimeException {
    public AlreadySubscribedException(Long userId, Long lotId) {
        super("User " + userId + " is already subscribed to lot " + lotId);
    }
}
