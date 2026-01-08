-- Active: 1738012344281@@127.0.0.1@5432@s5_conception_1_aizatransport@public
-- =====================================================
-- Schéma SQL : Application de gestion de Taxi-Brousse
-- =====================================================

CREATE DATABASE s5_conception_1_aizatransport;

-- =========================
-- 1. Utilisateurs
-- =========================
CREATE TABLE public.users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    role VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =========================
-- 2. Véhicules
-- =========================
CREATE TABLE vehicles (
    id SERIAL PRIMARY KEY,
    plate_number VARCHAR(20) UNIQUE NOT NULL,
    brand VARCHAR(50),
    model VARCHAR(50),
    capacity INT NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =========================
-- 3. Places (par véhicule)
-- =========================
CREATE TABLE seats (
    id SERIAL PRIMARY KEY,
    vehicle_id INT NOT NULL REFERENCES vehicles (id) ON DELETE CASCADE,
    seat_number VARCHAR(3) NOT NULL,
    UNIQUE (vehicle_id, seat_number)
);

-- =========================
-- 4. Chauffeurs
-- =========================
CREATE TABLE drivers (
    id SERIAL PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(30),
    license_number VARCHAR(50) UNIQUE NOT NULL,
    status VARCHAR(20) NOT NULL
);

-- =========================
-- 5. Lignes (trajets)
-- =========================

CREATE TABLE destinations (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT
);

CREATE TABLE routes (
    id SERIAL PRIMARY KEY,
    departure_destination INT NOT NULL REFERENCES destinations (id),
    arrival_destination INT NOT NULL REFERENCES destinations (id),
    distance_km DECIMAL(6, 2),
    active BOOLEAN DEFAULT TRUE
);

CREATE TABLE route_prices (
    id SERIAL PRIMARY KEY,
    route_id INT NOT NULL REFERENCES routes (id),
    price DECIMAL(10, 2) NOT NULL,
    effective_date DATE NOT NULL,
    UNIQUE (route_id, effective_date)
);

-- =========================
-- 6. Voyages
-- =========================
CREATE TABLE trips (
    id SERIAL PRIMARY KEY,
    route_id INT NOT NULL REFERENCES routes (id),
    vehicle_id INT NOT NULL REFERENCES vehicles (id),
    driver_id INT NOT NULL REFERENCES drivers (id),
    departure_datetime TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL
);

-- =========================
-- 7. Passagers
-- =========================
CREATE TABLE passengers (
    id SERIAL PRIMARY KEY,
    full_name VARCHAR(100),
    phone VARCHAR(30)
);

-- =========================
-- 8. Réservations
-- =========================
CREATE TABLE reservations (
    id SERIAL PRIMARY KEY,
    passenger_id INT NOT NULL REFERENCES passengers (id),
    trip_id INT NOT NULL REFERENCES trips (id),
    seat_id INT NOT NULL REFERENCES seats (id),
    amount_paid DECIMAL(10, 2) NOT NULL,
    reservation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL,
    UNIQUE (trip_id, seat_id)
);

-- =========================
-- 9. Achats directs / Billets
-- =========================
CREATE TABLE tickets (
    id SERIAL PRIMARY KEY,
    passenger_id INT NOT NULL REFERENCES passengers (id),
    trip_id INT NOT NULL REFERENCES trips (id),
    seat_id INT NOT NULL REFERENCES seats (id),
    amount_paid DECIMAL(10, 2) NOT NULL,
    purchase_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (trip_id, seat_id)
);

-- =========================
-- 10. Dépenses
-- =========================

CREATE TABLE expense_types (
    id SERIAL PRIMARY KEY,
    type_name VARCHAR(50) UNIQUE NOT NULL,
    description TEXT
);

CREATE TABLE expenses (
    id SERIAL PRIMARY KEY,
    vehicle_id INT REFERENCES vehicles (id),
    trip_id INT REFERENCES trips (id),
    type_id INT NOT NULL REFERENCES expense_types (id),
    amount DECIMAL(10, 2) NOT NULL,
    expense_date DATE NOT NULL,
    description TEXT
);
