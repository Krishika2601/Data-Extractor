package com.Data.demo.auth.controller;


import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.Data.demo.auth.entity.Customer;
import com.Data.demo.auth.entity.PasswordResetRequest;
import com.Data.demo.auth.repository.CustomerRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.transaction.Transactional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/user")
public class CustomerController {
	@Autowired
	private final CustomerRepository customerRepository;
	@Autowired
	private final BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private AdminController adminController;
	

	public CustomerController(CustomerRepository customerRepository, BCryptPasswordEncoder passwordEncoder) {
		this.customerRepository = customerRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@PostMapping("/login")
	public ResponseEntity<?> customerLogin(@RequestBody Customer customer) {
		Customer storedCustomer = customerRepository.findByEmailIdsContaining(customer.getEmailId());

		if (storedCustomer != null) {
			System.out.println("Input Email: " + customer.getEmailId());
			System.out.println("Stored Email: " + storedCustomer.getEmailId());

			// Fetch the hashed password from the stored customer
			String storedHashedPassword = storedCustomer.getPassword();
			System.out.println("Stored Hashed Password: " + storedHashedPassword);
			System.out.println("Input Hashed Password: " + passwordEncoder.encode(customer.getPassword()));

			// Compare the input password with the stored hashed password
			if (passwordEncoder.matches(customer.getPassword(), storedHashedPassword)) {
				String token = generateToken(storedCustomer);

				// Return token along with user details
				Map<String, Object> response = new HashMap<>();
				response.put("token", token);
				response.put("customerName", storedCustomer.getCustomerName());
				response.put("username", storedCustomer.getUsername());
				response.put("userRole", storedCustomer.getRole());
				response.put("email_id", storedCustomer.getEmailId());
				return ResponseEntity.ok(response);

			} else {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email/password");
			}
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Contact Admin");
		}

	}

	@PostMapping("/reset-password")
	public ResponseEntity<String> resetPassword(@RequestBody PasswordResetRequest resetRequest,
			@RequestHeader("Authorization") String token) {
		if (adminController.isAdminAuthenticated(token) || isCustomerAuthenticated(token)) {
			// Validate the reset request data
			if (resetRequest.getEmail() == null || resetRequest.getCurrentPassword() == null
					|| resetRequest.getNewPassword() == null || resetRequest.getConfirmNewPassword() == null) {
				return ResponseEntity.badRequest().body("All fields are required");
			}

			// Find the customer by email
			Customer customer = customerRepository.findByEmailIdsContaining(resetRequest.getEmail());
			if (customer == null) {
				return ResponseEntity.notFound().build();
			}

			// Check if the current password matches
			if (!passwordEncoder.matches(resetRequest.getCurrentPassword(), customer.getPassword())) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid current password");
			}

			// Check if new password matches confirm new password
			if (!resetRequest.getNewPassword().equals(resetRequest.getConfirmNewPassword())) {
				return ResponseEntity.badRequest().body("New password and confirm new password do not match");
			}

			// Update the password
			customer.setPassword(passwordEncoder.encode(resetRequest.getNewPassword()));
			customerRepository.save(customer);

			return ResponseEntity.ok("Password reset successful");
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

	}

	private String generateToken(Customer customer) {
		// Build JWT token
		@SuppressWarnings("deprecation")
		String token = Jwts.builder().setSubject(customer.getEmailId())
				.claim("customerName", customer.getCustomerName()).claim("firstName", customer.getUsername())
				.claim("userRole", customer.getRole()).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + 86400000)) // Token valid for 1 day
				.signWith(SignatureAlgorithm.HS512,
						"cd8b9a95db1f6e390ff0c31a95bc6ebf5274fc21c5e8acce403474a5f23b9510e2f4ad763cf9d49481e3f514c296d35a6b503bd6e6ffb3242b64ad9a5c6c9a25")
				.compact();
		return token;
	}

	public boolean isCustomerAuthenticated(String token) {
		try {
			// Decode the token to extract userRole
			Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(
					"cd8b9a95db1f6e390ff0c31a95bc6ebf5274fc21c5e8acce403474a5f23b9510e2f4ad763cf9d49481e3f514c296d35a6b503bd6e6ffb3242b64ad9a5c6c9a25")
					.build().parseClaimsJws(token);
			String userRole = (String) claimsJws.getBody().get("userRole");

			// Check if userRole is "ADMIN"
			return "CUSTOMER".equals(userRole);
		} catch (Exception e) {
			e.printStackTrace();
			return false; // Return false if any error occurs
		}
	}

	@Transactional
	@PostMapping("/register")
	public ResponseEntity<String> customerRegister(@RequestBody Customer customer) {
		// Check if the email already exists
		System.out.println("Email id: " + customer.getEmailId());
		Customer existingCustomer = customerRepository.findByEmailIdsContaining(customer.getEmailId());

		if (existingCustomer != null) {
			// Email exists, update username and password
			existingCustomer.setUsername(customer.getUsername());
			existingCustomer.setPassword(passwordEncoder.encode(customer.getPassword()));
			customerRepository.save(existingCustomer);
			return new ResponseEntity<>("Customer Registration Successful (Username and Password Updated)",
					HttpStatus.OK);
		} else {
			// Email doesn't exist, registration fails
			return new ResponseEntity<>("Customer Registration Failed - Email not found in the database",
					HttpStatus.NOT_FOUND);
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
