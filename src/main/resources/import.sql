INSERT INTO owner (id, name, email) VALUES (1, 'Ana Pop', 'ana.pop@example.com');
INSERT INTO owner (id, name, email) VALUES (2, 'Bogdan Ionescu', 'bogdan.ionescu@example.com');

INSERT INTO car (id, vin, make, model, year_of_manufacture, purchase_date, owner_id) VALUES (1, 'VIN12345', 'Dacia', 'Logan', 2018, '2020-07-14', 1);
INSERT INTO car (id, vin, make, model, year_of_manufacture, purchase_date, owner_id) VALUES (2, 'VIN67890', 'VW', 'Golf', 2021, '2021-07-14', 2);

INSERT INTO insurancepolicy (id, car_id, provider, start_date, end_date) VALUES (1, 1, 'Allianz', DATE '2024-01-01', DATE '2024-12-31');
INSERT INTO insurancepolicy (id, car_id, provider, start_date, end_date) VALUES (2, 1, 'Groupama', DATE '2025-01-01', NULL);
INSERT INTO insurancepolicy (id, car_id, provider, start_date, end_date) VALUES (3, 2, 'Allianz', DATE '2025-03-01', DATE '2025-09-30');

UPDATE insurancepolicy SET end_date = COALESCE(end_date, start_date+1 YEAR) WHERE end_date IS NULL;
COMMIT;

ALTER TABLE insurancepolicy ALTER COLUMN end_date SET NOT NULL;

INSERT INTO insuranceclaim (car_id, claim_date, description, amount) VALUES (1, '2024-05-15', 'Rear protection broke due to car hit.', 1250);
INSERT INTO insuranceclaim (car_id, claim_date, description, amount) VALUES (1, '2024-08-22', 'Windshield replacement due to stone hit.', 350);
INSERT INTO insuranceclaim (car_id, claim_date, description, amount) VALUES (2, '2025-08-10', 'Driver door to be repainted.', 1000.21);




