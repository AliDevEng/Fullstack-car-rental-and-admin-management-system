package com.nextcar.carrental.dto;

import com.nextcar.carrental.entity.Car;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CarResponseDTO {

    private Integer id;
    private Integer categoryId;
    private String categoryName;
    private String brand;
    private String model;
    private Integer year;
    private String regNr;
    private String fuel;
    private String transmission;
    private Integer seats;
    private BigDecimal price;
    private String status;
    private String imageUrl;
    private LocalDateTime createdAt;

    public CarResponseDTO(Car car) {
        this.id = car.getId();
        this.categoryId = car.getCategoryId();
        this.categoryName = car.getCategory() != null ? car.getCategory().getName() : null;
        this.brand = car.getBrand();
        this.model = car.getModel();
        this.year = car.getYear();
        this.regNr = car.getRegNr();
        this.fuel = car.getFuel();
        this.transmission = car.getTransmission();
        this.seats = car.getSeats();
        this.price = car.getPrice();
        this.status = car.getStatus();
        this.imageUrl = car.getImageUrl();
        this.createdAt = car.getCreatedAt();
    }

    public Integer getId() { return id; }
    public Integer getCategoryId() { return categoryId; }
    public String getCategoryName() { return categoryName; }
    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public Integer getYear() { return year; }
    public String getRegNr() { return regNr; }
    public String getFuel() { return fuel; }
    public String getTransmission() { return transmission; }
    public Integer getSeats() { return seats; }
    public BigDecimal getPrice() { return price; }
    public String getStatus() { return status; }
    public String getImageUrl() { return imageUrl; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
