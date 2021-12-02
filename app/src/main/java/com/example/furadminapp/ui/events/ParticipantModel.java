package com.example.furadminapp.ui.events;

import java.util.List;

public class ParticipantModel {
    private String userId;
    private List<String> eventId;

    public ParticipantModel(String userId, List<String> eventId) {
        this.userId = userId;
        this.eventId = eventId;
    }

    public ParticipantModel() { }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getEventId() {
        return eventId;
    }

    public void setEventId(List<String> eventId) {
        this.eventId = eventId;
    }
}
