package com.Data.demo.auth.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Data.demo.auth.entity.Admin;



@Repository
public interface AdminRepository  extends JpaRepository<Admin, String>{

	Admin findByUsername(String username);

  
}
