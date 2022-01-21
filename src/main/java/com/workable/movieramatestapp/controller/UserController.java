package com.workable.movieramatestapp.controller;

import com.workable.movieramatestapp.domain.User;
import com.workable.movieramatestapp.service.UserService;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

@Controller
@RequestMapping(value = "/user")
public class UserController {
    private static final String USER_ROLE = "user";

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String toRegisterForm(Model model) {

        boolean userIsLoged = SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof UserDetails;
        User user = new User();
        model.addAttribute("user", user);
        model.addAttribute("logedin",userIsLoged);
        return "register"; //view
    }

    @PostMapping("/register")
    public String registeredForm(@ModelAttribute @Valid User user, BindingResult bindingResult, Model model) {

        boolean userIsLoged = SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof UserDetails;
        if (bindingResult.hasErrors()||userIsLoged) {
            model.addAttribute("logedin",userIsLoged);
            return "register";
        }
        user.setRole(USER_ROLE);
        User u = userService.registerUser(user);
        if (u == null) {
            model.addAttribute("username", "Unavailable username");
            return "register";
        }
        return "newform";
    }

    @GetMapping("/login")
    public String logIn(@ModelAttribute User user, Model model) {
        return "login";
    }


    @RequestMapping("/loginsuccess")
    public void loginSuccess(HttpServletResponse response) {

        try {
            response.sendRedirect("/");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
