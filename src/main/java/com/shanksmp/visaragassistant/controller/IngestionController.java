package com.shanksmp.visaragassistant.controller;

import com.shanksmp.visaragassistant.service.DocumentIngestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
public class IngestionController {

    @Autowired
    private DocumentIngestionService ingestionService;

    @PostMapping("/api/ingest")
    public ResponseEntity<String> ingest(@RequestParam String filePath, @RequestParam String sourceName) {
        if (!Files.exists(Path.of(filePath))) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("File not found at: " + filePath);
        }

        try {
            int chunkCount = ingestionService.ingestPdf(filePath, sourceName);
            return ResponseEntity.ok("Ingested " + chunkCount + " chunks from " + sourceName);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ingestion failed: " + e.getMessage());
        }
    }
}