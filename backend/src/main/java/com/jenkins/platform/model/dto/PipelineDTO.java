package com.jenkins.platform.model.dto;

import lombok.Data;
import java.util.List;

@Data
public class PipelineDTO {
    private List<Stage> stages;

    @Data
    public static class Stage {
        private String name;
        private List<String> steps;
    }
}