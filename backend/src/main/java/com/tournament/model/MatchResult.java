package com.tournament.model;

public class MatchResult {
    private String id;
    private String eventId;
    private String groupId;
    private String player1Id;
    private String player2Id;
    private Integer player1Score;
    private Integer player2Score;
    private String winnerId;
    private Boolean isCompleted = false;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }
    public String getPlayer1Id() { return player1Id; }
    public void setPlayer1Id(String player1Id) { this.player1Id = player1Id; }
    public String getPlayer2Id() { return player2Id; }
    public void setPlayer2Id(String player2Id) { this.player2Id = player2Id; }
    public Integer getPlayer1Score() { return player1Score; }
    public void setPlayer1Score(Integer player1Score) { this.player1Score = player1Score; }
    public Integer getPlayer2Score() { return player2Score; }
    public void setPlayer2Score(Integer player2Score) { this.player2Score = player2Score; }
    public String getWinnerId() { return winnerId; }
    public void setWinnerId(String winnerId) { this.winnerId = winnerId; }
    public Boolean getIsCompleted() { return isCompleted; }
    public void setIsCompleted(Boolean isCompleted) { this.isCompleted = isCompleted; }
}
