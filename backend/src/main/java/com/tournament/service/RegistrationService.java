package com.tournament.service;

import com.tournament.model.*;
import com.tournament.storage.DataStorage;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RegistrationService {

    public String registerPlayer(String playerId, String eventId) {
        Player player = DataStorage.players.get(playerId);
        Event event = DataStorage.events.get(eventId);

        if (player == null) {
            return "选手不存在";
        }
        if (event == null) {
            return "赛事不存在";
        }

        if (player.getIsViolated() != null && player.getIsViolated()) {
            return "该选手因违规已被取消资格，无法报名";
        }

        if (!event.getIsRegistrationOpen()) {
            return "该赛事报名已关闭";
        }

        if (player.getAge() < event.getMinAge() || player.getAge() > event.getMaxAge()) {
            return "年龄不符合要求，该赛事要求年龄: " + event.getMinAge() + "-" + event.getMaxAge();
        }

        if (!event.getAllowedLevels().contains(player.getLevel())) {
            return "等级不符合要求，该赛事允许等级: " + String.join(", ", event.getAllowedLevels());
        }

        boolean alreadyRegistered = DataStorage.registrations.values().stream()
                .anyMatch(r -> r.getPlayerId() != null && r.getPlayerId().equals(playerId) 
                        && r.getEventId() != null && r.getEventId().equals(eventId) 
                        && "CONFIRMED".equals(r.getStatus()));
        if (alreadyRegistered) {
            return "该选手已报名此赛事";
        }

        Registration registration = new Registration();
        registration.setId("R" + System.currentTimeMillis());
        registration.setPlayerId(playerId);
        registration.setEventId(eventId);

        if (event.getCurrentSlots() < event.getMaxSlots()) {
            registration.setStatus("CONFIRMED");
            registration.setIsWaitlisted(false);
            event.setCurrentSlots(event.getCurrentSlots() + 1);
        } else {
            registration.setStatus("WAITLISTED");
            registration.setIsWaitlisted(true);
            long waitlistCount = DataStorage.registrations.values().stream()
                    .filter(r -> r.getEventId() != null && r.getEventId().equals(eventId) 
                            && r.getIsWaitlisted() != null && r.getIsWaitlisted())
                    .count();
            registration.setWaitlistPosition((int) waitlistCount + 1);
        }

        DataStorage.registrations.put(registration.getId(), registration);
        return Boolean.TRUE.equals(registration.getIsWaitlisted()) 
                ? "报名成功，已进入候补名单，位置: " + registration.getWaitlistPosition() 
                : "报名成功";
    }

    public String cancelRegistration(String registrationId) {
        Registration registration = DataStorage.registrations.get(registrationId);
        if (registration == null) {
            return "报名记录不存在";
        }

        String eventId = registration.getEventId();
        Event event = DataStorage.events.get(eventId);

        registration.setStatus("CANCELLED");

        if (!Boolean.TRUE.equals(registration.getIsWaitlisted())) {
            event.setCurrentSlots(event.getCurrentSlots() - 1);
            promoteFromWaitlist(eventId);
        } else {
            updateWaitlistPositions(eventId);
        }

        autoRegroup(eventId);
        return "退赛成功，分组已重新调整";
    }

    private void promoteFromWaitlist(String eventId) {
        Event event = DataStorage.events.get(eventId);
        List<Registration> waitlist = DataStorage.registrations.values().stream()
                .filter(r -> r.getEventId() != null && r.getEventId().equals(eventId) 
                        && Boolean.TRUE.equals(r.getIsWaitlisted()) 
                        && "WAITLISTED".equals(r.getStatus()))
                .collect(Collectors.toList());

        for (Registration reg : waitlist) {
            if (event.getCurrentSlots() < event.getMaxSlots()) {
                reg.setIsWaitlisted(false);
                reg.setStatus("CONFIRMED");
                reg.setWaitlistPosition(null);
                event.setCurrentSlots(event.getCurrentSlots() + 1);
            } else {
                break;
            }
        }
        updateWaitlistPositions(eventId);
    }

    private void updateWaitlistPositions(String eventId) {
        List<Registration> waitlist = DataStorage.registrations.values().stream()
                .filter(r -> r.getEventId() != null && r.getEventId().equals(eventId) 
                        && Boolean.TRUE.equals(r.getIsWaitlisted()) 
                        && "WAITLISTED".equals(r.getStatus()))
                .collect(Collectors.toList());

        for (int i = 0; i < waitlist.size(); i++) {
            waitlist.get(i).setWaitlistPosition(i + 1);
        }
    }

    private void autoRegroup(String eventId) {
        List<Group> existingGroups = DataStorage.groups.values().stream()
                .filter(g -> g.getEventId() != null && g.getEventId().equals(eventId))
                .collect(Collectors.toList());

        if (!existingGroups.isEmpty()) {
            GroupingService groupingService = new GroupingService();
            groupingService.generateGroups(eventId, 1);
        }
    }

    public List<Registration> getRegistrationsByEvent(String eventId) {
        return DataStorage.registrations.values().stream()
                .filter(r -> r.getEventId() != null && r.getEventId().equals(eventId))
                .collect(Collectors.toList());
    }

    public List<Registration> getConfirmedRegistrations(String eventId) {
        return DataStorage.registrations.values().stream()
                .filter(r -> r.getEventId() != null && r.getEventId().equals(eventId) 
                        && "CONFIRMED".equals(r.getStatus()))
                .collect(Collectors.toList());
    }
}
