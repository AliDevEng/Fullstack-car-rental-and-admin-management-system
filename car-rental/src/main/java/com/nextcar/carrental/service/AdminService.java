package com.nextcar.carrental.service;

import com.nextcar.carrental.dto.AdminStatsDTO;
import com.nextcar.carrental.entity.Admin;
import com.nextcar.carrental.repository.AdminRepository;
import com.nextcar.carrental.repository.CarRepository;
import com.nextcar.carrental.repository.CustomerRepository;
import com.nextcar.carrental.repository.PaymentRepository;
import com.nextcar.carrental.repository.RentalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    public AdminStatsDTO getStats() {
        long totalCars = carRepository.count();
        long totalCustomers = customerRepository.count();
        long totalRentals = rentalRepository.count();
        BigDecimal totalRevenue = paymentRepository.getTotalRevenue();
        return new AdminStatsDTO(totalCars, totalCustomers, totalRentals, totalRevenue);
    }
}
