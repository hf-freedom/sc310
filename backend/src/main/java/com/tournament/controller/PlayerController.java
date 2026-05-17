package com.tournament.controller;

import com.tournament.dto.ApiResponse;
import com.tournament.model.Player;
import com.tournament.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/players")
@CrossOrigin(origins = "http://localhost:3007")
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    @PostMapping
    public ApiResponse<Player> addPlayer(@RequestBody Player player) {
        Player saved = playerService.addPlayer(player);
        return ApiResponse.success("选手添加成功", saved);
    }

    @GetMapping
    public ApiResponse<List<Player>> getAllPlayers() {
        return ApiResponse.success(playerService.getAllPlayers());
    }

    @GetMapping("/{id}")
    public ApiResponse<Player> getPlayer(@PathVariable String id) {
        Player player = playerService.getPlayer(id);
        if (player == null) {
            return ApiResponse.error("选手不存在");
        }
        return ApiResponse.success(player);
    }

    @PutMapping("/{id}")
    public ApiResponse<Player> updatePlayer(@PathVariable String id, @RequestBody Player player) {
        Player updated = playerService.updatePlayer(id, player);
        if (updated == null) {
            return ApiResponse.error("选手不存在");
        }
        return ApiResponse.success("选手更新成功", updated);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deletePlayer(@PathVariable String id) {
        if (playerService.deletePlayer(id)) {
            return ApiResponse.success("选手删除成功", null);
        }
        return ApiResponse.error("选手不存在");
    }
}
