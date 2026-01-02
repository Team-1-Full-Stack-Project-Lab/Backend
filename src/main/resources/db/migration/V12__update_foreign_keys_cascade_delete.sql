-- ==============================================================================
-- TRIPS TABLE
-- ==============================================================================

ALTER TABLE trips
DROP CONSTRAINT IF EXISTS trips_user_id_fkey;

ALTER TABLE trips
ADD CONSTRAINT trips_user_id_fkey
FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE trips
DROP CONSTRAINT IF EXISTS trips_city_id_fkey;

ALTER TABLE trips
ADD CONSTRAINT trips_city_id_fkey
FOREIGN KEY (city_id) REFERENCES cities(id) ON DELETE CASCADE;

-- ==============================================================================
-- TRIPS_STAY_UNITS TABLE
-- ==============================================================================

ALTER TABLE trips_stay_units
DROP CONSTRAINT IF EXISTS trips_stay_units_trip_id_fkey;

ALTER TABLE trips_stay_units
ADD CONSTRAINT trips_stay_units_trip_id_fkey
FOREIGN KEY (trip_id) REFERENCES trips(id) ON DELETE CASCADE;

ALTER TABLE trips_stay_units
DROP CONSTRAINT IF EXISTS trips_stay_units_stay_unit_id_fkey;

ALTER TABLE trips_stay_units
ADD CONSTRAINT trips_stay_units_stay_unit_id_fkey
FOREIGN KEY (stay_unit_id) REFERENCES stay_units(id) ON DELETE CASCADE;

-- ==============================================================================
-- STAYS TABLE
-- ==============================================================================

ALTER TABLE stays
DROP CONSTRAINT IF EXISTS stays_city_id_fkey;

ALTER TABLE stays
ADD CONSTRAINT stays_city_id_fkey
FOREIGN KEY (city_id) REFERENCES cities(id) ON DELETE CASCADE;

ALTER TABLE stays
DROP CONSTRAINT IF EXISTS stays_stay_type_id_fkey;

ALTER TABLE stays
ADD CONSTRAINT stays_stay_type_id_fkey
FOREIGN KEY (stay_type_id) REFERENCES stay_types(id) ON DELETE CASCADE;

-- ==============================================================================
-- CITIES TABLE
-- ==============================================================================

ALTER TABLE cities
DROP CONSTRAINT IF EXISTS cities_country_id_fkey;

ALTER TABLE cities
ADD CONSTRAINT cities_country_id_fkey
FOREIGN KEY (country_id) REFERENCES countries(id) ON DELETE CASCADE;

ALTER TABLE cities
DROP CONSTRAINT IF EXISTS cities_state_id_fkey;

ALTER TABLE cities
ADD CONSTRAINT cities_state_id_fkey
FOREIGN KEY (state_id) REFERENCES states(id) ON DELETE SET NULL;

-- ==============================================================================
-- COUNTRIES TABLE
-- ==============================================================================

ALTER TABLE countries
DROP CONSTRAINT IF EXISTS countries_region_id_fkey;

ALTER TABLE countries
ADD CONSTRAINT countries_region_id_fkey
FOREIGN KEY (region_id) REFERENCES regions(id) ON DELETE SET NULL;

-- ==============================================================================
-- STAY_SERVICES TABLE (junction table - already CASCADE)
-- ==============================================================================

ALTER TABLE stay_services
DROP CONSTRAINT IF EXISTS stay_services_stay_id_fkey;

ALTER TABLE stay_services
ADD CONSTRAINT stay_services_stay_id_fkey
FOREIGN KEY (stay_id) REFERENCES stays(id) ON DELETE CASCADE;

ALTER TABLE stay_services
DROP CONSTRAINT IF EXISTS stay_services_service_id_fkey;

ALTER TABLE stay_services
ADD CONSTRAINT stay_services_service_id_fkey
FOREIGN KEY (service_id) REFERENCES services(id) ON DELETE CASCADE;

-- ==============================================================================
-- STAY_UNITS TABLE (already CASCADE)
-- ==============================================================================

ALTER TABLE stay_units
DROP CONSTRAINT IF EXISTS stay_units_stay_id_fkey;

ALTER TABLE stay_units
ADD CONSTRAINT stay_units_stay_id_fkey
FOREIGN KEY (stay_id) REFERENCES stays(id) ON DELETE CASCADE;

-- ==============================================================================
-- STAY_IMAGES TABLE (already CASCADE)
-- ==============================================================================

ALTER TABLE stay_images
DROP CONSTRAINT IF EXISTS fk_stay_images_stay;

ALTER TABLE stay_images
ADD CONSTRAINT fk_stay_images_stay
FOREIGN KEY (stay_id) REFERENCES stays(id) ON DELETE CASCADE;

-- ==============================================================================
-- COMPANIES TABLE
-- ==============================================================================

ALTER TABLE companies
DROP CONSTRAINT IF EXISTS companies_user_id_fkey;

ALTER TABLE companies
ADD CONSTRAINT companies_user_id_fkey
FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;
