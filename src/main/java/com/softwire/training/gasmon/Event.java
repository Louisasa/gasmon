package com.softwire.training.gasmon;

public class Event {
    public String locationId;
    public String eventId;
    public double value;
    public Long timestamp;

    public Event() {

    }

    public Event(String locationId, String eventId, double value, Long timestamp) {
        this.locationId = locationId;
        this.eventId = eventId;
        this.value = value;
        this.timestamp = timestamp;
    }
}
