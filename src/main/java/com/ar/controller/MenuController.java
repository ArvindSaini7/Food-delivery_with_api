package com.ar.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import com.ar.entity.Menu;
import com.ar.entity.UserDetails;
import com.ar.service.CloudinaryService;
import com.ar.service.MenuService;

import jakarta.servlet.http.HttpSession;

@Controller
public class MenuController {
	
	@Autowired
	private CloudinaryService cloudinaryService;

    @Autowired
    private MenuService menuService;
    
    @GetMapping("/add")
    public String addItem(Model model) {
        model.addAttribute("menu", new Menu());
        return "addMenu";
    }

    @PostMapping("/addMenu")
    public String saveMenu(@ModelAttribute("menu") Menu menu,
                           @RequestParam("imageFile") MultipartFile imageFile,
                           HttpSession session) {

        try {

            String restaurantName = (String) session.getAttribute("restaurantName");

            if (restaurantName == null) {
                return "redirect:/login";
            }

            menu.setRestaurantsName(restaurantName);

            if (!imageFile.isEmpty()) {
                String imageUrl = cloudinaryService.uploadImage(imageFile);
                menu.setImage(imageUrl);
            }

            menuService.saveMenu(menu);

        } catch (Exception e) {
            e.printStackTrace();   // 🔥 IMPORTANT (console me error dikhega)
            return "redirect:/add?error";
        }

        return "redirect:/list";
    }
    // 👉 MENU LIST (Thymeleaf)
    @GetMapping("/list")
    public String menuList(Model model, HttpSession session) {

        String restaurantName = (String) session.getAttribute("restaurantName");

        model.addAttribute(
                "menuList",
                menuService.getMenuByRestaurant(restaurantName)
        );

        return "menuList";
    }

    // 👉 FRONT PAGE
    @GetMapping("/")
    public String home(HttpSession session, Model model) {

        UserDetails user = (UserDetails) session.getAttribute("user");

        if (user != null) {
            model.addAttribute("username", user.getUsername());
        }

        return "foodlist";   // ✅ ye foodlist.html open karega
    }
    // 👉 API PAGE
    @GetMapping("/Buttonmenu")
    public String menuApiPage() {
        return "NormalApi";
    }
    
    @GetMapping("/MenuButton")
    public String MenuButton() {
        return "MenuButton.html";
    }
    
    @GetMapping("/AboutPage")
    public String AboutPage() {
        return "AboutPage.html";
    }
    
    @GetMapping("/ContactPage")
    public String ContactPage() {
        return "ContactPage.html";
    }
    
    @GetMapping("/editMenu/{id}")
    public String editPage(@PathVariable int id, Model model) {
    	Menu menu =menuService.getById(id);
        model.addAttribute("menu", menu);
        return "EditMenuPage";
    }

    @PostMapping("/updateMenu")
    public String updateMenu(@ModelAttribute Menu menu) {
        menuService.Update(menu);
        return "redirect:/list";
    }

    @GetMapping("/deleteMenu/{id}")
    public String deleteMenu(@PathVariable int id) {
        menuService.delete(id);
        return "redirect:/list";
    }
    
    
    @GetMapping("/food-details")
    public String foodDetails(HttpSession session, Model model) {

        Integer foodId = (Integer) session.getAttribute("selectedFoodId");
        UserDetails user = (UserDetails) session.getAttribute("user");

        if (foodId == null) {
            return "redirect:/";
        }

        if (user == null) {
            return "redirect:/userDetails";
        }

        Menu food = menuService.getById(foodId);

        model.addAttribute("food", food);
        model.addAttribute("username", user.getUsername());

        return "food-details";
    }  
}
