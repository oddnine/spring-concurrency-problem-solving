package com.example.ticket.util.exception;

public class LockException extends RuntimeException {
    public LockException() {
        super("락을 획득하지 못하였습니다.");
    }
}
