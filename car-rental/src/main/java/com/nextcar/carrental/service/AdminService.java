package com.nextcar.carrental.service;

import com.nextcar.carrental.dto.AdminStatsDTO;
import com.nextcar.carrental.dto.CreateAdminDTO;
import com.nextcar.carrental.dto.UpdateAdminDTO;
import com.nextcar.carrental.entity.Admin;
import com.nextcar.carrental.repository.AdminRepository;
import com.nextcar.carrental.repository.CarRepository;
import com.nextcar.carrental.repository.CustomerRepository;
import com.nextcar.carrental.repository.PaymentRepository;
import com.nextcar.carrental.repository.RentalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    public AdminStatsDTO getStats() {
        long totalCars = carRepository.count();
        long totalCustomers = customerRepository.count();
        long totalRentals = rentalRepository.count();
        BigDecimal totalRevenue = paymentRepository.getTotalRevenue();
        return new AdminStatsDTO(totalCars, totalCustomers, totalRentals, totalRevenue);
    }

    // ── Admin CRUD ──────────────────────────────────────────────────────────────

    public Admin createAdmin(CreateAdminDTO dto) {
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        if (adminRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email is already registered");
        }

        Admin admin = new Admin();
        admin.setEmail(dto.getEmail());
        admin.setPassword(passwordEncoder.encode(dto.getPassword()));
        admin.setRole(dto.getRole());
        admin.setCreatedAt(LocalDateTime.now());

        return adminRepository.save(admin);
    }

    public Admin updateAdmin(Integer id, UpdateAdminDTO dto) {
        Admin existing = adminRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Admin not found with id: " + id));

        if (!existing.getEmail().equals(dto.getEmail())) {
            if (adminRepository.findByEmail(dto.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Email is already registered");
            }
        }

        existing.setEmail(dto.getEmail());
        existing.setRole(dto.getRole());

        return adminRepository.save(existing);
    }

    public void deleteAdmin(Integer id, String requestorEmail) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Admin not found with id: " + id));

        if (admin.getEmail().equals(requestorEmail)) {
            throw new IllegalArgumentException("You cannot delete your own account");
        }

        adminRepository.deleteById(id);
    }
}
