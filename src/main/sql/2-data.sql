-- Active: 1738012344281@@127.0.0.1@5432@s5_conception_1_aizatransport@public
-- ============================================
-- DONNÉES POUR LA BASE DE DONNÉES AIZA TRANSPORT
-- ============================================
-- Toutes les données ci-dessous sont en contexte malgache

-- ============================================
-- DESTINATIONS
-- ============================================
INSERT INTO public.destinations (name, description) VALUES
('Antananarivo', 'La capitale de Madagascar, centre économique et politique du pays'),
('Antsirabe', 'Deuxième ville importante du Vakinankaratra, connue pour ses thermes'),
('Fianarantsoa', 'Capitale de la région Amoron''i Mania, ville de commerce et d''agriculture'),
('Toliara', 'Port important du sud-ouest, centre commercial de la région Androy'),
('Sambava', 'Ville importante du nord-est, centre de commerce de vanille et de litchi'),
('Antalaha', 'Port majeur du nord-est, capitale du commerce de vanille'),
('Andapa', 'Ville portuaire du nord-est, centre commercial important'),
('Soanierana Ivongo', 'Petit port touristique de la côte nord-est'),
('Morondava', 'Port touristique majeur de la côte ouest, célèbre pour les allées de baobabs'),
('Mahajanga', 'Grande ville portuaire du nord-ouest, carrefour commercial');

-- ============================================
-- TRIP TYPES (Types de trajets)
-- ============================================
INSERT INTO public.trip_types (name, active) VALUES
('Classique', true),
('Premium', true),
('VIP', true),
('Gold VIP', true);

-- ============================================
-- EXPENSE TYPES (Types de dépenses)
-- ============================================
INSERT INTO public.expense_types (type_name, description) VALUES
('Carburant', 'Dépenses en carburant (essence, diesel) pour les véhicules'),
('Maintenance', 'Coûts de maintenance régulière et réparations des véhicules'),
('Péage', 'Frais de péage sur les routes nationales'),
('Assurance', 'Primes d''assurance pour les véhicules et responsabilité civile'),
('Salaire chauffeur', 'Salaire et indemnités des chauffeurs'),
('Frais administratifs', 'Frais administratifs, permis et autorisations'),
('Nettoyage', 'Coûts de nettoyage et entretien des véhicules'),
('Amendes', 'Amendes et infractions routières');

-- ============================================
-- VEHICLES (Véhicules)
-- ============================================
INSERT INTO public.vehicles (plate_number, brand, model, capacity, status, active, created_at) VALUES
('3101 TAB', 'Peugeot', '504', 14, 'Actif', true, '2024-01-15'),
('3102 TAB', 'Renault', 'Talisman', 16, 'Actif', true, '2024-02-20'),
('3103 TAB', 'Hyundai', 'H350', 20, 'Actif', true, '2024-03-10'),
('3104 TAB', 'Mercedes', 'Sprinter', 18, 'Maintenance', true, '2024-04-05'),
('3105 TAB', 'Toyota', 'Hiace', 14, 'Actif', true, '2024-05-12'),
('3106 TAB', 'Nissan', 'Urvan', 16, 'Actif', true, '2024-06-18'),
('3107 TAB', 'Peugeot', '505', 14, 'Retiré', false, '2023-08-22'),
('3108 FE', 'Isuzu', 'NQR', 20, 'Actif', true, '2024-07-30');

-- ============================================
-- DRIVERS (Chauffeurs)
-- ============================================
INSERT INTO public.drivers (full_name, license_number, phone, status, created_at) VALUES
('Jean Rakoto', 'PER-2020-001234', '+261 32 12 34 567', 'Actif', '2020-03-15'),
('Marie Randrianasolo', 'PER-2019-005678', '+261 33 45 67 890', 'Actif', '2019-07-22'),
('Pierre Andrianampoinimerina', 'PER-2021-009012', '+261 34 56 78 901', 'Actif', '2021-01-10'),
('Sofia Ramiandrasoa', 'PER-2020-003456', '+261 32 87 65 432', 'Actif', '2020-05-18'),
('Victor Randrianampoinimerina', 'PER-2022-007890', '+261 33 21 98 765', 'Actif', '2022-02-28'),
('Nathalie Ratsimandresy', 'PER-2021-011234', '+261 34 11 22 333', 'Inactif', '2021-08-05'),
('Philippe Razafindrakoto', 'PER-2023-015678', '+261 32 44 55 666', 'Actif', '2023-04-12'),
('Émilie Rasolofo', 'PER-2020-019012', '+261 33 77 88 999', 'Actif', '2020-09-30');

-- ============================================
-- PASSENGERS (Passagers)
-- ============================================
INSERT INTO public.passengers (full_name, phone) VALUES
('Rakoto Jean', '+261 32 11 11 111'),
('Randrianasolo Marie', '+261 33 22 22 222'),
('Andrianampoinimerina Pierre', '+261 34 33 33 333'),
('Ramiandrasoa Sofia', '+261 32 44 44 444'),
('Randrianampoinimerina Victor', '+261 33 55 55 555'),
('Ratsimandresy Nathalie', '+261 34 66 66 666'),
('Razafindrakoto Philippe', '+261 32 77 77 777'),
('Rasolofo Émilie', '+261 33 88 88 888'),
('Andriamampoinimerina Paul', '+261 34 99 99 999'),
('Ravelo Henriette', '+261 32 10 10 100'),
('Razafitsalama Joseph', '+261 33 20 20 200'),
('Ratsimamanga Bernadette', '+261 34 30 30 300'),
('Rafetrarimanana Laurent', '+261 32 40 40 400'),
('Rambahoaka Christine', '+261 33 50 50 500'),
('Ratsimanaro Michel', '+261 34 60 60 600'),
('Razafintsalama Jacqueline', '+261 32 70 70 700'),
('Ranjanahary Guillaume', '+261 33 80 80 800'),
('Ramaroson Isabelle', '+261 34 90 90 900'),
('Rakotoson Adrien', '+261 32 15 25 305'),
('Rafavatonina Angélique', '+261 33 25 35 405');

-- ============================================
-- ROUTES (Trajets)
-- ============================================
INSERT INTO public.routes (departure_destination, arrival_destination, distance_km, active) VALUES
-- Routes depuis Antananarivo
(1, 2, 169.5, true),
(1, 3, 395.0, true),
(1, 4, 920.0, true),
(1, 5, 580.0, true),
(1, 6, 610.0, true),
(1, 9, 385.0, true),
(1, 10, 350.0, true),

(2, 3, 235.5, true),
(3, 4, 525.0, true),
(5, 6, 45.0, true),
(5, 8, 85.0, true),
(6, 8, 90.0, true),
(9, 10, 420.0, true),

(2, 1, 169.5, true),
(3, 1, 395.0, true),
(4, 1, 920.0, true),
(5, 1, 580.0, true),
(10, 1, 350.0, true);

-- ============================================
-- ROUTE PRICES (Prix des trajets)
-- ============================================
INSERT INTO public.route_prices (route_id, trip_type_id, price, effective_date) VALUES
-- Route Antananarivo - Antsirabe (169.5 km)
(1, 1, 25000.00, '2024-01-01'),
(1, 2, 20000.00, '2024-01-01'),
(1, 3, 15000.00, '2024-01-01'),
(1, 4, 18000.00, '2024-01-01'),

(2, 1, 60000.00, '2024-01-01'),
(2, 2, 50000.00, '2024-01-01'),
(2, 3, 40000.00, '2024-01-01'),
(2, 4, 45000.00, '2024-01-01'),

(3, 1, 120000.00, '2024-01-01'),
(3, 2, 100000.00, '2024-01-01'),
(3, 3, 85000.00, '2024-01-01'),
(3, 4, 95000.00, '2024-01-01'),

(4, 1, 85000.00, '2024-01-01'),
(4, 2, 70000.00, '2024-01-01'),
(4, 3, 60000.00, '2024-01-01'),
(4, 4, 65000.00, '2024-01-01'),

(5, 1, 90000.00, '2024-01-01'),
(5, 2, 75000.00, '2024-01-01'),
(5, 3, 65000.00, '2024-01-01'),
(5, 4, 70000.00, '2024-01-01'),

(6, 1, 65000.00, '2024-01-01'),
(6, 2, 55000.00, '2024-01-01'),
(6, 3, 45000.00, '2024-01-01'),
(6, 4, 50000.00, '2024-01-01'),

(7, 1, 60000.00, '2024-01-01'),
(7, 2, 50000.00, '2024-01-01'),
(7, 3, 40000.00, '2024-01-01'),
(7, 4, 45000.00, '2024-01-01'),

(8, 1, 40000.00, '2024-01-01'),
(8, 2, 35000.00, '2024-01-01'),
(8, 3, 28000.00, '2024-01-01'),
(8, 4, 32000.00, '2024-01-01'),

(9, 1, 85000.00, '2024-01-01'),
(9, 2, 70000.00, '2024-01-01'),
(9, 3, 60000.00, '2024-01-01'),
(9, 4, 65000.00, '2024-01-01'),

(10, 1, 12000.00, '2024-01-01'),
(10, 2, 10000.00, '2024-01-01'),
(10, 3, 8000.00, '2024-01-01'),
(10, 4, 9000.00, '2024-01-01'),

(11, 1, 18000.00, '2024-01-01'),
(11, 2, 15000.00, '2024-01-01'),
(11, 3, 12000.00, '2024-01-01'),
(11, 4, 14000.00, '2024-01-01'),

(12, 1, 20000.00, '2024-01-01'),
(12, 2, 17000.00, '2024-01-01'),
(12, 3, 14000.00, '2024-01-01'),
(12, 4, 16000.00, '2024-01-01'),

(13, 1, 70000.00, '2024-01-01'),
(13, 2, 60000.00, '2024-01-01'),
(13, 3, 50000.00, '2024-01-01'),
(13, 4, 55000.00, '2024-01-01'),

(14, 1, 25000.00, '2024-01-01'),
(14, 2, 20000.00, '2024-01-01'),
(14, 3, 15000.00, '2024-01-01'),
(14, 4, 18000.00, '2024-01-01'),

(15, 1, 60000.00, '2024-01-01'),
(15, 2, 50000.00, '2024-01-01'),
(15, 3, 40000.00, '2024-01-01'),
(15, 4, 45000.00, '2024-01-01'),

(16, 1, 120000.00, '2024-01-01'),
(16, 2, 100000.00, '2024-01-01'),
(16, 3, 85000.00, '2024-01-01'),
(16, 4, 95000.00, '2024-01-01'),

(17, 1, 85000.00, '2024-01-01'),
(17, 2, 70000.00, '2024-01-01'),
(17, 3, 60000.00, '2024-01-01'),
(17, 4, 65000.00, '2024-01-01'),

(18, 1, 60000.00, '2024-01-01'),
(18, 2, 50000.00, '2024-01-01'),
(18, 3, 40000.00, '2024-01-01'),
(18, 4, 45000.00, '2024-01-01');


-- ============================================
-- FIN DES DONNÉES
-- ============================================
