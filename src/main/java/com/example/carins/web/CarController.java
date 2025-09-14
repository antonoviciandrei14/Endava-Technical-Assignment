package com.example.carins.web;

import com.example.carins.model.Car;
import com.example.carins.model.InsuranceClaim;
import com.example.carins.model.InsurancePolicy;
import com.example.carins.service.CarService;
import com.example.carins.web.dto.CarDto;
import com.example.carins.web.dto.ClaimDto;
import com.example.carins.web.dto.HistoryDto;
import com.example.carins.web.dto.PolicyDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CarController {

    private final CarService service;

    public CarController(CarService service) {
        this.service = service;
    }

    @GetMapping("/cars")
    public List<CarDto> getCars() {
        return service.listCars().stream().map(this::toDto).toList();
    }
    @GetMapping("/policies")
    public List<PolicyDto> getPolicies() {
        return service.listPolicies().stream().map(this::toDto).toList();
    }

    @GetMapping("/cars/{carId}/insurance-valid")
    public ResponseEntity<?> isInsuranceValid(@PathVariable Long carId, @RequestParam String date) {
        // TODO: validate date format and handle errors consistently (made in service)
        LocalDate d = LocalDate.parse(date);
        /*LocalDate d;
        try {
            d = LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid YYYY-MM-DD format");
        }*/
        boolean valid = service.isInsuranceValid(carId, d);
        return ResponseEntity.ok(new InsuranceValidityResponse(carId, d.toString(), valid));
    }

    @PostMapping("/create-policy")
    public ResponseEntity<PolicyDto> createPolicy(@Valid @RequestBody PolicyDto pDto) {
        InsurancePolicy savedPolicy = service.createPolicy(
                pDto.car_id(),
                pDto.provider(),
                pDto.startDate(),
                pDto.endDate()
        );
        return ResponseEntity.ok(toDto(savedPolicy));
    }

    @PostMapping("/create-claim")
    public ResponseEntity<ClaimDto> createClaim(@Valid @RequestBody ClaimDto cDto) {
        InsuranceClaim savedClaim = service.createClaim(
                cDto.car_id(),
                cDto.description(),
                cDto.claimDate(),
                cDto.amount()
        );
        return ResponseEntity.ok(toDto(savedClaim));
        /*URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{claimId}")
                .buildAndExpand(savedClaim.getId())
                .toUri();

        return ResponseEntity.created(location).body(cDto);*/
    }

    @GetMapping("cars/{carId}/claims")
    public List<ClaimDto> getClaims(@PathVariable Long carId) {
        return service.getClaimsByCarId(carId).stream().map(this::toDto).toList();
    }

    @GetMapping("cars/{carId}/history")
    public ResponseEntity<List<HistoryDto>> getHistory(@PathVariable Long carId) {
        return ResponseEntity.ok(service.getCarHistory(carId));
    }

    private CarDto toDto(Car c) {
        var o = c.getOwner();
        return new CarDto(c.getId(), c.getVin(), c.getMake(), c.getModel(), c.getYearOfManufacture(), c.getPurchaseDate(),
                o != null ? o.getId() : null,
                o != null ? o.getName() : null,
                o != null ? o.getEmail() : null);
    }
    private PolicyDto toDto(InsurancePolicy p) {
        return new PolicyDto(p.getId(), p.getCar().getId(), p.getProvider(), p.getStartDate(), p.getEndDate());
    }

    private ClaimDto toDto(InsuranceClaim c) {
        return new ClaimDto(c.getId(), c.getCar().getId(), c.getDescription(), c.getClaimDate(), c.getAmount());
    }
    public record InsuranceValidityResponse(Long carId, String date, boolean valid) {}

}
