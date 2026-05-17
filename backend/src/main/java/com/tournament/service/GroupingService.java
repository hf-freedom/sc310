package com.tournament.service;

import com.tournament.model.*;
import com.tournament.storage.DataStorage;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GroupingService {

    public List<Group> generateGroups(String eventId, int round) {
        List<Registration> registrations = DataStorage.registrations.values().stream()
                .filter(r -> r.getEventId().equals(eventId) && "CONFIRMED".equals(r.getStatus()))
                .collect(Collectors.toList());

        List<String> playerIds = registrations.stream()
                .map(Registration::getPlayerId)
                .filter(id -> {
                    Player p = DataStorage.players.get(id);
                    return p != null && !p.getIsViolated();
                })
                .collect(Collectors.toList());

        List<Player> players = playerIds.stream()
                .map(DataStorage.players::get)
                .collect(Collectors.toList());

        players.sort((p1, p2) -> {
            int levelCompare = compareLevel(p1.getLevel(), p2.getLevel());
            if (levelCompare != 0) return levelCompare;
            
            if (p1.getHistoricalScore() != null && p2.getHistoricalScore() != null) {
                int scoreCompare = Double.compare(p2.getHistoricalScore(), p1.getHistoricalScore());
                if (scoreCompare != 0) return scoreCompare;
            }
            
            return p1.getRegion().compareTo(p2.getRegion());
        });

        int groupSize = 4;
        int groupCount = (int) Math.ceil((double) players.size() / groupSize);
        List<Group> groups = new ArrayList<>();

        for (int i = 0; i < groupCount; i++) {
            Group group = new Group();
            group.setId("G" + eventId + "_R" + round + "_" + (i + 1));
            group.setEventId(eventId);
            group.setName("第" + (i + 1) + "组");
            group.setRound(round);
            group.setPlayerIds(new ArrayList<>());
            groups.add(group);
        }

        Map<String, List<Player>> teamMap = players.stream()
                .filter(p -> p.getTeamId() != null)
                .collect(Collectors.groupingBy(Player::getTeamId));

        List<Player> remainingPlayers = new ArrayList<>(players);
        int groupIndex = 0;

        for (Map.Entry<String, List<Player>> entry : teamMap.entrySet()) {
            List<Player> teamPlayers = entry.getValue();
            if (teamPlayers.size() >= 2) {
                Set<Integer> usedGroups = new HashSet<>();
                for (Player player : teamPlayers) {
                    int attempts = 0;
                    int targetGroup = groupIndex % groups.size();
                    while (usedGroups.contains(targetGroup) && attempts < groups.size()) {
                        groupIndex++;
                        targetGroup = groupIndex % groups.size();
                        attempts++;
                    }
                    groups.get(targetGroup).getPlayerIds().add(player.getId());
                    usedGroups.add(targetGroup);
                    remainingPlayers.remove(player);
                    groupIndex++;
                }
            }
        }

        groupIndex = 0;
        for (Player player : remainingPlayers) {
            groups.get(groupIndex % groups.size()).getPlayerIds().add(player.getId());
            groupIndex++;
        }

        DataStorage.groups.entrySet().removeIf(e -> e.getValue().getEventId().equals(eventId));
        for (Group group : groups) {
            DataStorage.groups.put(group.getId(), group);
        }

        return groups;
    }

    private int compareLevel(String level1, String level2) {
        List<String> levelOrder = Arrays.asList("一级", "二级", "三级", "四级");
        int index1 = levelOrder.indexOf(level1);
        int index2 = levelOrder.indexOf(level2);
        return Integer.compare(index1, index2);
    }

    public List<Group> getGroupsByEvent(String eventId, Integer round) {
        return DataStorage.groups.values().stream()
                .filter(g -> g.getEventId().equals(eventId) && (round == null || g.getRound().equals(round)))
                .collect(Collectors.toList());
    }

    public List<String> getPromotedPlayers(String eventId, int round) {
        List<MatchResult> results = DataStorage.matchResults.values().stream()
                .filter(r -> r.getEventId().equals(eventId) && r.getIsCompleted())
                .collect(Collectors.toList());

        return results.stream()
                .map(MatchResult::getWinnerId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }
}
