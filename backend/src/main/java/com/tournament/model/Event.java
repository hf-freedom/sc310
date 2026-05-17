package com.tournament.model;

import java.util.List;

public class Event {
    private String id;
    private String name;
    private Integer minAge;
    private Integer maxAge;
    private List<String> allowedLevels;
    private Integer maxSlots;
    private Integer currentSlots = 0;
    private Boolean isRegistrationOpen = true;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getMinAge() { return minAge; }
    public void setMinAge(Integer minAge) { this.minAge = minAge; }
    public Integer getMaxAge() { return maxAge; }
    public void setMaxAge(Integer maxAge) { this.maxAge = maxAge; }
    public List<String> getAllowedLevels() { return allowedLevels; }
    public void setAllowedLevels(List<String> allowedLevels) { this.allowedLevels = allowedLevels; }
    public Integer getMaxSlots() { return maxSlots; }
    public void setMaxSlots(Integer maxSlots) { this.maxSlots = maxSlots; }
    public Integer getCurrentSlots() { return currentSlots; }
    public void setCurrentSlots(Integer currentSlots) { this.currentSlots = currentSlots; }
    public Boolean getIsRegistrationOpen() { return isRegistrationOpen; }
    public void setIsRegistrationOpen(Boolean isRegistrationOpen) { this.isRegistrationOpen = isRegistrationOpen; }
}
