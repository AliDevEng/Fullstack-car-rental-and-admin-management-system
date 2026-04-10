package com.nextcar.carrental.controller;

import com.nextcar.carrental.dto.CarResponseDTO;
import com.nextcar.carrental.dto.PagedResponseDTO;
import com.nextcar.carrental.dto.UpdateCarStatusDTO;
import com.nextcar.carrental.entity.Car;
import com.nextcar.carrental.service.CarService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/cars")
@CrossOrigin(origins = "http://localhost:3000")
public class CarController {

    @Autowired
    private CarService carService;

    // GET /cars?page=0&size=10&sort=price,asc
    @GetMapping
    public ResponseEntity<PagedResponseDTO<CarResponseDTO>> getAllCars(
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        Page<CarResponseDTO> cars = carService.getAllCars(pageable).map(CarResponseDTO::new);
        return ResponseEntity.ok(new PagedResponseDTO<>(cars));
    }

    // GET /cars/available?startDate=2024-10-15&endDate=2024-10-20&categoryId=1&sort=asc
    @GetMapping("/available")
    public ResponseEntity<?> getAvailableCars(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false, defaultValue = "asc") String sort) {

        if (startDate == null || startDate.isEmpty() || endDate == null || endDate.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("Both start date and end date must be provided");
        }

        LocalDate start;
        LocalDate end;
        try {
            start = LocalDate.parse(startDate);
            end = LocalDate.parse(endDate);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Invalid date format. Use: YYYY-MM-DD");
        }

        if (start.isBefore(LocalDate.now())) {
            return ResponseEntity.badRequest()
                    .body("Start date cannot be in the past");
        }

        if (!end.isAfter(start)) {
            return ResponseEntity.badRequest()
                    .body("End date must be at least 1 day after start date");
        }

        List<CarResponseDTO> availableCars = carService.getAvailableCars(start, end, categoryId, sort)
                .stream().map(CarResponseDTO::new).toList();
        return ResponseEntity.ok(availableCars);
    }

    // GET /cars/5
    @GetMapping("/{id}")
    public ResponseEntity<CarResponseDTO> getCarById(@PathVariable Integer id) {
        return ResponseEntity.ok(new CarResponseDTO(carService.getCarById(id)));
    }

    // POST /cars - 201 Created
    @PostMapping
    public ResponseEntity<CarResponseDTO> createCar(@Valid @RequestBody Car car) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new CarResponseDTO(carService.saveCar(car)));
    }

    // PUT /cars/5
    @PutMapping("/{id}")
    public ResponseEntity<CarResponseDTO> updateCar(@PathVariable Integer id, @Valid @RequestBody Car car) {
        car.setId(id);
        return ResponseEntity.ok(new CarResponseDTO(carService.saveCar(car)));
    }

    // DELETE /cars/5
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCar(@PathVariable Integer id) {
        carService.deleteCar(id);
        return ResponseEntity.noContent().build();
    }

    // PUT /cars/5/status - admin: toggle car status (Available / Unavailable)
    @PutMapping("/{id}/status")
    public ResponseEntity<CarResponseDTO> updateCarStatus(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateCarStatusDTO dto) {
        return ResponseEntity.ok(new CarResponseDTO(carService.updateCarStatus(id, dto.getStatus())));
    }
}
