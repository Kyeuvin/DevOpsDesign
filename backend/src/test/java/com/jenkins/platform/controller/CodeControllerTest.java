package com.jenkins.platform.controller;

import com.jenkins.platform.model.dto.CodeSubmissionDTO;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class CodeControllerTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    void submitCode_ShouldReturnSubmissionId() {
        CodeSubmissionDTO submission = new CodeSubmissionDTO();
        submission.setCode("public class Test { }");
        submission.setLanguage("java");

        given()
            .contentType(ContentType.JSON)
            .body(submission)
        .when()
            .post("/api/code/submit")
        .then()
            .statusCode(200)
            .body(not(emptyString()));
    }

    @Test
    void getStatus_ShouldReturnStatus() {
        given()
            .pathParam("submissionId", "test-id")
        .when()
            .get("/api/code/status/{submissionId}")
        .then()
            .statusCode(200)
            .body(not(emptyString()));
    }
}