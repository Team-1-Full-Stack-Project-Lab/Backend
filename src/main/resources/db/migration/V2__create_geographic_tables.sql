CREATE TABLE regions (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    code VARCHAR(10) UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE countries (
	id SERIAL PRIMARY KEY,
	name VARCHAR(100) NOT NULL,
	iso2_code CHAR(2) NOT NULL UNIQUE,
    iso3_code CHAR(3) NOT NULL UNIQUE,
	phone_code VARCHAR(10),
	currency_code CHAR(3),
	currency_symbol VARCHAR(5),
    region_id INT REFERENCES regions(id) ON DELETE SET NULL,
	created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_countries_region ON countries(region_id);

CREATE TABLE states (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(10),
    country_id INT NOT NULL REFERENCES countries(id) ON DELETE CASCADE,
    latitude DECIMAL(10,8),
    longitude DECIMAL(11,8),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    UNIQUE(country_id, code)
);

CREATE INDEX idx_states_country ON states(country_id);

CREATE TABLE cities (
	id SERIAL PRIMARY KEY,
	name VARCHAR(100) NOT NULL,
    name_ascii VARCHAR(100),
    country_id INT NOT NULL REFERENCES countries(id) ON DELETE RESTRICT,
    state_id INT REFERENCES states(id) ON DELETE SET NULL,
	latitude DECIMAL(10,8) NOT NULL,
	longitude DECIMAL(11,8) NOT NULL,
	timezone VARCHAR(50),
	google_place_id VARCHAR(255),
	population INT,
    is_capital BOOLEAN DEFAULT FALSE,
    is_featured BOOLEAN DEFAULT FALSE,
	created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_cities_country ON cities(country_id);
CREATE INDEX idx_cities_country_name ON cities(country_id, name);
CREATE INDEX idx_cities_state ON cities(state_id);
CREATE INDEX idx_cities_coordinates ON cities(latitude, longitude);
CREATE INDEX idx_cities_featured ON cities(is_featured) WHERE is_featured = TRUE;

CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_countries_updated_at BEFORE UPDATE ON countries
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_states_updated_at BEFORE UPDATE ON states
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_cities_updated_at BEFORE UPDATE ON cities
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
