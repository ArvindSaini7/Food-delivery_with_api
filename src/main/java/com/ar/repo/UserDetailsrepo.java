package com.ar.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ar.entity.UserDetails;

public interface UserDetailsrepo extends JpaRepository<UserDetails,Integer>  {
	UserDetails findByMobilenoAndPassword(String mobileno, String password);
}
