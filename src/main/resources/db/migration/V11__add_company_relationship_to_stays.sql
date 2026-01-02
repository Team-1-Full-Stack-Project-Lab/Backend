ALTER TABLE stays
ADD COLUMN company_id INT REFERENCES companies(id) ON DELETE SET NULL;

CREATE INDEX idx_stays_company ON stays(company_id);
