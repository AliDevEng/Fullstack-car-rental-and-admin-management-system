package com.nextcar.carrental.service;

import com.nextcar.carrental.dto.CustomerRegistrationDTO;
import com.nextcar.carrental.entity.Customer;
import com.nextcar.carrental.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Customer not found with id: " + id));
    }

    public Customer registerCustomer(CustomerRegistrationDTO dto) {
        // Business rule: passwords must match
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        // Business rule: email must be unique
        if (customerRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email is already registered");
        }

        Customer newCustomer = new Customer();
        newCustomer.setFirstName(dto.getFirstName());
        newCustomer.setLastName(dto.getLastName());
        newCustomer.setEmail(dto.getEmail());
        newCustomer.setPassword(passwordEncoder.encode(dto.getPassword()));
        newCustomer.setAddress(dto.getAddress());
        newCustomer.setPostalCode(dto.getPostalCode());
        newCustomer.setCity(dto.getCity());
        newCustomer.setCountry(dto.getCountry());
        newCustomer.setPhone(dto.getPhone());
        newCustomer.setCreated_At(LocalDateTime.now());

        return customerRepository.save(newCustomer);
    }

    public Customer updateCustomer(Long id, CustomerRegistrationDTO dto) {
        Customer existing = getCustomerById(id);

        // If email changed, ensure new email is not already taken
        if (!existing.getEmail().equals(dto.getEmail())) {
            if (customerRepository.findByEmail(dto.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Email is already registered");
            }
        }

        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());
        existing.setEmail(dto.getEmail());
        existing.setAddress(dto.getAddress());
        existing.setPostalCode(dto.getPostalCode());
        existing.setCity(dto.getCity());
        existing.setCountry(dto.getCountry());
        existing.setPhone(dto.getPhone());

        return customerRepository.save(existing);
    }

    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new NoSuchElementException("Customer not found with id: " + id);
        }
        customerRepository.deleteById(id);
    }
}
