package com.example.carins.service;

import com.example.carins.model.Car;
import com.example.carins.model.InsuranceClaim;
import com.example.carins.model.InsurancePolicy;
import com.example.carins.repo.CarRepository;
import com.example.carins.repo.InsuranceClaimRepository;
import com.example.carins.repo.InsurancePolicyRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;


@Service
public class CarService {

    private final CarRepository carRepository;
    private final InsurancePolicyRepository policyRepository;
    private final InsuranceClaimRepository claimRepository;

    public CarService(CarRepository carRepository, InsurancePolicyRepository policyRepository, InsuranceClaimRepository claimRepository) {
        this.carRepository = carRepository;
        this.policyRepository = policyRepository;
        this.claimRepository = claimRepository;
    }

    public List<Car> listCars() {
        return carRepository.findAll();
    }
    public List<InsurancePolicy> listPolicies() {
        return policyRepository.findAll();
    }

    public Car findById(Long id)
    {
        return carRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Car with ID " + id + " not found"));

    }
    public boolean isInsuranceValid(Long carId, LocalDate date) {
        if (carId == null || date == null)
            return false; //sau illegal_argument
        // TODO: optionally throw NotFound if car does not exist
        if (!carRepository.existsById(carId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Car with ID " + carId + " not found");
        }
        return policyRepository.existsActiveOnDate(carId, date);
    }

    public InsurancePolicy createPolicy(Long carId, String provider, LocalDate startDate, LocalDate endDate) {

        if(policyRepository.existsOverlappingPolicy(carId, startDate, endDate))
        {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
        Car car = findById(carId);

        if(endDate.isBefore(startDate))
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End date is before start date.");
        }
        InsurancePolicy policy = new InsurancePolicy();
        policy.setCar(car);
        policy.setProvider(provider);
        policy.setStartDate(startDate);
        policy.setEndDate(endDate);

        return policyRepository.save(policy);
    }
    public InsuranceClaim createClaim(Long carId, String description, LocalDate claimDate, double amount) {
        Car car = findById(carId);

        if (!isInsuranceValid(carId, claimDate)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No active insurance policy found for car on claim date: " + claimDate
            );
        }

        InsuranceClaim claim = new InsuranceClaim();
        claim.setCar(car);
        claim.setDescription(description);
        claim.setClaimDate(claimDate);
        claim.setAmount(amount);

        return claimRepository.save(claim);
    }

    public List<InsuranceClaim> getClaimsByCarId(@PathVariable Long carId) {
        return claimRepository.findByCarId(carId);
    }

}