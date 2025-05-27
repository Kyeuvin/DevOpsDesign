package com.jenkins.platform.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WebSocketService {
    private final SimpMessagingTemplate messagingTemplate;

    public void sendBuildUpdate(String jobName, int buildNumber, String status) {
        BuildUpdate update = new BuildUpdate(jobName, buildNumber, status);
        messagingTemplate.convertAndSend("/topic/builds/" + jobName, update);
    }

    public void sendBuildLog(String jobName, int buildNumber, String log) {
        messagingTemplate.convertAndSend("/topic/logs/" + jobName + "/" + buildNumber, log);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BuildUpdate {
        private String jobName;
        private int buildNumber;
        private String status;
    }
}