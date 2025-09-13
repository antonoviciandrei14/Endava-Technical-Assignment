package com.example.carins.service;

import com.example.carins.model.Car;
import com.example.carins.model.InsurancePolicy;
import com.example.carins.repo.CarRepository;
import com.example.carins.repo.InsurancePolicyRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;


@Service
public class CarService {

    private final CarRepository carRepository;
    private final InsurancePolicyRepository policyRepository;

    public CarService(CarRepository carRepository, InsurancePolicyRepository policyRepository) {
        this.carRepository = carRepository;
        this.policyRepository = policyRepository;
    }

    public List<Car> listCars() {
        return carRepository.findAll();
    }
    public List<InsurancePolicy> listPolicies() {
        return policyRepository.findAll();
    }

    public Car findById(Long id)
    {
        return carRepository.findById(id).orElseThrow(() -> new RuntimeException("Car with ID " + id + " not found"));

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

        InsurancePolicy policy = new InsurancePolicy();
        policy.setCar(car);
        policy.setProvider(provider);
        policy.setStartDate(startDate);
        policy.setEndDate(endDate);

        return policyRepository.save(policy);
    }
}