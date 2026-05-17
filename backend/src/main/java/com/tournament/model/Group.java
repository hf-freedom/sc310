package com.tournament.model;

import java.util.List;

public class Group {
    private String id;
    private String eventId;
    private String name;
    private Integer round;
    private List<String> playerIds;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getRound() { return round; }
    public void setRound(Integer round) { this.round = round; }
    public List<String> getPlayerIds() { return playerIds; }
    public void setPlayerIds(List<String> playerIds) { this.playerIds = playerIds; }
}
