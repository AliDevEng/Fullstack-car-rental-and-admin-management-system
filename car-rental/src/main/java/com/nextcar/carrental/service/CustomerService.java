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

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer getCustomerById(Integer id) {
        return customerRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Customer not found with id: " + id));
    }

    public Customer registerCustomer(CustomerRegistrationDTO dto) {
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        if (customerRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email is already registered");
        }

        Customer customer = new Customer();
        customer.setFirstName(dto.getFirstName());
        customer.setLastName(dto.getLastName());
        customer.setEmail(dto.getEmail());
        customer.setPassword(passwordEncoder.encode(dto.getPassword()));
        customer.setAddress(dto.getAddress());
        customer.setPostalCode(dto.getPostalCode());
        customer.setCity(dto.getCity());
        customer.setCountry(dto.getCountry());
        customer.setPhone(dto.getPhone());
        customer.setCreatedAt(LocalDateTime.now());

        return customerRepository.save(customer);
    }

    public Customer updateCustomer(Integer id, CustomerRegistrationDTO dto) {
        Customer existing = getCustomerById(id);

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

    public void deleteCustomer(Integer id) {
        if (!customerRepository.existsById(id)) {
            throw new NoSuchElementException("Customer not found with id: " + id);
        }
        customerRepository.deleteById(id);
    }
}
