package com.nextcar.carrental.repository;

import com.nextcar.carrental.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    // Hitta kund baserat på email
    Optional<Customer> findByEmail(String email);

}
