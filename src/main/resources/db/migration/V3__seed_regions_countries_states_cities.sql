INSERT INTO regions (name, code)
VALUES
    ('Africa', 'AF'),
    ('Asia', 'AS'),
    ('Europe', 'EU'),
    ('North America', 'NA'),
    ('South America', 'SA'),
    ('Oceania', 'OC')
ON CONFLICT (code) DO NOTHING;

INSERT INTO countries (name, iso2_code, iso3_code, phone_code, currency_code, currency_symbol, region_id)
VALUES
    ('Chile', 'CL', 'CHL', '+56', 'CLP', '$', (SELECT id FROM regions WHERE code='SA')),
    ('Argentina', 'AR', 'ARG', '+54', 'ARS', '$', (SELECT id FROM regions WHERE code='SA')),
    ('United States', 'US', 'USA', '+1', 'USD', '$', (SELECT id FROM regions WHERE code='NA')),
    ('Mexico', 'MX', 'MEX', '+52', 'MXN', '$', (SELECT id FROM regions WHERE code='NA')),
    ('Spain', 'ES', 'ESP', '+34', 'EUR', '€', (SELECT id FROM regions WHERE code='EU')),
    ('France', 'FR', 'FRA', '+33', 'EUR', '€', (SELECT id FROM regions WHERE code='EU')),
    ('Japan', 'JP', 'JPN', '+81', 'JPY', '¥', (SELECT id FROM regions WHERE code='AS')),
    ('Australia', 'AU', 'AUS', '+61', 'AUD', '$', (SELECT id FROM regions WHERE code='OC')),
    ('Brazil', 'BR', 'BRA', '+55', 'BRL', 'R$', (SELECT id FROM regions WHERE code='SA')),
    ('Canada', 'CA', 'CAN', '+1', 'CAD', '$', (SELECT id FROM regions WHERE code='NA'))
ON CONFLICT (iso2_code) DO NOTHING;

INSERT INTO states (name, code, country_id, latitude, longitude)
VALUES
    ('Región Metropolitana', 'RM', (SELECT id FROM countries WHERE iso2_code='CL'), -33.45, -70.66),
    ('Buenos Aires', 'BA', (SELECT id FROM countries WHERE iso2_code='AR'), -34.60, -58.38),
    ('California', 'CA', (SELECT id FROM countries WHERE iso2_code='US'), 36.7783, -119.4179),
    ('Ciudad de México', 'CMX', (SELECT id FROM countries WHERE iso2_code='MX'), 19.43, -99.13),
    ('Madrid', 'MD', (SELECT id FROM countries WHERE iso2_code='ES'), 40.42, -3.70),
    ('Île-de-France', 'IDF', (SELECT id FROM countries WHERE iso2_code='FR'), 48.85, 2.35),
    ('Tokyo Prefecture', 'TK', (SELECT id FROM countries WHERE iso2_code='JP'), 35.68, 139.69),
    ('New South Wales', 'NSW', (SELECT id FROM countries WHERE iso2_code='AU'), -33.86, 151.21),
    ('São Paulo', 'SP', (SELECT id FROM countries WHERE iso2_code='BR'), -23.55, -46.63),
    ('Ontario', 'ON', (SELECT id FROM countries WHERE iso2_code='CA'), 43.65, -79.38)
ON CONFLICT (country_id, code) DO NOTHING;

INSERT INTO cities (name, name_ascii, country_id, state_id, latitude, longitude, timezone, google_place_id, population, is_capital, is_featured)
VALUES
    ('Santiago', 'Santiago',
        (SELECT id FROM countries WHERE iso2_code='CL'),
        (SELECT id FROM states WHERE code='RM'),
        -33.4489, -70.6693, 'America/Santiago', 'ChIJL68lBEHFYpYRMQkPQDzVdYQ', 7000000, TRUE, TRUE),

    ('Valparaíso', 'Valparaiso',
        (SELECT id FROM countries WHERE iso2_code='CL'),
        (SELECT id FROM states WHERE code='RM'),
        -33.0472, -71.6127, 'America/Santiago', 'ChIJxR2X8LBZYpYRPHvFwjDNY1Q', 900000, FALSE, FALSE),

    ('Buenos Aires', 'Buenos Aires',
        (SELECT id FROM countries WHERE iso2_code='AR'),
        (SELECT id FROM states WHERE code='BA'),
        -34.6037, -58.3816, 'America/Argentina/Buenos_Aires', 'ChIJvQz5TjvP1ZERKx8JvEo4LzQ', 15000000, TRUE, TRUE),

    ('Los Angeles', 'Los Angeles',
        (SELECT id FROM countries WHERE iso2_code='US'),
        (SELECT id FROM states WHERE code='CA'),
        34.0522, -118.2437, 'America/Los_Angeles', 'ChIJE9on3F3HwoAR9AhGJW_fL-I', 4000000, FALSE, TRUE),

    ('New York', 'New York',
        (SELECT id FROM countries WHERE iso2_code='US'),
        (SELECT id FROM states WHERE code='CA'),
        40.7128, -74.0060, 'America/New_York', 'ChIJOwg_06VPwokRYv534QaPC8g', 8500000, FALSE, TRUE),

    ('Ciudad de México', 'Mexico City',
        (SELECT id FROM countries WHERE iso2_code='MX'),
        (SELECT id FROM states WHERE code='CMX'),
        19.4326, -99.1332, 'America/Mexico_City', 'ChIJB3UJ2yYAzoURQeheJnYQBlQ', 9200000, TRUE, TRUE),

    ('Madrid', 'Madrid',
        (SELECT id FROM countries WHERE iso2_code='ES'),
        (SELECT id FROM states WHERE code='MD'),
        40.4168, -3.7038, 'Europe/Madrid', 'ChIJgTwKgJcpQg0RaSKMYcHeNsQ', 6700000, TRUE, TRUE),

    ('Paris', 'Paris',
        (SELECT id FROM countries WHERE iso2_code='FR'),
        (SELECT id FROM states WHERE code='IDF'),
        48.8566, 2.3522, 'Europe/Paris', 'ChIJD7fiBh9u5kcRYJSMaMOCCwQ', 11000000, TRUE, TRUE),

    ('Tokyo', 'Tokyo',
        (SELECT id FROM countries WHERE iso2_code='JP'),
        (SELECT id FROM states WHERE code='TK'),
        35.6762, 139.6503, 'Asia/Tokyo', 'ChIJ51cu8IcbXWARiRtXIothAS4', 14000000, TRUE, TRUE),

    ('Sydney', 'Sydney',
        (SELECT id FROM countries WHERE iso2_code='AU'),
        (SELECT id FROM states WHERE code='NSW'),
        -33.8688, 151.2093, 'Australia/Sydney', 'ChIJP3Sa8ziYEmsRUKgyFmh9AQM', 5300000, TRUE, TRUE),

    ('São Paulo', 'Sao Paulo',
        (SELECT id FROM countries WHERE iso2_code='BR'),
        (SELECT id FROM states WHERE code='SP'),
        -23.5505, -46.6333, 'America/Sao_Paulo', 'ChIJ0WGkg4FEzpQRrlsz_whLqZs', 12000000, FALSE, TRUE),

    ('Toronto', 'Toronto',
        (SELECT id FROM countries WHERE iso2_code='CA'),
        (SELECT id FROM states WHERE code='ON'),
        43.6511, -79.3470, 'America/Toronto', 'ChIJpTvG15DL1IkRd8S0KlBVNTI', 3000000, TRUE, TRUE)
ON CONFLICT DO NOTHING;
