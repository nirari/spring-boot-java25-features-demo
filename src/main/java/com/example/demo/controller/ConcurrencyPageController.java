package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for serving the Thymeleaf frontend page for concurrency demos.
 */
@Controller
public class ConcurrencyPageController {

    /**
     * Serves the interactive concurrency demo page.
     * Maps to http://localhost:8080/concurrency
     */
    @GetMapping("/concurrency")
    public String concurrencyPage() {
        return "concurrency"; // resolves to src/main/resources/templates/concurrency.html
    }
}
