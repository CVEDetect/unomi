package org.oasis_open.contextserver.api;

import org.oasis_open.contextserver.api.Event;

import java.util.List;

public class EventsCollectorRequest {
    private List<Event> events;

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }
}
