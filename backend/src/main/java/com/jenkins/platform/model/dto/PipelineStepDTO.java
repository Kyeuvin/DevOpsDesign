package com.jenkins.platform.model.dto;

import lombok.Data;
import java.util.Map;

@Data
public class PipelineStepDTO {
    private String name;
    private String type;
    private Map<String, String> parameters;
}