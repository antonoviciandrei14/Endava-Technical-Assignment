package com.example.carins.repo;

import com.example.carins.model.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface InsurancePolicyRepository extends JpaRepository<InsurancePolicy, Long> {
    List<InsurancePolicy> findAll();
    @Query("select case when count(p) > 0 then true else false end " +
           "from InsurancePolicy p " +
           "where p.car.id = :carId " +
           "and p.startDate <= :date " +
           "and (p.endDate >= :date)")
    boolean existsActiveOnDate(@Param("carId") Long carId, @Param("date") LocalDate date);

    @Query("select case when count(p) > 0 then true else false end " +
            "from InsurancePolicy p " +
            "where p.car.id = :carId " +
            "and p.startDate <= :endDate " +
            "and p.endDate >= :startDate")
    boolean existsOverlappingPolicy(@Param("carId") Long carId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    List<InsurancePolicy> findByCarId(Long carId);
}