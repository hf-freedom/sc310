package com.tournament.model;

public class Registration {
    private String id;
    private String playerId;
    private String eventId;
    private String status;
    private Boolean isWaitlisted = false;
    private Integer waitlistPosition;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPlayerId() { return playerId; }
    public void setPlayerId(String playerId) { this.playerId = playerId; }
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Boolean getIsWaitlisted() { return isWaitlisted; }
    public void setIsWaitlisted(Boolean isWaitlisted) { this.isWaitlisted = isWaitlisted; }
    public Integer getWaitlistPosition() { return waitlistPosition; }
    public void setWaitlistPosition(Integer waitlistPosition) { this.waitlistPosition = waitlistPosition; }
}
