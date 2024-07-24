package com.armando.task4;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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

    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String email = ((UserDetails) principal).getUsername();
            return userService.findByEmail(email);
        }
        return null;
    }


    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }
    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, Model model) {
        try {
            userService.registerUser(user);
            return "redirect:/login";
        } catch (DuplicateUserException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "register";
        }
    }
    @GetMapping("/login")
    public String showLoginForm(@RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "blocked", required = false) String blocked,
                                @RequestParam(value = "logout", required = false) String logout,
                                Model model) {
        if ("inactive".equals(error)) {
            model.addAttribute("errorMessage", "Your account is blocked. Please contact support if you think this is an error");
        }
        return "login";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String showAdminPanel(Model model) {
        User user = getCurrentUser();
        if (user == null || !user.isActive()) {
            return "redirect:/login?error=inactive";
        }
        model.addAttribute("users", userService.findAllUsers());
        return "admin";
    }

    @PostMapping("/admin/block")
    public String blockUsers(@RequestParam(value = "userId", required = false) List<Long> userId, Model model) {
        User user = getCurrentUser();
        if (user == null || !user.isActive()) {
            return "redirect:/login?error=inactive";
        }
        if (userId == null || userId.isEmpty()) {
            model.addAttribute("errorMessage", "No users selected to block.");
            return "redirect:/admin";
        }
        userService.blockUsers(userId);
        return "redirect:/admin";
    }

    @PostMapping("/admin/unblock")
    public String unblockUsers(@RequestParam(value = "userId", required = false) List<Long> userId, Model model) {
        User user = getCurrentUser();
        if (user == null || !user.isActive()) {
            return "redirect:/login?error=inactive";
        }
        if (userId == null || userId.isEmpty()) {
            model.addAttribute("errorMessage", "No users selected to unblock.");
            return "redirect:/admin";
        }
        userService.unblockUsers(userId);
        return "redirect:/admin";
    }

    @PostMapping("/admin/delete")
    public String deleteUsers(@RequestParam(value = "userId", required = false) List<Long> userId, Model model) {
        User user = getCurrentUser();
        if (user == null || !user.isActive()) {
            return "redirect:/login?error=inactive";
        }
        if (userId == null || userId.isEmpty()) {
            model.addAttribute("errorMessage", "No users selected to delete.");
            return "redirect:/admin";
        }
        userService.deleteUsers(userId);
        return "redirect:/admin";
    }
}