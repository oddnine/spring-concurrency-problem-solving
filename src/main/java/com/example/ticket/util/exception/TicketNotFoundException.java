package com.example.ticket.util.exception;

public class TicketNotFoundException extends RuntimeException {
    public TicketNotFoundException() {
        super("티켓이 존재하지 않습니다.");
    }
}
