package com.example.ticket.dto.ticket.response;

import com.example.ticket.entity.Ticket;
import lombok.Builder;
import lombok.Data;

@Data
public class TicketResponse {
    private Long id;
    private String ticketName;
    private Integer ticketMaxCount;
    private Integer ticketCurrentCount;

    @Builder
    public TicketResponse(Long id, String ticketName, Integer ticketMaxCount, Integer ticketCurrentCount) {
        this.id = id;
        this.ticketName = ticketName;
        this.ticketMaxCount = ticketMaxCount;
        this.ticketCurrentCount = ticketCurrentCount;
    }

    public static TicketResponse from(Ticket entity) {
        if (entity == null)
            return null;
        return new TicketResponse(entity.getId(), entity.getTicketName(), entity.getTicketMaxCount(), entity.getTicketCurrentCount());
    }
}
