package com.jenkins.platform.service;

import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.BuildWithDetails;
import com.offbytwo.jenkins.model.Job;
import com.offbytwo.jenkins.model.JobWithDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Optional;
import java.util.stream.Collectors;




@Slf4j
@Service
@RequiredArgsConstructor
public class JobMonitorService {
    private final JenkinsService jenkinsService;
    private final WebSocketService webSocketService;
    private final Map<String, String> lastBuildNumbers = new ConcurrentHashMap<>();

    public void monitorJobs() {
        try {
            List<Job> jobs = jenkinsService.getAllJobs();
            for (Job job : jobs) {
                JobWithDetails details = job.details();
                Build lastBuild = details.getLastBuild();
                if (lastBuild != null) {
                    String buildResult = jenkinsService.getBuildResult(lastBuild);
                    boolean isBuilding = jenkinsService.isBuildInProgress(lastBuild);
                    log.info("Job: {}, Status: {}, Building: {}", 
                            job.getName(), buildResult, isBuilding);
                }
            }
        } catch (IOException e) {
            log.error("监控作业时发生错误", e);
        }
    }

    private void monitorBuild(String jobName, Build build) {
        try {
            BuildWithDetails details = build.details();
            String buildStatus = details.getResult() != null ? details.getResult().toString() : "BUILDING";
            String consoleOutput = details.getConsoleOutputText();

            // 推送状态和日志
            webSocketService.sendBuildUpdate(jobName, build.getNumber(), buildStatus);
            webSocketService.sendBuildLog(jobName, build.getNumber(), consoleOutput);
        } catch (IOException e) {
            log.error("Failed to monitor build: " + jobName + "#" + build.getNumber(), e);
        }
    }
}