package com.example.ticket.util.exception;

public class TicketSoldOutException extends RuntimeException {
    public TicketSoldOutException() {
        super("티켓이 모두 팔렸습니다.");
    }
}