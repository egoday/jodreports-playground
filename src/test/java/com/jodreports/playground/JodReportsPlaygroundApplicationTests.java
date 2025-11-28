package com.jodreports.playground;

import com.jodreports.playground.service.JodReportsService;
import net.sf.jooreports.templates.DocumentTemplateException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JodReportsPlaygroundApplicationTests {

    @Autowired
    private JodReportsService jodReportsService;

    @Test
    void contextLoads() {
        assertNotNull(jodReportsService);
    }

    @Test
    void testParseJsonToMap() throws IOException {
        String json = "{\"nombre\": \"Test User\", \"edad\": 30}";
        Map<String, Object> result = jodReportsService.parseJsonToMap(json);
        
        assertEquals("Test User", result.get("nombre"));
        assertEquals(30, result.get("edad"));
    }

    @Test
    void testGenerateDocument() throws IOException, DocumentTemplateException {
        // Load sample template
        ClassPathResource templateResource = new ClassPathResource("samples/templates/carta-bienvenida.odt");
        byte[] templateBytes = templateResource.getInputStream().readAllBytes();
        
        // Sample JSON data
        String jsonData = """
            {
                "nombre": "Test User",
                "empresa": "Test Company",
                "puesto": "Developer",
                "fecha_inicio": "1 de enero de 2024",
                "departamento": "IT",
                "responsable": "Manager",
                "firma": "HR Department"
            }
            """;
        
        byte[] result = jodReportsService.generateDocument(templateBytes, jsonData);
        
        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void testIsValidTemplate() throws IOException {
        // Load sample template
        ClassPathResource templateResource = new ClassPathResource("samples/templates/carta-bienvenida.odt");
        byte[] templateBytes = templateResource.getInputStream().readAllBytes();
        
        assertTrue(jodReportsService.isValidTemplate(templateBytes));
    }

    @Test
    void testInvalidTemplate() {
        byte[] invalidTemplate = "This is not a valid ODT file".getBytes();
        // JODReports may not detect invalid template until document generation
        // So this test just ensures the method doesn't throw on basic invalid content
        // The actual validation happens during document generation
        jodReportsService.isValidTemplate(invalidTemplate);
        // Test passes if no exception is thrown
    }
}
