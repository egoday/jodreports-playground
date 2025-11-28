package com.jodreports.playground.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/samples")
public class SampleController {

    private static final String ODT_MEDIA_TYPE = "application/vnd.oasis.opendocument.text";

    @GetMapping("/templates/{filename}")
    public ResponseEntity<byte[]> getSampleTemplate(@PathVariable String filename) {
        try {
            // Sanitize filename to prevent path traversal
            String sanitizedFilename = sanitizeFilename(filename);
            Resource resource = new ClassPathResource("samples/templates/" + sanitizedFilename);
            
            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }
            
            byte[] content = resource.getInputStream().readAllBytes();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(ODT_MEDIA_TYPE));
            headers.setContentDispositionFormData("attachment", sanitizedFilename);
            
            return new ResponseEntity<>(content, headers, HttpStatus.OK);
            
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/data/{filename}")
    public ResponseEntity<String> getSampleData(@PathVariable String filename) {
        try {
            // Sanitize filename to prevent path traversal
            String sanitizedFilename = sanitizeFilename(filename);
            Resource resource = new ClassPathResource("samples/data/" + sanitizedFilename);
            
            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }
            
            String content = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(content);
            
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String sanitizeFilename(String filename) {
        // Remove any path traversal attempts
        return filename.replaceAll("[^a-zA-Z0-9._-]", "");
    }
}
