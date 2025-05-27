package com.jenkins.platform.controller;

import com.jenkins.platform.model.dto.CodeSubmissionDTO;
import com.jenkins.platform.service.CodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/code")
@RequiredArgsConstructor
public class CodeController {
    private final CodeService codeService;

    @PostMapping("/submit")
    public ResponseEntity<?> submitCode(@Valid @RequestBody CodeSubmissionDTO submission) {
        String jobId = codeService.submitCode(submission);
        return ResponseEntity.ok(jobId);
    }

    @GetMapping("/status/{jobId}")
    public ResponseEntity<String> getJobStatus(@PathVariable String jobId) {
        String status = codeService.getJobStatus(jobId);
        return ResponseEntity.ok(status);
    }
}