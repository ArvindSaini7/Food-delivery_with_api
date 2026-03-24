package com.ar.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ar.entity.Restaurant;
import com.ar.repo.RestaurantRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

    @Autowired
    private RestaurantRepository restaurantRepo;

    @GetMapping("/loginn")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam String username,
                          @RequestParam String password,
                          HttpSession session,
                          Model model) {

        Restaurant restaurant =
            restaurantRepo.findByUsernameAndPassword(username, password);

        if (restaurant == null) {
            model.addAttribute("error", "Invalid credentials");
            return "login";
        }

        // 🔥 IMPORTANT LINE
        session.setAttribute(
            "restaurantName",
            restaurant.getRestaurantName()
        );

        return "redirect:/list";
    }
    
    
    
    
}

