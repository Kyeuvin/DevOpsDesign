package com.jenkins.platform.config;

import com.offbytwo.jenkins.JenkinsServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class JenkinsConfig {
    @Value("${jenkins.url}")
    private String jenkinsUrl;

    @Value("${jenkins.username}")
    private String jenkinsUsername;

    @Value("${jenkins.token}")
    private String jenkinsToken;

    @Bean
    public JenkinsServer jenkinsServer() throws URISyntaxException {
        return new JenkinsServer(new URI(jenkinsUrl), jenkinsUsername, jenkinsToken);
    }
}