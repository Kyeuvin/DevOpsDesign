package com.jenkins.platform.service;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.BuildResult;
import com.offbytwo.jenkins.model.Job;
import com.offbytwo.jenkins.model.JobWithDetails;
import com.offbytwo.jenkins.model.QueueReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
@RequiredArgsConstructor
public class JenkinsService {
    private final JenkinsServer jenkinsServer;

    public QueueReference createAndTriggerJob(String jobName, String jenkinsfileContent, Map<String, String> parameters)
            throws IOException {
        // 创建配置XML
        String configXml = generateConfigXml(jenkinsfileContent);

        // 创建或更新Jenkins任务
        if (jenkinsServer.getJob(jobName) != null) {
            jenkinsServer.updateJob(jobName, configXml);
        } else {
            jenkinsServer.createJob(jobName, configXml);
        }

        // 获取任务并构建
        JobWithDetails job = jenkinsServer.getJob(jobName);
        return job.build(parameters);
    }

    private String generateConfigXml(String jenkinsfileContent) {
        return """
            <?xml version='1.1' encoding='UTF-8'?>
            <flow-definition plugin="workflow-job@1316.vd2290d3341a_f">
                <description></description>
                <keepDependencies>false</keepDependencies>
                <properties/>
                <definition class="org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition" plugin="workflow-cps@3697.vb_470e454c232">
                    <script>%s</script>
                    <sandbox>true</sandbox>
                </definition>
                <triggers/>
                <disabled>false</disabled>
            </flow-definition>
            """.formatted(jenkinsfileContent);
    }

    public String getJobStatus(String jobName) throws IOException {
        Job job = jenkinsServer.getJob(jobName);
        if (job == null) {
            return "NOT_FOUND";
        }

        JobWithDetails jobDetails = job.details();
        if (jobDetails.isInQueue()) {
            return "IN_QUEUE";
        }

        if (jobDetails.isBuilding()) {
            return "BUILDING";
        }

        return jobDetails.getLastBuild().getResult().name();
    }

    public boolean isJobBuilding(String jobName) throws IOException {
        try {
            JobWithDetails jobDetails = jenkinsServer.getJob(jobName);
            if (jobDetails != null) {
                Build lastBuild = jobDetails.getLastBuild();
                if (lastBuild != null) {
                    BuildWithDetails details = lastBuild.details();
                    // 检查构建状态
                    return details != null && 
                           "IN_PROGRESS".equals(details.getDisplayName()) || 
                           details.getDuration() == 0;
                }
            }
            return false;
        } catch (Exception e) {
            log.error("检查作业状态时发生错误: {}", e.getMessage());
            return false;
        }
    }

    public String getBuildResult(Build build) throws IOException {
        try {
            if (build != null) {
                BuildWithDetails details = build.details();
                return Optional.ofNullable(details)
                        .map(d -> d.getDisplayName())
                        .orElse("UNKNOWN");
            }
            return "NOT_FOUND";
        } catch (Exception e) {
            log.error("获取构建结果时发生错误: {}", e.getMessage());
            return "ERROR";
        }
    }

    public boolean isBuildInProgress(Build build) throws IOException {
        try {
            if (build != null) {
                BuildWithDetails details = build.details();
                // 通过构建持续时间判断是否在进行中
                return details != null && details.getDuration() == 0;
            }
            return false;
        } catch (Exception e) {
            log.error("检查构建状态时发生错误: {}", e.getMessage());
            return false;
        }
    }

    public List<Job> getAllJobs() throws IOException {
        return jenkinsServer.getJobs().values()
                .stream()
                .map(job -> (Job) job)
                .collect(Collectors.toList());
    }
}