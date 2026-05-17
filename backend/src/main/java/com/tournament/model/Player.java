package com.tournament.model;

public class Player {
    private String id;
    private String name;
    private Integer age;
    private String level;
    private String region;
    private String teamId;
    private Double historicalScore;
    private Boolean isViolated = false;
    private String violationReason;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    public String getTeamId() { return teamId; }
    public void setTeamId(String teamId) { this.teamId = teamId; }
    public Double getHistoricalScore() { return historicalScore; }
    public void setHistoricalScore(Double historicalScore) { this.historicalScore = historicalScore; }
    public Boolean getIsViolated() { return isViolated; }
    public void setIsViolated(Boolean isViolated) { this.isViolated = isViolated; }
    public String getViolationReason() { return violationReason; }
    public void setViolationReason(String violationReason) { this.violationReason = violationReason; }
}
