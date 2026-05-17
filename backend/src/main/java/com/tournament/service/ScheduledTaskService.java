package com.tournament.service;

import com.tournament.storage.DataStorage;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScheduledTaskService {

    public List<String> getCurrentWarnings() {
        return DataStorage.warnings;
    }
}
