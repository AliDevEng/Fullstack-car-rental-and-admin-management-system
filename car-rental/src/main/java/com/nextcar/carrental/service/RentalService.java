package com.nextcar.carrental.service;

import com.nextcar.carrental.dto.CreateRentalDTO;
import com.nextcar.carrental.dto.RentalResponseDTO;
import com.nextcar.carrental.entity.Car;
import com.nextcar.carrental.entity.Customer;
import com.nextcar.carrental.entity.Rental;
import com.nextcar.carrental.repository.CarRepository;
import com.nextcar.carrental.repository.CustomerRepository;
import com.nextcar.carrental.repository.RentalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RentalService {

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private CustomerRepository customerRepository;

    // ── Admin: list all rentals ────────────────────────────────────────────────

    public List<RentalResponseDTO> getAllRentals() {
        return rentalRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ── Customer: list own rentals ─────────────────────────────────────────────

    public List<RentalResponseDTO> getRentalsByCustomerEmail(String email) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("Customer not found"));
        return rentalRepository.findByCustomer_Id(customer.getId())
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ── Get single rental (admin sees any; customer sees own) ──────────────────

    public RentalResponseDTO getRentalById(Integer id, String requestorEmail, boolean isAdmin) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Rental not found with id: " + id));

        if (!isAdmin && !rental.getCustomer().getEmail().equals(requestorEmail)) {
            throw new IllegalArgumentException("Access denied");
        }
        return toDTO(rental);
    }

    // ── Create rental ──────────────────────────────────────────────────────────

    public RentalResponseDTO createRental(CreateRentalDTO dto, String customerEmail) {
        // Validate date order
        if (!dto.getStartDate().isBefore(dto.getEndDate())) {
            throw new IllegalArgumentException("End date must be after start date");
        }

        Customer customer = customerRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new NoSuchElementException("Customer not found"));

        Car car = carRepository.findById(dto.getCarId())
                .orElseThrow(() -> new NoSuchElementException("Car not found with id: " + dto.getCarId()));

        if (!"Available".equalsIgnoreCase(car.getStatus())) {
            throw new IllegalArgumentException("Car is not available for booking");
        }

        LocalDateTime startDT = dto.getStartDate().atStartOfDay();
        LocalDateTime endDT   = dto.getEndDate().atStartOfDay();

        List<?> conflicts = rentalRepository.findConflictingRentals(car.getId(), startDT, endDT);
        if (!conflicts.isEmpty()) {
            throw new IllegalArgumentException(
                "Car is already booked for the selected dates"
            );
        }

        Rental rental = new Rental();
        rental.setCustomer(customer);
        rental.setCar(car);
        rental.setStartDate(startDT);
        rental.setEndDate(endDT);
        rental.setRentalDate(LocalDateTime.now());
        rental.setBookingNumber(generateBookingNumber());
        rental.setStatus("PENDING");
        rental.setCreatedAt(LocalDateTime.now());

        return toDTO(rentalRepository.save(rental));
    }

    // ── Cancel rental (PENDING → CANCELLED) ───────────────────────────────────

    public RentalResponseDTO cancelRental(Integer id, String requestorEmail, boolean isAdmin) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Rental not found with id: " + id));

        if (!isAdmin && !rental.getCustomer().getEmail().equals(requestorEmail)) {
            throw new IllegalArgumentException("Access denied");
        }

        if (!"PENDING".equals(rental.getStatus())) {
            throw new IllegalArgumentException(
                "Only PENDING rentals can be cancelled (current status: " + rental.getStatus() + ")"
            );
        }

        rental.setStatus("CANCELLED");
        return toDTO(rentalRepository.save(rental));
    }

    // ── Admin: update rental status ────────────────────────────────────────────

    public RentalResponseDTO updateStatus(Integer id, String newStatus) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Rental not found with id: " + id));

        rental.setStatus(newStatus);
        return toDTO(rentalRepository.save(rental));
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private String generateBookingNumber() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uniquePart = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return "BK-" + datePart + "-" + uniquePart;
    }

    private RentalResponseDTO toDTO(Rental rental) {
        RentalResponseDTO dto = new RentalResponseDTO();
        dto.setId(rental.getId());
        dto.setBookingNumber(rental.getBookingNumber());
        dto.setStatus(rental.getStatus());
        dto.setStartDate(rental.getStartDate());
        dto.setEndDate(rental.getEndDate());
        dto.setRentalDate(rental.getRentalDate());
        dto.setCreatedAt(rental.getCreatedAt());

        Car car = rental.getCar();
        dto.setCarId(car.getId());
        dto.setCarBrand(car.getBrand());
        dto.setCarModel(car.getModel());
        dto.setCarYear(car.getYear());
        dto.setCarRegNr(car.getRegNr());
        dto.setCarImageUrl(car.getImageUrl());
        dto.setCarPricePerDay(car.getPrice());

        Customer customer = rental.getCustomer();
        dto.setCustomerId(customer.getId());
        dto.setCustomerEmail(customer.getEmail());
        dto.setCustomerFirstName(customer.getFirstName());
        dto.setCustomerLastName(customer.getLastName());

        return dto;
    }
}
