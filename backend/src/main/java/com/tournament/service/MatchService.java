package com.tournament.service;

import com.tournament.model.*;
import com.tournament.storage.DataStorage;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MatchService {

    public String recordMatchResult(MatchResult result) {
        Player player1 = DataStorage.players.get(result.getPlayer1Id());
        Player player2 = DataStorage.players.get(result.getPlayer2Id());

        if (player1 == null || player2 == null) {
            return "选手不存在";
        }

        if (player1.getIsViolated()) {
            result.setWinnerId(result.getPlayer2Id());
        } else if (player2.getIsViolated()) {
            result.setWinnerId(result.getPlayer1Id());
        } else {
            if (result.getPlayer1Score() > result.getPlayer2Score()) {
                result.setWinnerId(result.getPlayer1Id());
            } else if (result.getPlayer2Score() > result.getPlayer1Score()) {
                result.setWinnerId(result.getPlayer2Id());
            } else {
                return "比分不能平局";
            }
        }

        result.setId("M" + System.currentTimeMillis());
        result.setIsCompleted(true);
        DataStorage.matchResults.put(result.getId(), result);

        return "比赛成绩录入成功";
    }

    public List<MatchResult> getMatchResults(String eventId, String groupId) {
        return DataStorage.matchResults.values().stream()
                .filter(r -> (eventId == null || r.getEventId().equals(eventId))
                        && (groupId == null || r.getGroupId().equals(groupId)))
                .collect(Collectors.toList());
    }

    public String disqualifyPlayer(String playerId, String reason) {
        Player player = DataStorage.players.get(playerId);
        if (player == null) {
            return "选手不存在";
        }

        player.setIsViolated(true);
        player.setViolationReason(reason);

        DataStorage.registrations.values().stream()
                .filter(r -> r.getPlayerId().equals(playerId) && "CONFIRMED".equals(r.getStatus()))
                .forEach(r -> {
                    r.setStatus("DISQUALIFIED");
                    Event event = DataStorage.events.get(r.getEventId());
                    if (event != null) {
                        event.setCurrentSlots(event.getCurrentSlots() - 1);
                    }
                });

        recalculatePromotionResults(playerId);

        return "选手已被取消资格，晋级结果已重新计算";
    }

    private void recalculatePromotionResults(String disqualifiedPlayerId) {
        DataStorage.matchResults.values().stream()
                .filter(MatchResult::getIsCompleted)
                .filter(r -> disqualifiedPlayerId.equals(r.getPlayer1Id()) 
                        || disqualifiedPlayerId.equals(r.getPlayer2Id()))
                .forEach(r -> {
                    if (disqualifiedPlayerId.equals(r.getWinnerId())) {
                        String newWinnerId = disqualifiedPlayerId.equals(r.getPlayer1Id()) 
                                ? r.getPlayer2Id() : r.getPlayer1Id();
                        r.setWinnerId(newWinnerId);
                    }
                });
    }

    public Map<String, Object> getAdvancementInfo(String eventId) {
        List<MatchResult> completedMatches = DataStorage.matchResults.values().stream()
                .filter(r -> r.getEventId().equals(eventId) && r.getIsCompleted())
                .collect(Collectors.toList());

        Map<String, Long> playerWins = completedMatches.stream()
                .map(MatchResult::getWinnerId)
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(w -> w, Collectors.counting()));

        List<Map.Entry<String, Long>> sortedPlayers = new ArrayList<>(playerWins.entrySet());
        sortedPlayers.sort((a, b) -> Long.compare(b.getValue(), a.getValue()));

        int advanceCount = Math.min(8, sortedPlayers.size());
        List<String> advancedPlayers = new ArrayList<>();
        for (int i = 0; i < advanceCount; i++) {
            String playerId = sortedPlayers.get(i).getKey();
            Player player = DataStorage.players.get(playerId);
            if (player != null && !player.getIsViolated()) {
                advancedPlayers.add(playerId);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("advancedPlayers", advancedPlayers);
        result.put("totalCompletedMatches", completedMatches.size());

        return result;
    }
}
