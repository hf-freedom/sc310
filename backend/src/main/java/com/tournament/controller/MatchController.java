package com.tournament.controller;

import com.tournament.dto.ApiResponse;
import com.tournament.model.MatchResult;
import com.tournament.service.MatchService;
import com.tournament.service.ScheduledTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/matches")
@CrossOrigin(origins = "http://localhost:3007")
public class MatchController {

    @Autowired
    private MatchService matchService;

    @Autowired
    private ScheduledTaskService scheduledTaskService;

    @PostMapping("/result")
    public ApiResponse<String> recordResult(@RequestBody MatchResult result) {
        String message = matchService.recordMatchResult(result);
        if (message.contains("成功")) {
            return ApiResponse.success(message, message);
        }
        return ApiResponse.error(message);
    }

    @GetMapping("/results")
    public ApiResponse<List<MatchResult>> getResults(@RequestParam(required = false) String eventId,
                                                      @RequestParam(required = false) String groupId) {
        return ApiResponse.success(matchService.getMatchResults(eventId, groupId));
    }

    @PostMapping("/disqualify")
    public ApiResponse<String> disqualifyPlayer(@RequestBody Map<String, String> request) {
        String playerId = request.get("playerId");
        String reason = request.get("reason");
        String message = matchService.disqualifyPlayer(playerId, reason);
        return ApiResponse.success(message, message);
    }

    @GetMapping("/{eventId}/advancement")
    public ApiResponse<Map<String, Object>> getAdvancementInfo(@PathVariable String eventId) {
        return ApiResponse.success(matchService.getAdvancementInfo(eventId));
    }

    @GetMapping("/warnings")
    public ApiResponse<List<String>> getWarnings() {
        return ApiResponse.success(scheduledTaskService.getCurrentWarnings());
    }
}
