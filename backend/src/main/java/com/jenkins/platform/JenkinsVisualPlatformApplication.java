package com.jenkins.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class JenkinsVisualPlatformApplication {
    public static void main(String[] args) {
        SpringApplication.run(JenkinsVisualPlatformApplication.class, args);
    }
}