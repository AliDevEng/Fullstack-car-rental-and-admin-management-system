package com.nextcar.carrental.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Cars")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @NotBlank(message = "Brand is required")
    @Column(name = "Brand", nullable = false, length = 255)
    private String brand;

    @NotBlank(message = "Model is required")
    @Column(name = "Model", nullable = false, length = 255)
    private String model;

    @NotNull(message = "Year is required")
    @Min(value = 1900, message = "Year must be 1900 or later")
    @Column(name = "Year", nullable = false)
    private Integer year;

    @NotBlank(message = "Fuel type is required")
    @Column(name = "Fuel", nullable = false, length = 20)
    private String fuel;

    @NotBlank(message = "Transmission is required")
    @Column(name = "Transmission", nullable = false, length = 20)
    private String transmission;

    @NotNull(message = "Category is required")
    @ManyToOne
    @JoinColumn(name = "CategoryId", nullable = false)
    private CarsCategory category;

    @NotNull(message = "Seats is required")
    @Min(value = 1, message = "Seats must be at least 1")
    @Column(name = "Seats", nullable = false)
    private Integer seats;

    @NotBlank(message = "Registration number is required")
    @Column(name = "RegNr", nullable = false, length = 255)
    private String regNr;

    @NotNull(message = "Price is required")
    @Column(name = "Price", nullable = false, precision = 18, scale = 2)
    private BigDecimal price;

    @Column(name = "Status", nullable = false, length = 20)
    private String status = "Available";

    @Column(name = "ImageUrl", length = 500)
    private String imageUrl;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    // Getters and setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public String getFuel() { return fuel; }
    public void setFuel(String fuel) { this.fuel = fuel; }

    public String getTransmission() { return transmission; }
    public void setTransmission(String transmission) { this.transmission = transmission; }

    public CarsCategory getCategory() { return category; }
    public void setCategory(CarsCategory category) { this.category = category; }

    public Integer getSeats() { return seats; }
    public void setSeats(Integer seats) { this.seats = seats; }

    public String getRegNr() { return regNr; }
    public void setRegNr(String regNr) { this.regNr = regNr; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // Helper to get categoryId directly
    public Integer getCategoryId() {
        return category != null ? category.getId() : null;
    }
}
