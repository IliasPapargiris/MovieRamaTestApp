package com.workable.movieramatestapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/error")
public class ErrorController {


    @GetMapping
    public String viewHomePage(Model model) {
        return "error";
    }
}
