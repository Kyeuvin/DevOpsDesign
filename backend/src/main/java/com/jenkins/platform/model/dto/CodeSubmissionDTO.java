package com.jenkins.platform.model.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class CodeSubmissionDTO {
    private String code;
    private String language;
}