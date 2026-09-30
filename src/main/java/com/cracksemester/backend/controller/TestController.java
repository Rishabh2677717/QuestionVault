package com.cracksemester.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/")
    public String home() {
        return "CrackSemester Backend Running 🚀";
    }

    @GetMapping("/api/test")
    public String test() {
        return "API Working Successfully ✅";
    }
}