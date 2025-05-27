package com.jenkins.platform.service;

import com.jenkins.platform.model.dto.PipelineStageDTO;
import com.offbytwo.jenkins.model.Job;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;



@Slf4j
@Service
public class PipelineService {
    private final JenkinsService jenkinsService;

    public String createPipeline(String name, List<PipelineStageDTO> stages) {
        try {
            String jenkinsfile = generateJenkinsfile(stages);
            return jenkinsService.createAndTriggerJob(name, jenkinsfile, null).getQueueItemUrlPart();
        } catch (IOException e) {
            log.error("Failed to create pipeline", e);
            throw new RuntimeException("创建流水线失败: " + e.getMessage());
        }
    }


    private String generateJenkinsfile(List<PipelineStageDTO> stages) {
        StringBuilder jenkinsfile = new StringBuilder();
        jenkinsfile.append("pipeline {\n")
                   .append("    agent any\n")
                   .append("    stages {\n");

        // 生成每个阶段的代码
        for (PipelineStageDTO stage : stages) {
            jenkinsfile.append(generateStage(stage));
        }

        jenkinsfile.append("    }\n")
                   .append("}\n");

        return jenkinsfile.toString();
    }

    private String getJavaJenkinsfile() {
        return "pipeline {\n" +
               "    agent any\n" +
               "    stages {\n" +
               "        stage('Prepare') {\n" +
               "            steps {\n" +
               "                writeFile file: 'Main.java', text: env.CODE\n" +
               "            }\n" +
               "        }\n" +
               "        stage('Compile') {\n" +
               "            steps {\n" +
               "                sh 'javac Main.java'\n" +
               "            }\n" +
               "        }\n" +
               "        stage('Run') {\n" +
               "            steps {\n" +
               "                sh 'java Main'\n" +
               "            }\n" +
               "        }\n" +
               "    }\n" +
               "}";
    }

    private String generateStage(PipelineStageDTO stage) {
        StringBuilder stageBuilder = new StringBuilder();
        stageBuilder.append("stage('").append(stage.getName()).append("') {\n");
        stageBuilder.append("    steps {\n");
        stageBuilder.append("        ").append(stage.getSteps()).append("\n");
        stageBuilder.append("    }\n");
        stageBuilder.append("}\n");
        return stageBuilder.toString();
    }
}