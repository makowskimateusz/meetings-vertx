


package com.mak.simple.vertx;

import java.util.concurrent.atomic.AtomicInteger;

public class Meeting {

    //statyczny i niezmienny obiekt klasy AtomicInteger
    private static final AtomicInteger COUNTER = new AtomicInteger();

    //niezmienne id
    private final int id;
    private String description;
    private String date;

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Meeting(String description, String date) {
        this.id = COUNTER.getAndIncrement();
        this.description = description;
        this.date = date;
    }

//komentarz

    public Meeting() {
        this.id = COUNTER.getAndIncrement();
    }

}
