package com.example.ticket.dto.ticket.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketRequest {
    private String ticketName;
    private Integer ticketMaxCount;
}
