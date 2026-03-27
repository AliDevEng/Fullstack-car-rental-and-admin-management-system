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

    // Hämta alla bilar (paginerat)
    public Page<Car> getAllCars(Pageable pageable) {
        return carRepository.findAll(pageable);
    }

    // Hämta en bil via ID
    public Car getCarById(Integer id) {
        return carRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Car not found with id: " + id));
    }

    // Spara eller uppdatera en bil
    public Car saveCar(Car car) {
        return carRepository.save(car);
    }

    // Ta bort en bil
    public void deleteCar(Integer id) {
        if (!carRepository.existsById(id)) {
            throw new NoSuchElementException("Car not found with id: " + id);
        }
        carRepository.deleteById(id);
    }

    // Hitta tillgängliga bilar baserat på datumintervall, kategori, sortering och paginering
    public List<Car> getAvailableCars(LocalDate startDate, LocalDate endDate, Integer categoryId, String sort) {
        List<Car> allCars = carRepository.findAll();

        if (categoryId != null) {
            allCars = allCars.stream()
                    .filter(car -> car.getCategoryId().equals(categoryId))
                    .collect(Collectors.toList());
        }

        List<Rental> allRentals = rentalRepository.findAll();

        List<Car> availableCars = allCars.stream()
                .filter(car -> isCarAvailable(car.getId(), startDate, endDate, allRentals))
                .collect(Collectors.toList());

        if (sort != null && !sort.isEmpty()) {
            availableCars = sortCars(availableCars, sort);
        }

        return availableCars;
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

    public List<Car> getAvailableCars(LocalDate startDate, LocalDate endDate) {
        return getAvailableCars(startDate, endDate, null, "asc");
    }

    private boolean isCarAvailable(Integer carId, LocalDate startDate, LocalDate endDate, List<Rental> allRentals) {
        for (Rental rental : allRentals) {
            if (rental.getCar().getId().equals(carId)) {
                boolean overlaps = !(endDate.isBefore(rental.getStartDate()) || startDate.isAfter(rental.getEndDate()));
                if (overlaps) {
                    return false;
                }
            }
        }
        return true;
    }
}
