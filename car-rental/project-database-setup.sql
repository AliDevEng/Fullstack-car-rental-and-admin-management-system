-- ============================================================================
-- Java Spring Boot - Car Rental System
-- Database: mr_rent
-- Schema: Matches the C# project structure exactly
--         (same tables, same columns, same relationships)
-- Run this entire file in MySQL Workbench
-- ============================================================================

CREATE DATABASE IF NOT EXISTS `mr_rent`
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE `mr_rent`;

-- ============================================================================
-- TABLE CREATION  (follow FK dependency order)
-- ============================================================================

-- 1. CarsCategories — no dependencies
CREATE TABLE IF NOT EXISTS `CarsCategories` (
    `Id`   INT          NOT NULL AUTO_INCREMENT,
    `Name` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`Id`)
) ENGINE=InnoDB;

-- 2. Admins — no dependencies
CREATE TABLE IF NOT EXISTS `Admins` (
    `Id`        INT          NOT NULL AUTO_INCREMENT,
    `Email`     VARCHAR(255) NOT NULL,
    `Password`  VARCHAR(255) NOT NULL,
    `Role`      VARCHAR(50)  NOT NULL,
    `CreatedAt` DATETIME(6)  NOT NULL,
    PRIMARY KEY (`Id`),
    UNIQUE KEY `uq_admins_email` (`Email`)
) ENGINE=InnoDB;

-- 3. Customers — no dependencies
CREATE TABLE IF NOT EXISTS `Customers` (
    `Id`         INT          NOT NULL AUTO_INCREMENT,
    `Email`      VARCHAR(255) NOT NULL,
    `Password`   VARCHAR(255) NOT NULL,
    `FirstName`  VARCHAR(50),
    `LastName`   VARCHAR(50),
    `Phone`      VARCHAR(20),
    `Address`    VARCHAR(200),
    `City`       VARCHAR(50),
    `PostalCode` VARCHAR(20),
    `Country`    VARCHAR(50),
    `CreatedAt`  DATETIME(6)  NOT NULL,
    PRIMARY KEY (`Id`),
    UNIQUE KEY `uq_customers_email` (`Email`)
) ENGINE=InnoDB;

-- 4. Cars — depends on CarsCategories
--    ImageUrl added on top of C# schema for frontend image support
CREATE TABLE IF NOT EXISTS `Cars` (
    `Id`           INT           NOT NULL AUTO_INCREMENT,
    `CategoryId`   INT           NOT NULL,
    `Brand`        VARCHAR(255)  NOT NULL,
    `Model`        VARCHAR(255)  NOT NULL,
    `Year`         INT           NOT NULL,
    `RegNr`        VARCHAR(255)  NOT NULL,
    `Fuel`         VARCHAR(20)   NOT NULL,
    `Transmission` VARCHAR(20)   NOT NULL,
    `Seats`        INT           NOT NULL,
    `Price`        DECIMAL(18,2) NOT NULL,
    `Status`       VARCHAR(20)   NOT NULL DEFAULT 'Available',
    `ImageUrl`     VARCHAR(500),
    `CreatedAt`    DATETIME(6)   NOT NULL,
    PRIMARY KEY (`Id`),
    UNIQUE KEY `uq_cars_regnr` (`RegNr`),
    CONSTRAINT `fk_cars_category` FOREIGN KEY (`CategoryId`) REFERENCES `CarsCategories` (`Id`)
) ENGINE=InnoDB;

-- 5. Rentals — depends on Customers and Cars
--    PaymentId is NULL initially (set after payment is created)
--    FK to Payments is added later via ALTER TABLE (circular dependency workaround)
CREATE TABLE IF NOT EXISTS `Rentals` (
    `Id`            INT          NOT NULL AUTO_INCREMENT,
    `CustomerId`    INT          NOT NULL,
    `CarId`         INT          NOT NULL,
    `PaymentId`     INT          NULL,
    `RentalDate`    DATETIME(6)  NOT NULL,
    `StartDate`     DATETIME(6)  NOT NULL,
    `EndDate`       DATETIME(6)  NOT NULL,
    `BookingNumber` VARCHAR(255) NOT NULL,
    `Status`        VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    `CreatedAt`     DATETIME(6)  NOT NULL,
    PRIMARY KEY (`Id`),
    UNIQUE KEY `uq_rentals_booking` (`BookingNumber`),
    CONSTRAINT `fk_rentals_customer` FOREIGN KEY (`CustomerId`) REFERENCES `Customers` (`Id`),
    CONSTRAINT `fk_rentals_car`      FOREIGN KEY (`CarId`)      REFERENCES `Cars` (`Id`)
) ENGINE=InnoDB;

-- 6. Payments — depends on Rentals
CREATE TABLE IF NOT EXISTS `Payments` (
    `Id`            INT           NOT NULL AUTO_INCREMENT,
    `RentalId`      INT           NOT NULL,
    `Amount`        DECIMAL(18,2) NOT NULL,
    `Method`        VARCHAR(50)   NOT NULL,
    `Status`        VARCHAR(20)   NOT NULL DEFAULT 'PENDING',
    `PaymentDate`   DATETIME(6)   NOT NULL,
    `TransactionId` VARCHAR(100),
    `CreatedAt`     DATETIME(6)   NOT NULL,
    PRIMARY KEY (`Id`),
    CONSTRAINT `fk_payments_rental` FOREIGN KEY (`RentalId`) REFERENCES `Rentals` (`Id`)
) ENGINE=InnoDB;

-- 7. Close the circular FK: Rentals.PaymentId -> Payments.Id
ALTER TABLE `Rentals`
    ADD CONSTRAINT `fk_rentals_payment` FOREIGN KEY (`PaymentId`) REFERENCES `Payments` (`Id`);


-- ============================================================================
-- SEED DATA
-- ============================================================================

-- Car categories (fixed — do not change IDs)
INSERT INTO `CarsCategories` (`Id`, `Name`) VALUES
(1, 'Budget'),
(2, 'Economy'),
(3, 'SUV'),
(4, 'Transport');


-- ============================================================================
-- Admin accounts
--
-- Passwords MUST be BCrypt hashed. The app verifies with BCrypt.
-- Steps:
--   1. Go to https://bcrypt-generator.com
--   2. Enter your desired password (e.g. "Admin123!")
--   3. Set Rounds = 10
--   4. Copy the hash  (starts with $2a$10$...)
--   5. Replace the placeholder below with the real hash
-- ============================================================================
INSERT INTO `Admins` (`Email`, `Password`, `Role`, `CreatedAt`) VALUES
('admin@nextcar.com',   '$2a$10$REPLACE_WITH_BCRYPT_HASH', 'ADMIN',       NOW()),
('manager@nextcar.com', '$2a$10$REPLACE_WITH_BCRYPT_HASH', 'SUPER_ADMIN', NOW());


-- ============================================================================
-- Cars — 20 vehicles across 4 categories
-- Status values: 'Available' | 'Rented' | 'Maintenance'
-- ============================================================================

-- Budget (3)
INSERT INTO `Cars` (`CategoryId`, `Brand`, `Model`, `Year`, `RegNr`, `Fuel`, `Transmission`, `Seats`, `Price`, `Status`, `CreatedAt`) VALUES
(1, 'Volkswagen', 'Polo',   2018, 'ABC123', 'Petrol', 'Manual', 5,  350.00, 'Available', NOW()),
(1, 'Ford',       'Fiesta', 2019, 'DEF456', 'Petrol', 'Manual', 5,  400.00, 'Available', NOW()),
(1, 'Skoda',      'Fabia',  2020, 'GHI789', 'Diesel', 'Manual', 5,  450.00, 'Available', NOW());

-- Economy (5)
INSERT INTO `Cars` (`CategoryId`, `Brand`, `Model`, `Year`, `RegNr`, `Fuel`, `Transmission`, `Seats`, `Price`, `Status`, `CreatedAt`) VALUES
(2, 'Toyota',     'Corolla', 2021, 'JKL012', 'Hybrid', 'Automatic', 5, 650.00, 'Available', NOW()),
(2, 'Volkswagen', 'Golf',    2022, 'MNO345', 'Petrol', 'Automatic', 5, 700.00, 'Available', NOW()),
(2, 'Honda',      'Civic',   2023, 'PQR678', 'Hybrid', 'Automatic', 5, 750.00, 'Rented',    NOW()),
(2, 'Mazda',      '3',       2022, 'STU901', 'Petrol', 'Automatic', 5, 680.00, 'Available', NOW()),
(2, 'Hyundai',    'i30',     2021, 'VWX234', 'Diesel', 'Automatic', 5, 620.00, 'Available', NOW());

-- SUV (7)
INSERT INTO `Cars` (`CategoryId`, `Brand`, `Model`, `Year`, `RegNr`, `Fuel`, `Transmission`, `Seats`, `Price`, `Status`, `CreatedAt`) VALUES
(3, 'Volvo',      'XC60',    2023, 'YZA567', 'Hybrid', 'Automatic', 5, 1200.00, 'Available', NOW()),
(3, 'BMW',        'X3',      2022, 'BCD890', 'Diesel', 'Automatic', 5, 1350.00, 'Rented',    NOW()),
(3, 'Toyota',     'RAV4',    2024, 'EFG123', 'Hybrid', 'Automatic', 5, 1100.00, 'Available', NOW()),
(3, 'Nissan',     'Qashqai', 2021, 'HIJ456', 'Petrol', 'Automatic', 5,  950.00, 'Available', NOW()),
(3, 'Audi',       'Q5',      2023, 'KLM789', 'Diesel', 'Automatic', 5, 1400.00, 'Available', NOW()),
(3, 'Mercedes',   'GLC',     2022, 'NOP012', 'Diesel', 'Automatic', 5, 1500.00, 'Available', NOW()),
(3, 'Volkswagen', 'Tiguan',  2023, 'QRS345', 'Petrol', 'Automatic', 5, 1050.00, 'Available', NOW());

-- Transport (5)
INSERT INTO `Cars` (`CategoryId`, `Brand`, `Model`, `Year`, `RegNr`, `Fuel`, `Transmission`, `Seats`, `Price`, `Status`, `CreatedAt`) VALUES
(4, 'Mercedes',   'Sprinter',    2022, 'TUV678', 'Diesel', 'Manual',    3, 1800.00, 'Available', NOW()),
(4, 'Ford',       'Transit',     2021, 'WXY901', 'Diesel', 'Manual',    3, 1600.00, 'Rented',    NOW()),
(4, 'Volkswagen', 'Transporter', 2023, 'ZAB234', 'Diesel', 'Automatic', 9, 1700.00, 'Available', NOW()),
(4, 'Renault',    'Master',      2020, 'CDE567', 'Diesel', 'Manual',    3, 1500.00, 'Available', NOW()),
(4, 'Peugeot',    'Boxer',       2022, 'FGH890', 'Diesel', 'Manual',    3, 1650.00, 'Available', NOW());


-- ============================================================================
-- Customers — use POST /customers/register API instead for proper BCrypt hashing
-- Uncomment and replace hashes if inserting directly:
-- ============================================================================
/*
INSERT INTO `Customers` (`Email`, `Password`, `FirstName`, `LastName`, `Phone`, `Address`, `City`, `PostalCode`, `Country`, `CreatedAt`) VALUES
('john.doe@gmail.com',         '$2a$10$REPLACE_WITH_BCRYPT_HASH', 'John',  'Doe',       '+46701234567', 'Storgatan 1',       'Stockholm',  '11122', 'Sweden', NOW()),
('jane.smith@gmail.com',       '$2a$10$REPLACE_WITH_BCRYPT_HASH', 'Jane',  'Smith',     '+46709876543', 'Kungsgatan 5',      'Gothenburg', '41103', 'Sweden', NOW()),
('mike.johnson@hotmail.com',   '$2a$10$REPLACE_WITH_BCRYPT_HASH', 'Mike',  'Johnson',   '+46708765432', 'Drottninggatan 10', 'Malmö',      '21143', 'Sweden', NOW()),
('anna.wilson@yahoo.com',      '$2a$10$REPLACE_WITH_BCRYPT_HASH', 'Anna',  'Wilson',    '+46707654321', 'Norrmalmstorg 1',   'Stockholm',  '11147', 'Sweden', NOW()),
('lars.andersson@outlook.com', '$2a$10$REPLACE_WITH_BCRYPT_HASH', 'Lars',  'Andersson', '+46706543210', 'Avenyn 12',         'Gothenburg', '41104', 'Sweden', NOW());
*/


-- ============================================================================
-- VERIFICATION — run after inserting to confirm data is correct
-- ============================================================================
SELECT 'CarsCategories' AS `Table`, COUNT(*) AS `Rows` FROM `CarsCategories`
UNION ALL SELECT 'Admins',    COUNT(*) FROM `Admins`
UNION ALL SELECT 'Customers', COUNT(*) FROM `Customers`
UNION ALL SELECT 'Cars',      COUNT(*) FROM `Cars`;

SELECT cc.`Name` AS `Category`, COUNT(c.`Id`) AS `Cars`
FROM `Cars` c
JOIN `CarsCategories` cc ON c.`CategoryId` = cc.`Id`
GROUP BY cc.`Name`;
