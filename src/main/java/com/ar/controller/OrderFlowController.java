package com.ar.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.ar.entity.UserDetails;

import jakarta.servlet.http.HttpSession;
@Controller
public class OrderFlowController {
	
	@GetMapping("/order-food/{id}")
	public String orderFood(@PathVariable int id, HttpSession session) {
		
	    session.setAttribute("selectedFoodId", id);

	    UserDetails user = (UserDetails) session.getAttribute("user");

	    if (user == null) {
	        return "redirect:/userDetails";
	    }

	    return "redirect:/food-details";
	}

}
