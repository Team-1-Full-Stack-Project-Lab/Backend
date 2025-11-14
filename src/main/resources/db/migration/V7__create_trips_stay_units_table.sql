CREATE TABLE trips_stay_units (
	trip_id BIGINT NOT NULL REFERENCES trips(id),
	stay_unit_id BIGINT NOT NULL REFERENCES stay_units(id),
	start_date DATE NOT NULL,
	end_date DATE NOT NULL,
	created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (trip_id, stay_unit_id)
);

CREATE INDEX idx_trips_stay_units_trip_id ON trips_stay_units(trip_id);
CREATE INDEX idx_trips_stay_units_stay_unit_id ON trips_stay_units(stay_unit_id);
CREATE INDEX idx_trips_stay_units_dates ON trips_stay_units(start_date, end_date);
