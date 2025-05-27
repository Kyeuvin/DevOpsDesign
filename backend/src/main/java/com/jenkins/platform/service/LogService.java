package com.jenkins.platform.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;


@Service
@RequiredArgsConstructor
public class LogService {
    private final SimpMessagingTemplate messagingTemplate;

    public void pushLog(String submissionId, String log) {
        messagingTemplate.convertAndSend(
            "/topic/logs/" + submissionId,
            log
        );
    }
}