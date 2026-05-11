package ru.auctionservice.exception;

public class NotSubscribedException extends RuntimeException {
    public NotSubscribedException(Long userId, Long lotId) {
        super("User " + userId + " is not subscribed to lot " + lotId);
    }
}
