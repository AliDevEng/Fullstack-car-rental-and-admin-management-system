package com.nextcar.carrental.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@Table(name = "cars")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Brand is required")
    @Column(nullable = false, length = 50)
    private String brand;

    @NotBlank(message = "Model is required")
    @Column(nullable = false, length = 50)
    private String model;

    @NotNull(message = "Year is required")
    @Min(value = 1900, message = "Year must be 1900 or later")
    @Column(nullable = false)
    private Integer year;

    @NotBlank(message = "Fuel type is required")
    @Column(nullable = false, length = 20)
    private String fuel;

    @NotBlank(message = "Transmission is required")
    @Column(nullable = false, length = 20)
    private String transmission;

    @NotNull(message = "Category is required")
    @ManyToOne
    @JoinColumn(name = "categoryId", nullable = false)
    private CarsCategory category;

    @NotNull(message = "Seats is required")
    @Min(value = 1, message = "Seats must be at least 1")
    @Column(nullable = false)
    private Integer seats;

    @NotBlank(message = "Registration number is required")
    @Column(nullable = false, length = 20)
    private String regNr;

    @NotNull(message = "Price is required")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "image_url")
    private String imageUrl;

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

    public Integer getCategoryId() {
        return category != null ? category.getId() : null;
    }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
