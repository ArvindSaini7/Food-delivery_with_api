package com.ar.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ar.entity.Menu;
import com.ar.repo.MenuRepo;
@Service
public class MenuService {
	
	private MenuRepo menuRepo;

	public MenuService(MenuRepo menuRepo) {
		super();
		this.menuRepo = menuRepo;
	}
	
	public Menu saveMenu(Menu menu) {
		return menuRepo.save(menu);
		
	}
	
	public List<Menu> getMenuByRestaurant(String restaurantName) {
	    return menuRepo.findByRestaurantsName(restaurantName);
	}
	
	
	public List<Menu> getAllMenu() {
        return menuRepo.findAll();
    }
	
	public Menu Update(Menu menu){
		return menuRepo.save(menu);
	}
	
	public Menu getById(int id) {
        return menuRepo.getById(id);
    }
	
	public void delete(int id) {
		menuRepo.deleteById(id);
	}


}
