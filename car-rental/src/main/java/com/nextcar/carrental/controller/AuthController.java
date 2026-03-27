package com.nextcar.carrental.controller;

import com.nextcar.carrental.dto.LoginDTO;
import com.nextcar.carrental.dto.LoginResponseDTO;
import com.nextcar.carrental.entity.Admin;
import com.nextcar.carrental.entity.Customer;
import com.nextcar.carrental.repository.AdminRepository;
import com.nextcar.carrental.repository.CustomerRepository;
import com.nextcar.carrental.security.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    // POST /auth/login - Customer login
    @PostMapping("/login")
    public ResponseEntity<?> customerLogin(@RequestBody LoginDTO loginDTO) {
        Optional<Customer> customerOpt = customerRepository.findByEmail(loginDTO.getEmail());

        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            if (passwordEncoder.matches(loginDTO.getPassword(), customer.getPassword())) {
                String token = jwtTokenUtil.generateToken(customer.getEmail(), "CUSTOMER");
                return ResponseEntity.ok(new LoginResponseDTO(token, customer.getEmail(), "CUSTOMER"));
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("status", 401, "error", "Invalid email or password"));
    }

    // POST /auth/admin/login - Admin login
    @PostMapping("/admin/login")
    public ResponseEntity<?> adminLogin(@RequestBody LoginDTO loginDTO) {
        Optional<Admin> adminOpt = adminRepository.findByEmail(loginDTO.getEmail());

        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            if (passwordEncoder.matches(loginDTO.getPassword(), admin.getPassword())) {
                String token = jwtTokenUtil.generateToken(admin.getEmail(), "ADMIN");
                return ResponseEntity.ok(new LoginResponseDTO(token, admin.getEmail(), "ADMIN"));
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("status", 401, "error", "Invalid email or password"));
    }
}
