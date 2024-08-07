package com.Data.demo.auth.controller;

import java.security.Key;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import com.Data.demo.auth.entity.Admin;
import com.Data.demo.auth.entity.Customer;
import com.Data.demo.auth.repository.AdminRepository;
import com.Data.demo.auth.repository.CustomerRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.transaction.Transactional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/admin")
public class AdminController {
	@Autowired
	private final AdminRepository adminRepository;
	@Autowired
	private final BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private final CustomerRepository customerRepository;

	@Autowired
	private RestTemplate restTemplate;


	public AdminController(AdminRepository adminRepository, BCryptPasswordEncoder passwordEncoder,
			CustomerRepository customerRepository) {
		this.adminRepository = adminRepository;
		this.passwordEncoder = passwordEncoder;
		this.customerRepository = customerRepository;
	}

	// Method to check if the user is authenticated as an admin
	public boolean isAdminAuthenticated(String token) {
		try {
			// Decode the token to extract userRole
			Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(
					"cd8b9a95db1f6e390ff0c31a95bc6ebf5274fc21c5e8acce403474a5f23b9510e2f4ad763cf9d49481e3f514c296d35a6b503bd6e6ffb3242b64ad9a5c6c9a25")
					.build().parseClaimsJws(token);
			String userRole = (String) claimsJws.getBody().get("userRole");

			// Check if userRole is "ADMIN"
			return "ADMIN".equals(userRole);
		} catch (Exception e) {
			e.printStackTrace();
			return false; // Return false if any error occurs
		}
	}

	@PostMapping("/login")
	public ResponseEntity<?> adminLogin(@RequestBody Admin admin) {
		Admin storedAdmin = adminRepository.findByUsername(admin.getUsername());

		if (storedAdmin != null) {
			System.out.println("Input username: " + admin.getUsername());
			System.out.println("Stored username: " + storedAdmin.getUsername());

			// Fetch the hashed password from the stored customer
			String storedHashedPassword = storedAdmin.getPassword();
			System.out.println("Stored Hashed Password: " + storedHashedPassword);
			System.out.println("Input Hashed Password: " + passwordEncoder.encode(admin.getPassword()));
			if (passwordEncoder.matches(admin.getPassword(), storedHashedPassword)) {
				String token = generateToken(storedAdmin);

				// Return token along with user details
				Map<String, Object> response = new HashMap<>();
				response.put("token", token);
				response.put("username", storedAdmin.getUsername());
				response.put("userRole", storedAdmin.getRole());
				return ResponseEntity.ok(response);

			} else {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Username/password");
			}
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Contact Admin");
		}

	}

	private String generateToken(Admin admin) {
		// Build JWT token
		@SuppressWarnings("deprecation")
		String token = Jwts.builder().setSubject(admin.getUsername()).claim("AdminName", admin.getUsername())

				.claim("userRole", admin.getRole()).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + 86400000)) // Token valid for 1 day
				.signWith(SignatureAlgorithm.HS512,
						"cd8b9a95db1f6e390ff0c31a95bc6ebf5274fc21c5e8acce403474a5f23b9510e2f4ad763cf9d49481e3f514c296d35a6b503bd6e6ffb3242b64ad9a5c6c9a25")
				.compact();
		return token;
	}

	@GetMapping("/getAllCustomers")
	public ResponseEntity<?> getAllCustomers(@RequestHeader("Authorization") String token) {
		if (!isAdminAuthenticated(token)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
		}

		List<Customer> allCustomers = customerRepository.findAll();
		return ResponseEntity.ok(allCustomers);
	}

	@PostMapping("/addCustomer")
	public ResponseEntity<?> addCustomer(@RequestBody Customer customer, @RequestHeader("Authorization") String token) {
		if (!isAdminAuthenticated(token)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
		}

	
		customer.setUsername(null);

		String[] emails = customer.getEmailIds().split(",");

		if (emails.length == 1) {
			
			customer.setEmailId(emails[0].trim());
		} else {
			
			customer.setEmailId(emails[0].trim());
		}
		

		String defaultPassword = customer.getEmailId().split("@")[0] + "123";
		customer.setPassword(passwordEncoder.encode(defaultPassword));

		// Save the customer
		customerRepository.save(customer);
		return ResponseEntity.ok("Customer Added Successfully by Admin");
	}

	@Transactional
	@PutMapping("/updateCustomer")
	public ResponseEntity<?> updateCustomer(@RequestBody Customer updatedCustomer,
			@RequestHeader("Authorization") String token) {
		if (!isAdminAuthenticated(token)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
		}

		// Extract the first email from the emailIds field
		String[] emails = updatedCustomer.getEmailIds().split(",");
		String firstEmail = emails[0].trim();

		// Update the existing customer with the new values
		int updatedRows = customerRepository.updateCustomer(updatedCustomer.getCustomerName(),
				updatedCustomer.getStatus(), updatedCustomer.getAddressLine1(), updatedCustomer.getAddressLine2(),
				updatedCustomer.getCity(), updatedCustomer.getState(), updatedCustomer.getDeviceIds(),
				updatedCustomer.getDateOfRegistration(), updatedCustomer.getEmailIds(), firstEmail);

		if (updatedRows > 0) {
			return ResponseEntity.ok("Customer updated successfully by Admin");
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer with email " + firstEmail + " not found");
		}
	}

	@DeleteMapping("/deleteCustomer")
	public ResponseEntity<?> deleteCustomer(@RequestParam String email, @RequestHeader("Authorization") String token) {
		if (!isAdminAuthenticated(token)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
		}

		// Find the customer by email
		Customer existingCustomer = customerRepository.findByEmailIdsContaining(email);

		if (existingCustomer != null) {
			// Delete the customer
			customerRepository.delete(existingCustomer);
			return ResponseEntity.ok("Customer with email " + email + " deleted successfully by Admin");
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer with email " + email + " not found");
		}
	}

	public static String timestamps() {

		ZoneId singaporeZone = ZoneId.of("Asia/Singapore");
		ZonedDateTime currentDateTime = ZonedDateTime.now(singaporeZone);
		Instant instant = currentDateTime.toInstant();
		long currentTimestampMillis = instant.toEpochMilli();
		String currentTimestampString = Long.toString(currentTimestampMillis);

		return currentTimestampString;
	}



}