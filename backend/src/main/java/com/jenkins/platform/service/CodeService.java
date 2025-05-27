package com.jenkins.platform.service;

import com.jenkins.platform.model.dto.CodeSubmissionDTO;
import com.offbytwo.jenkins.model.JobWithDetails;
import com.offbytwo.jenkins.model.QueueReference;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CodeService {
    private final JenkinsService jenkinsService;
    private final Map<String, String> submissionStatus = new ConcurrentHashMap<>();

    public String submitCode(@Valid CodeSubmissionDTO submission) {
        try {
            // 生成唯一的提交ID
            String submissionId = UUID.randomUUID().toString();
            
            // 创建Jenkins任务
            String jobName = "code-execution-" + submission.getLanguage() + "-" + submissionId;
            
            // 根据不同语言配置不同的Jenkins任务
            String jenkinsfileContent = generateJenkinsfile(submission);
            
            // 创建并触发Jenkins任务
            QueueReference queueReference = jenkinsService.createAndTriggerJob(
                jobName, 
                jenkinsfileContent, 
                Map.of("CODE", submission.getCode())
            );
            
            // 记录提交状态
            submissionStatus.put(submissionId, "SUBMITTED");
            
            return submissionId;
        } catch (IOException e) {
            log.error("Failed to submit code", e);
            throw new RuntimeException("代码提交失败: " + e.getMessage());
        }
    }

    public String getJobStatus(String submissionId) {
        return submissionStatus.getOrDefault(submissionId, "NOT_FOUND");
    }

    private String generateJenkinsfile(CodeSubmissionDTO submission) {
        String jenkinsfileTemplate = switch (submission.getLanguage().toLowerCase()) {
            case "java" -> getJavaJenkinsfile();
            case "python" -> getPythonJenkinsfile();
            case "javascript", "typescript" -> getNodeJenkinsfile();
            default -> throw new IllegalArgumentException("Unsupported language: " + submission.getLanguage());
        };
        
        return jenkinsfileTemplate;
    }

    private String getJavaJenkinsfile() {
        return """
            pipeline {
                agent any
                stages {
                    stage('Prepare') {
                        steps {
                            // 创建源代码文件
                            writeFile file: 'Main.java', text: env.CODE
                        }
                    }
                    stage('Compile') {
                        steps {
                            sh 'javac Main.java'
                        }
                    }
                    stage('Run') {
                        steps {
                            sh 'java Main'
                        }
                    }
                }
            }
            """;
    }

    private String getPythonJenkinsfile() {
        return """
            pipeline {
                agent any
                stages {
                    stage('Prepare') {
                        steps {
                            writeFile file: 'main.py', text: env.CODE
                        }
                    }
                    stage('Run') {
                        steps {
                            sh 'python3 main.py'
                        }
                    }
                }
            }
            """;
    }

    private String getNodeJenkinsfile() {
        return """
            pipeline {
                agent any
                stages {
                    stage('Prepare') {
                        steps {
                            writeFile file: 'index.js', text: env.CODE
                        }
                    }
                    stage('Run') {
                        steps {
                            sh 'node index.js'
                        }
                    }
                }
            }
            """;
    }
}