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
import java.util.Set;

@RestController
@RequestMapping("/api/samples")
public class SampleController {

    private static final String ODT_MEDIA_TYPE = "application/vnd.oasis.opendocument.text";
    
    // Whitelist of allowed sample template files
    private static final Set<String> ALLOWED_TEMPLATES = Set.of(
            "carta-bienvenida.odt",
            "factura-simple.odt"
    );
    
    // Whitelist of allowed sample data files
    private static final Set<String> ALLOWED_DATA_FILES = Set.of(
            "carta-bienvenida.json",
            "factura-simple.json"
    );

    @GetMapping("/templates/{filename}")
    public ResponseEntity<byte[]> getSampleTemplate(@PathVariable String filename) {
        // Validate filename against whitelist
        if (!ALLOWED_TEMPLATES.contains(filename)) {
            return ResponseEntity.notFound().build();
        }
        
        try {
            Resource resource = new ClassPathResource("samples/templates/" + filename);
            
            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }
            
            byte[] content = resource.getInputStream().readAllBytes();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(ODT_MEDIA_TYPE));
            headers.setContentDispositionFormData("attachment", filename);
            
            return new ResponseEntity<>(content, headers, HttpStatus.OK);
            
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/data/{filename}")
    public ResponseEntity<String> getSampleData(@PathVariable String filename) {
        // Validate filename against whitelist
        if (!ALLOWED_DATA_FILES.contains(filename)) {
            return ResponseEntity.notFound().build();
        }
        
        try {
            Resource resource = new ClassPathResource("samples/data/" + filename);
            
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
}
