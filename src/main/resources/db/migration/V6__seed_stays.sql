-- Seed Stay Types
INSERT INTO stay_types (name)
VALUES
    ('Hotel'),
    ('House'),
    ('Cabin')
ON CONFLICT (name) DO NOTHING;

-- Seed Services with shadcn/lucide-react icon names
INSERT INTO services (name, icon)
VALUES
    ('WiFi', 'Wifi'),
    ('Swimming Pool', 'Waves'),
    ('Air Conditioning', 'Wind'),
    ('Kitchen', 'ChefHat'),
    ('Breakfast Included', 'Coffee'),
    ('Pet Friendly', 'Dog'),
    ('Gym', 'Dumbbell'),
    ('Laundry', 'WashingMachine'),
    ('Room Service', 'ConciergeBell'),
    ('TV', 'Tv'),
    ('Balcony', 'Home'),
    ('Private Bathroom', 'ShowerHead')
ON CONFLICT (name) DO NOTHING;

-- Seed Stays: 2 Hotels, 2 Houses, 2 Cabins
INSERT INTO stays (city_id, stay_type_id, name, address, latitude, longitude)
VALUES
    -- Hotel 1: Santiago, Chile
    ((SELECT id FROM cities WHERE name='Santiago' AND country_id=(SELECT id FROM countries WHERE iso2_code='CL')),
     (SELECT id FROM stay_types WHERE name='Hotel'),
     'Hotel Plaza San Francisco', 'Alameda 816, Santiago', -33.4378, -70.6503),

    -- Hotel 2: Paris, France
    ((SELECT id FROM cities WHERE name='Paris' AND country_id=(SELECT id FROM countries WHERE iso2_code='FR')),
     (SELECT id FROM stay_types WHERE name='Hotel'),
     'Le Meurice', 'Rue de Rivoli 228, Paris', 48.8655, 2.3279),

    -- House 1: Buenos Aires, Argentina
    ((SELECT id FROM cities WHERE name='Buenos Aires' AND country_id=(SELECT id FROM countries WHERE iso2_code='AR')),
     (SELECT id FROM stay_types WHERE name='House'),
     'Palermo House', 'Honduras 4800, Palermo', -34.5869, -58.4263),

    -- House 2: Sydney, Australia
    ((SELECT id FROM cities WHERE name='Sydney' AND country_id=(SELECT id FROM countries WHERE iso2_code='AU')),
     (SELECT id FROM stay_types WHERE name='House'),
     'Bondi Beach House', '178 Campbell Parade, Bondi Beach', -33.8915, 151.2767),

    -- Cabin 1: Toronto, Canada
    ((SELECT id FROM cities WHERE name='Toronto' AND country_id=(SELECT id FROM countries WHERE iso2_code='CA')),
     (SELECT id FROM stay_types WHERE name='Cabin'),
     'Muskoka Lake Cabin', 'Lake Muskoka Rd, Muskoka', 43.7500, -79.5000),

    -- Cabin 2: Valparaíso, Chile
    ((SELECT id FROM cities WHERE name='Valparaíso' AND country_id=(SELECT id FROM countries WHERE iso2_code='CL')),
     (SELECT id FROM stay_types WHERE name='Cabin'),
     'Cerro Alegre Cabin', 'Cerro Alegre 245, Valparaíso', -33.0422, -71.6272)
ON CONFLICT DO NOTHING;

-- Seed Stay Units (2-3 units per stay)
INSERT INTO stay_units (stay_id, stay_number, number_of_beds, capacity, price_per_night, room_type)
VALUES
    -- Hotel Plaza San Francisco units
    ((SELECT id FROM stays WHERE name='Hotel Plaza San Francisco'), '301', 2, 2, 120.00, 'Deluxe Double'),
    ((SELECT id FROM stays WHERE name='Hotel Plaza San Francisco'), '405', 1, 2, 150.00, 'Executive Suite'),
    ((SELECT id FROM stays WHERE name='Hotel Plaza San Francisco'), '502', 2, 4, 180.00, 'Family Room'),

    -- Le Meurice units
    ((SELECT id FROM stays WHERE name='Le Meurice'), 'Belle Étoile', 1, 2, 1200.00, 'Prestige Suite'),
    ((SELECT id FROM stays WHERE name='Le Meurice'), 'Fontaine', 2, 3, 950.00, 'Deluxe Suite'),

    -- Palermo House units
    ((SELECT id FROM stays WHERE name='Palermo House'), 'Main House', 3, 6, 200.00, 'Entire House'),
    ((SELECT id FROM stays WHERE name='Palermo House'), 'Studio', 1, 2, 80.00, 'Studio Apartment'),

    -- Bondi Beach House units
    ((SELECT id FROM stays WHERE name='Bondi Beach House'), 'Entire House', 4, 8, 350.00, 'Beach House'),
    ((SELECT id FROM stays WHERE name='Bondi Beach House'), 'Upper Floor', 2, 4, 180.00, 'Upper Floor Suite'),

    -- Muskoka Lake Cabin units
    ((SELECT id FROM stays WHERE name='Muskoka Lake Cabin'), 'Lakefront Cabin', 2, 4, 180.00, 'Entire Cabin'),
    ((SELECT id FROM stays WHERE name='Muskoka Lake Cabin'), 'Loft', 1, 2, 120.00, 'Cabin Loft'),

    -- Cerro Alegre Cabin units
    ((SELECT id FROM stays WHERE name='Cerro Alegre Cabin'), 'Ocean View', 2, 3, 95.00, 'Entire Cabin'),
    ((SELECT id FROM stays WHERE name='Cerro Alegre Cabin'), 'Mountain View', 1, 2, 75.00, 'Cozy Room')
ON CONFLICT DO NOTHING;

-- Seed Stay Services (linking stays with their amenities)

-- Hotel Plaza San Francisco services
INSERT INTO stay_services (stay_id, service_id)
SELECT
    (SELECT id FROM stays WHERE name='Hotel Plaza San Francisco'),
    id
FROM services
WHERE name IN ('WiFi', 'Swimming Pool', 'Air Conditioning', 'Room Service', 'TV', 'Breakfast Included', 'Gym', 'Laundry', 'Private Bathroom')
ON CONFLICT DO NOTHING;

-- Le Meurice services
INSERT INTO stay_services (stay_id, service_id)
SELECT
    (SELECT id FROM stays WHERE name='Le Meurice'),
    id
FROM services
WHERE name IN ('WiFi', 'Air Conditioning', 'Room Service', 'TV', 'Breakfast Included', 'Gym', 'Laundry', 'Balcony', 'Private Bathroom')
ON CONFLICT DO NOTHING;

-- Palermo House services
INSERT INTO stay_services (stay_id, service_id)
SELECT
    (SELECT id FROM stays WHERE name='Palermo House'),
    id
FROM services
WHERE name IN ('WiFi', 'Kitchen', 'Air Conditioning', 'TV', 'Laundry', 'Balcony', 'Pet Friendly', 'Private Bathroom')
ON CONFLICT DO NOTHING;

-- Bondi Beach House services
INSERT INTO stay_services (stay_id, service_id)
SELECT
    (SELECT id FROM stays WHERE name='Bondi Beach House'),
    id
FROM services
WHERE name IN ('WiFi', 'Kitchen', 'Air Conditioning', 'TV', 'Laundry', 'Balcony', 'Pet Friendly', 'Private Bathroom')
ON CONFLICT DO NOTHING;

-- Muskoka Lake Cabin services
INSERT INTO stay_services (stay_id, service_id)
SELECT
    (SELECT id FROM stays WHERE name='Muskoka Lake Cabin'),
    id
FROM services
WHERE name IN ('WiFi', 'Kitchen', 'TV', 'Pet Friendly', 'Private Bathroom', 'Balcony')
ON CONFLICT DO NOTHING;

-- Cerro Alegre Cabin services
INSERT INTO stay_services (stay_id, service_id)
SELECT
    (SELECT id FROM stays WHERE name='Cerro Alegre Cabin'),
    id
FROM services
WHERE name IN ('WiFi', 'Kitchen', 'TV', 'Balcony', 'Pet Friendly', 'Private Bathroom')
ON CONFLICT DO NOTHING;
