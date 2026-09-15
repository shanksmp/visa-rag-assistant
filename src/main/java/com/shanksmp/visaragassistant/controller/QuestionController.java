package com.shanksmp.visaragassistant.controller;

import com.shanksmp.visaragassistant.service.QuestionAnsweringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QuestionController {

    @Autowired
    private QuestionAnsweringService questionAnsweringService;

    @GetMapping("/api/ask")
    public ResponseEntity<String> ask(@RequestParam String question) {
        try {
            String answer = questionAnsweringService.answer(question);
            return ResponseEntity.ok(answer);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to answer: " + e.getMessage());
        }
    }
}