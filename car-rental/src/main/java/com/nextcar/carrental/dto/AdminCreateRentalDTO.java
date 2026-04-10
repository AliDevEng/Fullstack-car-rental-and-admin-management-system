package com.nextcar.carrental.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class AdminCreateRentalDTO {

    @NotNull(message = "Customer ID is required")
    private Integer customerId;

    @NotNull(message = "Car ID is required")
    private Integer carId;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }

    public Integer getCarId() { return carId; }
    public void setCarId(Integer carId) { this.carId = carId; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
}
