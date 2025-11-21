-- Crea las tablas stay_images y stay_unit_images, y añade la columna description a stays.

-- 1) Añade columna description a stays
ALTER TABLE IF EXISTS stays
  ADD COLUMN IF NOT EXISTS description TEXT;

-- 2) Crea tabla stay_images (relacionada con stays)
CREATE TABLE IF NOT EXISTS stay_images (
  id BIGSERIAL PRIMARY KEY,
  link VARCHAR(2048) NOT NULL,
  stay_id BIGINT NOT NULL,
  CONSTRAINT fk_stay_images_stay
    FOREIGN KEY (stay_id) REFERENCES stays (id) ON DELETE CASCADE
);

-- Índice para búsquedas por stay_id
CREATE INDEX IF NOT EXISTS idx_stay_images_stay_id ON stay_images (stay_id);
