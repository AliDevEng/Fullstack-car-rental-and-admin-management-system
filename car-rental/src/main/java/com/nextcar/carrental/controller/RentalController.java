package com.nextcar.carrental.controller;

import com.nextcar.carrental.dto.CreateRentalDTO;
import com.nextcar.carrental.dto.RentalResponseDTO;
import com.nextcar.carrental.dto.UpdateRentalStatusDTO;
import com.nextcar.carrental.service.RentalService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rentals")
public class RentalController {

    @Autowired
    private RentalService rentalService;

    // ── Admin: GET /rentals — list all rentals ─────────────────────────────────

    @GetMapping
    public List<RentalResponseDTO> getAllRentals() {
        return rentalService.getAllRentals();
    }

    // ── Customer: GET /rentals/my — own rentals ────────────────────────────────

    @GetMapping("/my")
    public List<RentalResponseDTO> getMyRentals(Authentication auth) {
        return rentalService.getRentalsByCustomerEmail(auth.getName());
    }

    // ── GET /rentals/{id} — single rental (admin sees any; customer sees own) ──

    @GetMapping("/{id}")
    public RentalResponseDTO getRentalById(@PathVariable Integer id, Authentication auth) {
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN"));
        return rentalService.getRentalById(id, auth.getName(), isAdmin);
    }

    // ── POST /rentals — create rental (authenticated customer) ─────────────────

    @PostMapping
    public ResponseEntity<RentalResponseDTO> createRental(
            @Valid @RequestBody CreateRentalDTO dto,
            Authentication auth) {
        RentalResponseDTO created = rentalService.createRental(dto, auth.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ── PUT /rentals/{id}/cancel — cancel PENDING rental ──────────────────────

    @PutMapping("/{id}/cancel")
    public RentalResponseDTO cancelRental(@PathVariable Integer id, Authentication auth) {
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN"));
        return rentalService.cancelRental(id, auth.getName(), isAdmin);
    }

    // ── PUT /rentals/{id}/status — admin: update rental status ────────────────

    @PutMapping("/{id}/status")
    public RentalResponseDTO updateStatus(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateRentalStatusDTO dto) {
        return rentalService.updateStatus(id, dto.getStatus());
    }
}
