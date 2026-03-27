package com.nextcar.carrental.controller;

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
    public ResponseEntity<Page<Car>> getAllCars(
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        Page<Car> cars = carService.getAllCars(pageable);
        return ResponseEntity.ok(cars);
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

        List<Car> availableCars = carService.getAvailableCars(start, end, categoryId, sort);
        return ResponseEntity.ok(availableCars);
    }

    // GET /cars/5
    @GetMapping("/{id}")
    public ResponseEntity<Car> getCarById(@PathVariable Integer id) {
        Car car = carService.getCarById(id);
        return ResponseEntity.ok(car);
    }

    // POST /cars - 201 Created
    @PostMapping
    public ResponseEntity<Car> createCar(@Valid @RequestBody Car car) {
        Car savedCar = carService.saveCar(car);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCar);
    }

    // PUT /cars/5
    @PutMapping("/{id}")
    public ResponseEntity<Car> updateCar(@PathVariable Integer id, @Valid @RequestBody Car car) {
        car.setId(id);
        Car updatedCar = carService.saveCar(car);
        return ResponseEntity.ok(updatedCar);
    }

    // DELETE /cars/5
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCar(@PathVariable Integer id) {
        carService.deleteCar(id);
        return ResponseEntity.noContent().build();
    }
}
