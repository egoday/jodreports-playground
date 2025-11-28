package com.jodreports.playground.controller;

import com.jodreports.playground.exception.DocumentGenerationException;
import com.jodreports.playground.service.JodReportsService;
import net.sf.jooreports.templates.DocumentTemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class DocumentController {

    private static final Logger logger = LoggerFactory.getLogger(DocumentController.class);
    private static final String ODT_MEDIA_TYPE = "application/vnd.oasis.opendocument.text";

    private final JodReportsService jodReportsService;

    public DocumentController(JodReportsService jodReportsService) {
        this.jodReportsService = jodReportsService;
    }

    /**
     * Generates a document from an uploaded template and JSON data.
     *
     * @param template The ODT template file
     * @param jsonData The JSON string containing the data model
     * @return The generated document as a downloadable file
     */
    @PostMapping("/generate")
    public ResponseEntity<byte[]> generateDocument(
            @RequestParam("template") MultipartFile template,
            @RequestParam("data") String jsonData) {
        
        logger.info("Received request to generate document. Template: {}, Data length: {}", 
                template.getOriginalFilename(), jsonData.length());

        try {
            byte[] templateBytes = template.getBytes();
            
            // Validate template
            if (!jodReportsService.isValidTemplate(templateBytes)) {
                return ResponseEntity.badRequest()
                        .body("Invalid ODT template file".getBytes());
            }

            byte[] generatedDocument = jodReportsService.generateDocument(templateBytes, jsonData);

            String outputFilename = generateOutputFilename(template.getOriginalFilename());
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(ODT_MEDIA_TYPE));
            headers.setContentDispositionFormData("attachment", outputFilename);
            headers.setContentLength(generatedDocument.length);

            logger.info("Document generated successfully: {}", outputFilename);
            
            return new ResponseEntity<>(generatedDocument, headers, HttpStatus.OK);
            
        } catch (IOException | DocumentTemplateException e) {
            logger.error("Error generating document", e);
            throw new DocumentGenerationException("Failed to generate document: " + e.getMessage(), e);
        }
    }

    /**
     * Validates an uploaded template file.
     *
     * @param template The ODT template file to validate
     * @return Validation result
     */
    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateTemplate(
            @RequestParam("template") MultipartFile template) {
        
        logger.info("Validating template: {}", template.getOriginalFilename());

        try {
            byte[] templateBytes = template.getBytes();
            boolean isValid = jodReportsService.isValidTemplate(templateBytes);
            
            return ResponseEntity.ok(Map.of(
                    "valid", isValid,
                    "filename", template.getOriginalFilename(),
                    "size", template.getSize()
            ));
            
        } catch (IOException e) {
            logger.error("Error validating template", e);
            return ResponseEntity.ok(Map.of(
                    "valid", false,
                    "error", e.getMessage()
            ));
        }
    }

    /**
     * Health check endpoint.
     *
     * @return Status information
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "JODReports Playground"
        ));
    }

    private String generateOutputFilename(String originalFilename) {
        if (originalFilename == null || originalFilename.isEmpty()) {
            return "generated-document.odt";
        }
        
        String baseName = originalFilename;
        if (baseName.toLowerCase().endsWith(".odt")) {
            baseName = baseName.substring(0, baseName.length() - 4);
        }
        
        return baseName + "-generated.odt";
    }

    @ExceptionHandler(DocumentGenerationException.class)
    public ResponseEntity<Map<String, String>> handleDocumentGenerationException(DocumentGenerationException e) {
        logger.error("Document generation failed", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
    }
}
