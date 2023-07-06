package com.example.ticket.entity;

import com.example.ticket.util.exception.TicketSoldOutException;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Getter
@DynamicInsert
@NoArgsConstructor
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id")
    private Long id;
    private String ticketName;
    private Integer ticketMaxCount;
    @ColumnDefault("0")
    private Integer ticketCurrentCount;

    @Builder
    public Ticket(Long id, String ticketName, Integer ticketMaxCount, Integer ticketCurrentCount) {
        this.id = id;
        this.ticketName = ticketName;
        this.ticketMaxCount = ticketMaxCount;
        this.ticketCurrentCount = ticketCurrentCount;
    }

    public void currentCountIncrement() throws TicketSoldOutException {
        if (ticketMaxCount <= ticketCurrentCount) {
            throw new TicketSoldOutException();
        }
        ticketCurrentCount++;
    }
}
