package com.jodreports.playground.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Controller
public class WebController {

    private static final String[] SAMPLE_TEMPLATES = {
            "carta-bienvenida.odt",
            "factura-simple.odt"
    };

    private static final String[] SAMPLE_DATA_FILES = {
            "carta-bienvenida.json",
            "factura-simple.json"
    };

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("sampleTemplates", getSampleTemplates());
        model.addAttribute("sampleData", getSampleData());
        return "index";
    }

    private List<String> getSampleTemplates() {
        List<String> templates = new ArrayList<>();
        for (String template : SAMPLE_TEMPLATES) {
            Resource resource = new ClassPathResource("samples/templates/" + template);
            if (resource.exists()) {
                templates.add(template);
            }
        }
        return templates;
    }

    private List<SampleData> getSampleData() {
        List<SampleData> samples = new ArrayList<>();
        for (String dataFile : SAMPLE_DATA_FILES) {
            Resource resource = new ClassPathResource("samples/data/" + dataFile);
            if (resource.exists()) {
                try {
                    String content = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                    samples.add(new SampleData(dataFile, content));
                } catch (IOException e) {
                    // Skip if file cannot be read
                }
            }
        }
        return samples;
    }

    public record SampleData(String name, String content) {}
}
