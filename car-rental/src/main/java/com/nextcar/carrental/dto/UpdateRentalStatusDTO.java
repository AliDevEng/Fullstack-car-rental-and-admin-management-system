package com.nextcar.carrental.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class UpdateRentalStatusDTO {

    @NotBlank(message = "Status is required")
    @Pattern(
        regexp = "PENDING|ACTIVE|COMPLETED|CANCELLED",
        message = "Status must be one of: PENDING, ACTIVE, COMPLETED, CANCELLED"
    )
    private String status;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
