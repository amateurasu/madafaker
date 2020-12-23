package com.viettel.utils.condition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
public class Item {

    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "quantity")
    private int qty;

    @Column(name = "sale")
    private boolean sale;

    @Column(name = "trigger_instant")
    private LocalDateTime instant;

    @Column(name = "available")
    private boolean available;

    @Column(name = "description")
    private String description;

    @Column(name = "trigger_date", db = "DATE(trigger_instant)")
    private LocalDate date;

    @Column(name = "trigger_time", db = "TIME(trigger_instant)")
    private LocalTime time;

    public LocalDate getDate() {
        if (date == null) {
            date = instant.toLocalDate();
        }

        return date;
    }

    public LocalTime getTime() {
        if (time == null) {
            time = instant.toLocalTime();
        }

        return time;
    }
}
