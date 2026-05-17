package com.tournament.controller;

import com.tournament.dto.ApiResponse;
import com.tournament.model.Group;
import com.tournament.service.GroupingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/groups")
@CrossOrigin(origins = "http://localhost:3007")
public class GroupController {

    @Autowired
    private GroupingService groupingService;

    @PostMapping("/generate")
    public ApiResponse<List<Group>> generateGroups(@RequestBody Map<String, Object> request) {
        String eventId = (String) request.get("eventId");
        int round = request.get("round") != null ? (int) request.get("round") : 1;
        List<Group> groups = groupingService.generateGroups(eventId, round);
        return ApiResponse.success("分组生成成功", groups);
    }

    @GetMapping
    public ApiResponse<List<Group>> getGroups(@RequestParam(required = false) String eventId,
                                               @RequestParam(required = false) Integer round) {
        List<Group> groups = groupingService.getGroupsByEvent(eventId, round);
        return ApiResponse.success(groups);
    }

    @GetMapping("/{eventId}/promoted")
    public ApiResponse<List<String>> getPromotedPlayers(@PathVariable String eventId,
                                                         @RequestParam(defaultValue = "1") int round) {
        List<String> promoted = groupingService.getPromotedPlayers(eventId, round);
        return ApiResponse.success(promoted);
    }
}
