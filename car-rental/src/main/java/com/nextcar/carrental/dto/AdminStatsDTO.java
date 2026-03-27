package com.nextcar.carrental.dto;

import java.math.BigDecimal;

public class AdminStatsDTO {

    private long totalCars;
    private long totalCustomers;
    private long totalRentals;
    private BigDecimal totalRevenue;

    public AdminStatsDTO(long totalCars, long totalCustomers, long totalRentals, BigDecimal totalRevenue) {
        this.totalCars = totalCars;
        this.totalCustomers = totalCustomers;
        this.totalRentals = totalRentals;
        this.totalRevenue = totalRevenue;
    }

    public long getTotalCars() { return totalCars; }
    public long getTotalCustomers() { return totalCustomers; }
    public long getTotalRentals() { return totalRentals; }
    public BigDecimal getTotalRevenue() { return totalRevenue; }
}
