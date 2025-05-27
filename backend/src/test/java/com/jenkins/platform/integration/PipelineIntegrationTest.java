package com.jenkins.platform.integration;

import com.jenkins.platform.model.dto.PipelineStageDTO;
import com.jenkins.platform.service.PipelineService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class PipelineIntegrationTest {

    @Autowired
    private PipelineService pipelineService;

    @Test
    void createPipeline_ShouldCreateJenkinsJob() {
        // Given
        List<PipelineStageDTO> stages = new ArrayList<>();
        PipelineStageDTO stage = new PipelineStageDTO();
        stage.setName("Build");
        stage.setSteps(List.of(/* 添加测试步骤 */));
        stages.add(stage);

        // When
        String jobId = pipelineService.createPipeline("test-pipeline", stages);

        // Then
        assertNotNull(jobId);
        assertTrue(jobId.length() > 0);
    }
}