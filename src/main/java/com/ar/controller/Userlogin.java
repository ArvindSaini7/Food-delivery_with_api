package com.ar.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.ar.entity.UserDetails;
import com.ar.repo.UserDetailsrepo;

import jakarta.servlet.http.HttpSession;

@Controller
public class Userlogin {

    @Autowired
    private UserDetailsrepo repo;

    @GetMapping("/userDetailsLogin")
    public String showLoginPage() {
        return "userDetailsLogin";
    }

    @PostMapping("/userDetailsLogin")
    public String loginUser(@RequestParam String mobileno,
                            @RequestParam String password,
                            HttpSession session,
                            Model model) {

        UserDetails user = repo.findByMobilenoAndPassword(mobileno, password);

        if (user != null) {
            session.setAttribute("user", user);

            Integer selectedFoodId = (Integer) session.getAttribute("selectedFoodId");
            if (selectedFoodId != null) {
                return "redirect:/food-details";
            }

            return "redirect:/";
        } else {
            model.addAttribute("error", "Invalid Mobile Number or Password");
            return "userDetailsLogin";
        }
    }
}