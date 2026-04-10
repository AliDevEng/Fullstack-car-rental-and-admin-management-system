package com.nextcar.carrental.service;

import com.nextcar.carrental.entity.Car;
import com.nextcar.carrental.entity.Rental;
import com.nextcar.carrental.repository.CarRepository;
import com.nextcar.carrental.repository.RentalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class CarService {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private RentalRepository rentalRepository;

    public Page<Car> getAllCars(Pageable pageable) {
        return carRepository.findAll(pageable);
    }

    public Car getCarById(Integer id) {
        return carRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Car not found with id: " + id));
    }

    public Car saveCar(Car car) {
        // Set createdAt on new cars (id is null before first save)
        if (car.getId() == null && car.getCreatedAt() == null) {
            car.setCreatedAt(LocalDateTime.now());
        }
        return carRepository.save(car);
    }

    public void deleteCar(Integer id) {
        if (!carRepository.existsById(id)) {
            throw new NoSuchElementException("Car not found with id: " + id);
        }
        carRepository.deleteById(id);
    }

    public Car updateCarStatus(Integer id, String status) {
        Car car = getCarById(id);
        car.setStatus(status);
        return carRepository.save(car);
    }

    public List<Car> getAvailableCars(LocalDate startDate, LocalDate endDate, Integer categoryId, String sort) {
        List<Car> allCars = carRepository.findAll();

        if (categoryId != null) {
            allCars = allCars.stream()
                    .filter(car -> car.getCategoryId() != null && car.getCategoryId().equals(categoryId))
                    .collect(Collectors.toList());
        }

        // Only PENDING and ACTIVE rentals block availability
        List<Rental> activeRentals = rentalRepository.findAll().stream()
                .filter(r -> "PENDING".equals(r.getStatus()) || "ACTIVE".equals(r.getStatus()))
                .collect(Collectors.toList());

        List<Car> availableCars = allCars.stream()
                .filter(car -> isCarAvailable(car.getId(), startDate, endDate, activeRentals))
                .collect(Collectors.toList());

        if (sort != null && !sort.isEmpty()) {
            availableCars = sortCars(availableCars, sort);
        }

        return availableCars;
    }

    public List<Car> getAvailableCars(LocalDate startDate, LocalDate endDate) {
        return getAvailableCars(startDate, endDate, null, "asc");
    }

    public List<Car> sortCars(List<Car> cars, String sort) {
        List<Car> sortedCars = new ArrayList<>(cars);
        if ("desc".equalsIgnoreCase(sort)) {
            sortedCars.sort((a, b) -> b.getPrice().compareTo(a.getPrice()));
        } else {
            sortedCars.sort(Comparator.comparing(Car::getPrice));
        }
        return sortedCars;
    }

    // Rental dates are LocalDateTime — convert to LocalDate for day-level comparison
    private boolean isCarAvailable(Integer carId, LocalDate startDate, LocalDate endDate, List<Rental> allRentals) {
        for (Rental rental : allRentals) {
            if (rental.getCar().getId().equals(carId)) {
                LocalDate rentalStart = rental.getStartDate().toLocalDate();
                LocalDate rentalEnd   = rental.getEndDate().toLocalDate();
                boolean overlaps = !(endDate.isBefore(rentalStart) || startDate.isAfter(rentalEnd));
                if (overlaps) {
                    return false;
                }
            }
        }
        return true;
    }
}
