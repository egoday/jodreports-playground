package com.jodreports.playground.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.sf.jooreports.templates.DocumentTemplate;
import net.sf.jooreports.templates.DocumentTemplateException;
import net.sf.jooreports.templates.DocumentTemplateFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Service
public class JodReportsService {

    private static final Logger logger = LoggerFactory.getLogger(JodReportsService.class);
    
    private final DocumentTemplateFactory templateFactory;
    private final ObjectMapper objectMapper;

    public JodReportsService(ObjectMapper objectMapper) {
        this.templateFactory = new DocumentTemplateFactory();
        this.objectMapper = objectMapper;
    }

    /**
     * Generates a document from an ODT template and JSON data.
     *
     * @param templateBytes The ODT template file as bytes
     * @param jsonData The JSON string containing the data model
     * @return The generated document as bytes
     * @throws IOException If template processing fails
     * @throws DocumentTemplateException If template processing fails
     */
    public byte[] generateDocument(byte[] templateBytes, String jsonData) throws IOException, DocumentTemplateException {
        logger.debug("Generating document from template ({} bytes) with data: {}", templateBytes.length, jsonData);
        
        // Parse JSON data to Map
        Map<String, Object> dataModel = parseJsonToMap(jsonData);
        
        return generateDocument(templateBytes, dataModel);
    }

    /**
     * Generates a document from an ODT template and a data model map.
     *
     * @param templateBytes The ODT template file as bytes
     * @param dataModel The data model as a Map
     * @return The generated document as bytes
     * @throws IOException If template processing fails
     * @throws DocumentTemplateException If template processing fails
     */
    public byte[] generateDocument(byte[] templateBytes, Map<String, Object> dataModel) throws IOException, DocumentTemplateException {
        logger.debug("Processing template with data model: {}", dataModel.keySet());
        
        try (InputStream templateStream = new ByteArrayInputStream(templateBytes);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            
            DocumentTemplate template = templateFactory.getTemplate(templateStream);
            template.createDocument(dataModel, outputStream);
            
            byte[] result = outputStream.toByteArray();
            logger.debug("Document generated successfully ({} bytes)", result.length);
            
            return result;
        }
    }

    /**
     * Parses a JSON string into a Map.
     *
     * @param jsonData The JSON string to parse
     * @return A Map representation of the JSON data
     * @throws IOException If JSON parsing fails
     */
    public Map<String, Object> parseJsonToMap(String jsonData) throws IOException {
        return objectMapper.readValue(jsonData, new TypeReference<>() {});
    }

    /**
     * Validates if the provided bytes represent a valid ODT template.
     *
     * @param templateBytes The template file as bytes
     * @return true if valid, false otherwise
     */
    public boolean isValidTemplate(byte[] templateBytes) {
        try (InputStream templateStream = new ByteArrayInputStream(templateBytes)) {
            templateFactory.getTemplate(templateStream);
            return true;
        } catch (Exception e) {
            logger.warn("Invalid template: {}", e.getMessage());
            return false;
        }
    }
}
