package com.tournament.storage;

import com.tournament.model.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DataStorage {
    public static final Map<String, Player> players = new ConcurrentHashMap<>();
    public static final Map<String, Event> events = new ConcurrentHashMap<>();
    public static final Map<String, Registration> registrations = new ConcurrentHashMap<>();
    public static final Map<String, Group> groups = new ConcurrentHashMap<>();
    public static final Map<String, MatchResult> matchResults = new ConcurrentHashMap<>();
    public static final List<String> warnings = new ArrayList<>();

    static {
        initSampleData();
    }

    private static void initSampleData() {
        Event event1 = new Event();
        event1.setId("E001");
        event1.setName("男子单打甲组");
        event1.setMinAge(18);
        event1.setMaxAge(35);
        event1.setAllowedLevels(Arrays.asList("一级", "二级"));
        event1.setMaxSlots(16);
        events.put(event1.getId(), event1);

        Event event2 = new Event();
        event2.setId("E002");
        event2.setName("男子单打乙组");
        event2.setMinAge(36);
        event2.setMaxAge(50);
        event2.setAllowedLevels(Arrays.asList("二级", "三级"));
        event2.setMaxSlots(16);
        events.put(event2.getId(), event2);

        Event event3 = new Event();
        event3.setId("E003");
        event3.setName("女子单打甲组");
        event3.setMinAge(18);
        event3.setMaxAge(35);
        event3.setAllowedLevels(Arrays.asList("一级", "二级"));
        event3.setMaxSlots(8);
        events.put(event3.getId(), event3);
    }
}
