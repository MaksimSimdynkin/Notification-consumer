package org.example.notificationservice.dto;

public class UserEventDto {
    private String email;
    private String eventType;

    public UserEventDto() {
    }

    public UserEventDto(String email, String eventType) {
        this.email = email;
        this.eventType = eventType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
}
