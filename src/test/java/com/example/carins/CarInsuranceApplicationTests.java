package com.example.carins;

import com.example.carins.service.CarService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CarInsuranceApplicationTests {

    @Autowired
    CarService service;

    @Test
    void insuranceValidityBasic() {
        assertTrue(service.isInsuranceValid(1L, LocalDate.parse("2024-06-01")));
        assertTrue(service.isInsuranceValid(1L, LocalDate.parse("2025-06-01")));
        assertFalse(service.isInsuranceValid(2L, LocalDate.parse("2025-02-01")));
    }

    @Test
    void testCarDateTooOld() {
        LocalDate tooOldDate = LocalDate.of(1850, 1, 1);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.isInsuranceValid(1L, tooOldDate));

        assertEquals(400, exception.getStatusCode().value());
    }

    @Test
    void testCarDateInFuture() {
        LocalDate tooFutureDate = LocalDate.now().plusYears(15);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.isInsuranceValid(1L, tooFutureDate));

        assertEquals(400, exception.getStatusCode().value());
    }

    @Test
    void testCarNotFound() {
        Long nonExistentCarId = 1234L;
        LocalDate validDate = LocalDate.now();

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.isInsuranceValid(nonExistentCarId, validDate));

        assertEquals(404, exception.getStatusCode().value());
    }

    @Test
    void testCreateClaimNonExistentCar() {
        Long nonExistentCarId = 1234L;
        LocalDate validDate = LocalDate.now();

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.createClaim(nonExistentCarId, "Test claim", validDate, 1000.0));

        assertEquals(404, exception.getStatusCode().value());
    }

    @Test
    void testCreateClaimInvalidInsurance() {
        LocalDate dateWithoutInsurance = LocalDate.of(2020, 1, 1);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.createClaim(2L, "Test claim", dateWithoutInsurance, 1000.0));

        assertEquals(400, exception.getStatusCode().value());
    }

    @Test
    void testCreatePolicyInvalidStartDate() {
        LocalDate invalidStartDate = LocalDate.of(1850, 1, 1);
        LocalDate validEndDate = LocalDate.now().plusYears(1);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.createPolicy(1L, "Allianz", invalidStartDate, validEndDate));

        assertEquals(400, exception.getStatusCode().value());
    }

    @Test
    void testCreatePolicyInvalidEndDate() {
        LocalDate validStartDate = LocalDate.now();
        LocalDate invalidEndDate = LocalDate.now().plusYears(15);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.createPolicy(1L, "Allianz", validStartDate, invalidEndDate));

        assertEquals(400, exception.getStatusCode().value());
    }

    @Test
    void testCreatePolicyEndDateBeforeStartDate() {
        LocalDate startDate = LocalDate.now().plusDays(10);
        LocalDate endDate = LocalDate.now().plusDays(5);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.createPolicy(1L, "Allianz", startDate, endDate));

        assertEquals(400, exception.getStatusCode().value());
    }

    @Test
    void testCreatePolicyNonExistentCar() {
        Long nonExistentCarId = 1234L;
        LocalDate validStartDate = LocalDate.now();
        LocalDate validEndDate = LocalDate.now().plusYears(1);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.createPolicy(nonExistentCarId, "Allianz", validStartDate, validEndDate));

        assertEquals(404, exception.getStatusCode().value());
    }

    @Test
    void testCreateOverlappingPolicy() {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.createPolicy(1L, "Allianz", startDate, endDate));

        assertEquals(409, exception.getStatusCode().value());
    }

    @Test
    void testGetHistoryNonExistentCar() {
        Long nonExistentCarId = 1234L;

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.getCarHistory(nonExistentCarId));

        assertEquals(404, exception.getStatusCode().value());
    }

    @Test
    void testNullCarId() {
        LocalDate validDate = LocalDate.now();
        assertFalse(service.isInsuranceValid(null, validDate));
    }

    @Test
    void testNullDate() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.isInsuranceValid(1L, null));

        assertEquals(400, exception.getStatusCode().value());
    }
    @Test
    void testCreatePolicyInvalidFormatDate() {
        Long carId = 1L;
        String provider = "Allianz";

        assertThrows(DateTimeParseException.class, () -> {
            LocalDate invalidDate = LocalDate.parse("14-02-2025");
            service.createPolicy(carId, provider, invalidDate, LocalDate.now().plusYears(1));
        });
    }
    @Test
    void testCreateClaimInvalidFormatDate() {
        Long carId = 1L;
        String description = "Bird in windshield";

        assertThrows(DateTimeParseException.class, () -> {
            LocalDate invalidDate = LocalDate.parse("14-02-2025");
            service.createClaim(carId, description, invalidDate, 1233);
        });
    }
}