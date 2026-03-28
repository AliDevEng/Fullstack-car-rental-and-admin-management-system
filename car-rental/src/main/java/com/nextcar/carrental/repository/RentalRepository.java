package com.nextcar.carrental.repository;

import com.nextcar.carrental.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Integer> {

    List<Rental> findByCustomer_Id(Integer customerId);

    Optional<Rental> findByBookingNumber(String bookingNumber);

    // Active bookings for a car in a given date range — excludes CANCELLED and COMPLETED
    @Query("""
        SELECT r FROM Rental r
        WHERE r.car.id = :carId
          AND r.status IN ('PENDING', 'ACTIVE')
          AND r.startDate < :endDate
          AND r.endDate > :startDate
    """)
    List<Rental> findConflictingRentals(
        @Param("carId") Integer carId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
}
