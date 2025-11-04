-- StayType table
CREATE TABLE stay_types (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Stays table
CREATE TABLE stays (
    id SERIAL PRIMARY KEY,
    city_id INT NOT NULL REFERENCES cities(id) ON DELETE RESTRICT,
    stay_type_id INT NOT NULL REFERENCES stay_types(id) ON DELETE RESTRICT,
    name VARCHAR(255) NOT NULL,
    address TEXT NOT NULL,
    latitude DECIMAL(10,8) NOT NULL,
    longitude DECIMAL(11,8) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_stays_city ON stays(city_id);
CREATE INDEX idx_stays_type ON stays(stay_type_id);
CREATE INDEX idx_stays_coordinates ON stays(latitude, longitude);

-- Services table
CREATE TABLE services (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    icon VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- StayService junction table
CREATE TABLE stay_services (
    id SERIAL PRIMARY KEY,
    stay_id INT NOT NULL REFERENCES stays(id) ON DELETE CASCADE,
    service_id INT NOT NULL REFERENCES services(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    UNIQUE(stay_id, service_id)
);

CREATE INDEX idx_stay_services_stay ON stay_services(stay_id);
CREATE INDEX idx_stay_services_service ON stay_services(service_id);

-- StayUnits table
CREATE TABLE stay_units (
    id SERIAL PRIMARY KEY,
    stay_id INT NOT NULL REFERENCES stays(id) ON DELETE CASCADE,
    stay_number VARCHAR(50) NOT NULL,
    number_of_beds INT NOT NULL CHECK (number_of_beds > 0),
    capacity INT NOT NULL CHECK (capacity > 0),
    price_per_night DECIMAL(10,2) NOT NULL CHECK (price_per_night >= 0),
    room_type VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    UNIQUE(stay_id, stay_number)
);

CREATE INDEX idx_stay_units_stay ON stay_units(stay_id);

-- triggers for updated_at columns
CREATE TRIGGER update_stay_types_updated_at BEFORE UPDATE ON stay_types
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_stays_updated_at BEFORE UPDATE ON stays
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_services_updated_at BEFORE UPDATE ON services
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_stay_units_updated_at BEFORE UPDATE ON stay_units
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
