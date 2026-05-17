package com.tournament.service;

import com.tournament.model.Player;
import com.tournament.storage.DataStorage;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlayerService {

    public Player addPlayer(Player player) {
        player.setId("P" + System.currentTimeMillis());
        if (player.getIsViolated() == null) {
            player.setIsViolated(false);
        }
        DataStorage.players.put(player.getId(), player);
        return player;
    }

    public Player getPlayer(String id) {
        return DataStorage.players.get(id);
    }

    public List<Player> getAllPlayers() {
        return DataStorage.players.values().stream().collect(Collectors.toList());
    }

    public Player updatePlayer(String id, Player player) {
        if (!DataStorage.players.containsKey(id)) {
            return null;
        }
        player.setId(id);
        DataStorage.players.put(id, player);
        return player;
    }

    public boolean deletePlayer(String id) {
        return DataStorage.players.remove(id) != null;
    }
}
