package com.armando.task4;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user) {
        userService.registerUser(user);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String showAdminPanel(Model model) {
        model.addAttribute("users", userService.findAllUsers());
        return "admin";
    }

    @PostMapping("/admin/block")
    public String blockUsers(@RequestParam List<Long> userId) {
        userService.blockUsers(userId);
        return "redirect:/admin";
    }

    @PostMapping("/admin/unblock")
    public String unblockUsers(@RequestParam List<Long> userId) {
        userService.unblockUsers(userId);
        return "redirect:/admin";
    }

    @PostMapping("/admin/delete")
    public String deleteUsers(@RequestParam List<Long> userId) {
        userService.deleteUsers(userId);
        return "redirect:/admin";
    }
}