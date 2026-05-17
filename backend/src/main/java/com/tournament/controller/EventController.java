package com.tournament.controller;

import com.tournament.dto.ApiResponse;
import com.tournament.model.Event;
import com.tournament.model.Registration;
import com.tournament.service.RegistrationService;
import com.tournament.storage.DataStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "http://localhost:3007")
public class EventController {

    @Autowired
    private RegistrationService registrationService;

    @GetMapping
    public ApiResponse<List<Event>> getAllEvents() {
        return ApiResponse.success(List.copyOf(DataStorage.events.values()));
    }

    @GetMapping("/{id}")
    public ApiResponse<Event> getEvent(@PathVariable String id) {
        Event event = DataStorage.events.get(id);
        if (event == null) {
            return ApiResponse.error("赛事不存在");
        }
        return ApiResponse.success(event);
    }

    @PostMapping("/{eventId}/register")
    public ApiResponse<String> register(@PathVariable String eventId, @RequestBody Map<String, String> request) {
        String playerId = request.get("playerId");
        String result = registrationService.registerPlayer(playerId, eventId);
        if (result.contains("成功")) {
            return ApiResponse.success(result, result);
        }
        return ApiResponse.error(result);
    }

    @PostMapping("/{eventId}/cancel-registration")
    public ApiResponse<String> cancelRegistration(@PathVariable String eventId, @RequestBody Map<String, String> request) {
        String registrationId = request.get("registrationId");
        String result = registrationService.cancelRegistration(registrationId);
        if (result.contains("成功")) {
            return ApiResponse.success(result, result);
        }
        return ApiResponse.error(result);
    }

    @GetMapping("/{eventId}/registrations")
    public ApiResponse<List<Registration>> getRegistrations(@PathVariable String eventId) {
        return ApiResponse.success(registrationService.getRegistrationsByEvent(eventId));
    }
}
