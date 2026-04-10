package com.nextcar.carrental.controller;

import com.nextcar.carrental.dto.AdminCreateRentalDTO;
import com.nextcar.carrental.dto.AdminStatsDTO;
import com.nextcar.carrental.dto.CreateAdminDTO;
import com.nextcar.carrental.dto.CustomerRegistrationDTO;
import com.nextcar.carrental.dto.RentalResponseDTO;
import com.nextcar.carrental.dto.UpdateAdminDTO;
import com.nextcar.carrental.entity.Admin;
import com.nextcar.carrental.entity.Customer;
import com.nextcar.carrental.service.AdminService;
import com.nextcar.carrental.service.CustomerService;
import com.nextcar.carrental.service.RentalService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private RentalService rentalService;

    // ── Dashboard ──────────────────────────────────────────────────────────────

    // GET /admin - list all admins
    @GetMapping
    public ResponseEntity<List<Admin>> getAllAdmins() {
        return ResponseEntity.ok(adminService.getAllAdmins());
    }

    // GET /admin/stats - dashboard statistics
    @GetMapping("/stats")
    public ResponseEntity<AdminStatsDTO> getStats() {
        return ResponseEntity.ok(adminService.getStats());
    }

    // ── Admin user management ──────────────────────────────────────────────────

    // POST /admin/users - create new admin
    @PostMapping("/users")
    public ResponseEntity<Admin> createAdmin(@Valid @RequestBody CreateAdminDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createAdmin(dto));
    }

    // PUT /admin/users/{id} - update admin email and role
    @PutMapping("/users/{id}")
    public ResponseEntity<Admin> updateAdmin(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateAdminDTO dto) {
        return ResponseEntity.ok(adminService.updateAdmin(id, dto));
    }

    // DELETE /admin/users/{id} - delete admin (cannot delete self)
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable Integer id, Authentication auth) {
        adminService.deleteAdmin(id, auth.getName());
        return ResponseEntity.noContent().build();
    }

    // ── Customer management ────────────────────────────────────────────────────

    // POST /admin/customers - create customer on behalf of walk-in or phone booking
    @PostMapping("/customers")
    public ResponseEntity<Customer> createCustomer(@Valid @RequestBody CustomerRegistrationDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.registerCustomer(dto));
    }

    // GET /admin/customers/{id}/rentals - full booking history for a customer
    @GetMapping("/customers/{id}/rentals")
    public ResponseEntity<List<RentalResponseDTO>> getCustomerRentals(@PathVariable Integer id) {
        return ResponseEntity.ok(rentalService.getRentalsByCustomerId(id));
    }

    // ── Rental management ─────────────────────────────────────────────────────

    // POST /admin/rentals - create rental on behalf of a customer
    @PostMapping("/rentals")
    public ResponseEntity<RentalResponseDTO> createRentalForCustomer(
            @Valid @RequestBody AdminCreateRentalDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(rentalService.createRentalForCustomer(dto));
    }
}
