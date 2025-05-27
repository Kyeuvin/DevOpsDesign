package com.jenkins.platform.service;

import com.jenkins.platform.model.dto.CodeSubmissionDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CodeServiceTest {

    @Mock
    private JenkinsService jenkinsService;

    @InjectMocks
    private CodeService codeService;

    private CodeSubmissionDTO submission;

    @BeforeEach
    void setUp() {
        submission = new CodeSubmissionDTO();
        submission.setCode("public class Test { }");
        submission.setLanguage("java");
    }

    @Test
    void submitCode_ShouldReturnSubmissionId() {
        // Given
        when(jenkinsService.createAndTriggerJob(any(), any(), any()))
            .thenReturn(new QueueReference());

        // When
        String submissionId = codeService.submitCode(submission);

        // Then
        assertNotNull(submissionId);
        assertTrue(submissionId.length() > 0);
    }

    @Test
    void getJobStatus_ShouldReturnCorrectStatus() {
        // Given
        String submissionId = "test-id";
        
        // When
        String status = codeService.getJobStatus(submissionId);
        
        // Then
        assertNotNull(status);
    }
}