package com.ar.controller;

import java.io.ByteArrayInputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

import com.ar.entity.Menu;
import com.ar.entity.Orders;
import com.ar.entity.UserDetails;
import com.ar.repo.OrderRepository;
import com.ar.service.MenuService;
import com.ar.service.PdfService;

import jakarta.servlet.http.HttpSession;

@Controller
public class OrderController {
	@Autowired
	private PdfService pdfService;
	
	@Autowired
	private MenuService menuService;
	
	@Autowired
	private OrderRepository orderRepository;

	@PostMapping("/confirmOrder")
	public ResponseEntity<InputStreamResource> confirmOrder(@RequestParam int foodId,
	                                                        @RequestParam int quantity,
	                                                        HttpSession session) throws Exception {

	    UserDetails user = (UserDetails) session.getAttribute("user");

	    if (user == null) {
	        return ResponseEntity.status(401).build();
	    }

	    Menu food = menuService.getById(foodId);

	    Orders order = new Orders();
	    order.setFoodName(food.getName());
	    order.setRestaurantName(food.getRestaurantsName());
	    order.setCategory(food.getCategory());
	    order.setPrice(food.getMRP());
	    order.setQuantity(quantity);
	    order.setTotal(food.getMRP() * quantity);
	    order.setCustomerName(user.getUsername());
	    order.setUser(user);

	    orderRepository.save(order);
	    
	    ByteArrayInputStream pdf = pdfService.generatePdf(order);

	    session.removeAttribute("selectedFoodId");

	    HttpHeaders headers = new HttpHeaders();
	    headers.add("Content-Disposition", "attachment; filename=food-bill.pdf");

	    return ResponseEntity.ok()
	            .headers(headers)
	            .contentType(MediaType.APPLICATION_PDF)
	            .body(new InputStreamResource(pdf));
	   
	    
	}
	

}
