package com.Data.demo.auth.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.Data.demo.auth.entity.Customer;

import jakarta.transaction.Transactional;

import java.util.Date;
import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {

    Customer findByEmailIdsContaining(String email);

    @Query("SELECT c FROM Customer c WHERE c.emailIds LIKE CONCAT(:email, ',%') OR c.emailIds LIKE CONCAT(:email)")
    Customer findByFirstEmail(@Param("email") String email);

    Customer findByEmailId(String emailId);

    @Modifying
    @Transactional
    @Query("UPDATE Customer c SET c.customerName = :customerName, c.status = :status, c.addressLine1 = :addressLine1, " +
            "c.addressLine2 = :addressLine2, c.city = :city, c.state = :state, c.deviceIds = :deviceIds, " +
            "c.dateOfRegistration = :dateOfRegistration, c.emailIds = :emailIds WHERE c.emailIds LIKE CONCAT(:email, ',%') " +
            "OR c.emailIds LIKE CONCAT(:email)")
    int updateCustomer(
            @Param("customerName") String customerName,
            @Param("status") int status,
            @Param("addressLine1") String addressLine1,
            @Param("addressLine2") String addressLine2,
            @Param("city") String city,
            @Param("state") String state,
            @Param("deviceIds") List<String> deviceIds,
            @Param("dateOfRegistration") Date dateOfRegistration,
            @Param("emailIds") String emailIds,
            @Param("email") String email
    );
    @Query("SELECT c.customerName FROM Customer c")
    List<String> getAllCustomerNames();

    Customer findByCustomerName(String customerName);
}
