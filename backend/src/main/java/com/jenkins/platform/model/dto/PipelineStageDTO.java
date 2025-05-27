package com.jenkins.platform.model.dto;

import lombok.Data;
import java.util.List;

@Data
public class PipelineStageDTO {
    private String name;
    private String id;
    private List<PipelineStepDTO> steps;
}