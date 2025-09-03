package com.example.busreservation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RootController {

    @GetMapping("/")
    public String redirect() {
        return "redirect:/swagger-ui/index.html";
    }
}
