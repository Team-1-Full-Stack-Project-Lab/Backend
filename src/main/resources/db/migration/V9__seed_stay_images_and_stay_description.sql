-- Seed de descripciones y de imágenes para los stays ya insertados por V6__seed_stays.sql

BEGIN;

-- 1) Añadir descripción para cada stay
UPDATE stays
SET description = 'Hotel clásico en el centro de Santiago con habitaciones confortables, desayuno incluido y piscina.'
WHERE name = 'Hotel Plaza San Francisco' AND (description IS NULL OR description = '');

UPDATE stays
SET description = 'Lujo y elegancia en el corazón de París. Servicio de primer nivel, decoración histórica y excelente ubicación.'
WHERE name = 'Le Meurice' AND (description IS NULL OR description = '');

UPDATE stays
SET description = 'Casa acogedora en Palermo, ideal para grupos o familias. Cocina equipada y barrio con vida nocturna y restaurantes.'
WHERE name = 'Palermo House' AND (description IS NULL OR description = '');

UPDATE stays
SET description = 'Casa frente a la playa en Bondi, perfecta para surfistas y viajeros que buscan sol. Espacios amplios y vista al mar.'
WHERE name = 'Bondi Beach House' AND (description IS NULL OR description = '');

UPDATE stays
SET description = 'Cabaña junto al lago en Muskoka. Entorno natural, chimenea y actividades al aire libre.'
WHERE name = 'Muskoka Lake Cabin' AND (description IS NULL OR description = '');

UPDATE stays
SET description = 'Cabaña en Cerro Alegre con vistas al océano. Barrio pintoresco, terrazas y acceso a actividades culturales.'
WHERE name = 'Cerro Alegre Cabin' AND (description IS NULL OR description = '');

-- 2) Insertar imágenes de ejemplo para cada stay
-- Hotel Plaza San Francisco
WITH s AS (SELECT id FROM stays WHERE name = 'Hotel Plaza San Francisco' LIMIT 1)
INSERT INTO stay_images (link, stay_id)
SELECT 'https://image-tc.galaxy.tf/wijpeg-lcuvdz3qqqqg75tlw5on7z8/mg-1151-ok-7-orig.jpg?width=1920', s.id FROM s
WHERE NOT EXISTS (
  SELECT 1 FROM stay_images si WHERE si.stay_id = s.id AND si.link = 'https://image-tc.galaxy.tf/wijpeg-lcuvdz3qqqqg75tlw5on7z8/mg-1151-ok-7-orig.jpg?width=1920'
);
WITH s AS (SELECT id FROM stays WHERE name = 'Hotel Plaza San Francisco' LIMIT 1)
INSERT INTO stay_images (link, stay_id)
SELECT 'https://image-tc.galaxy.tf/wijpeg-74e7oc8imj65zkf69s5blvmz7/hotel-san-francisco-dressler-1644-orig.jpg?width=1920', s.id FROM s
WHERE NOT EXISTS (
  SELECT 1 FROM stay_images si WHERE si.stay_id = s.id AND si.link = 'https://image-tc.galaxy.tf/wijpeg-74e7oc8imj65zkf69s5blvmz7/hotel-san-francisco-dressler-1644-orig.jpg?width=1920'
);
WITH s AS (SELECT id FROM stays WHERE name = 'Hotel Plaza San Francisco' LIMIT 1)
INSERT INTO stay_images (link, stay_id)
SELECT 'https://cf.bstatic.com/xdata/images/hotel/max1024x768/15961322.jpg?k=e9567a968bcfae42adf76920fd2f02324a4f3c2d6e727ba6b400c0a1a3663780&o=', s.id FROM s
WHERE NOT EXISTS (
  SELECT 1 FROM stay_images si WHERE si.stay_id = s.id AND si.link = 'https://cf.bstatic.com/xdata/images/hotel/max1024x768/15961322.jpg?k=e9567a968bcfae42adf76920fd2f02324a4f3c2d6e727ba6b400c0a1a3663780&o='
);

-- Le Meurice
WITH s AS (SELECT id FROM stays WHERE name = 'Le Meurice' LIMIT 1)
INSERT INTO stay_images (link, stay_id)
SELECT 'https://strawberrymilkevents.com/wp-content/uploads/2014/03/le-meurice-paris-1.jpg', s.id FROM s
WHERE NOT EXISTS (
  SELECT 1 FROM stay_images si WHERE si.stay_id = s.id AND si.link = 'https://strawberrymilkevents.com/wp-content/uploads/2014/03/le-meurice-paris-1.jpg'
);
WITH s AS (SELECT id FROM stays WHERE name = 'Le Meurice' LIMIT 1)
INSERT INTO stay_images (link, stay_id)
SELECT 'https://content-viajes.nationalgeographic.com.es/medio/2021/12/02/le-meurice_9cdff39f_1200x630.png', s.id FROM s
WHERE NOT EXISTS (
  SELECT 1 FROM stay_images si WHERE si.stay_id = s.id AND si.link = 'https://content-viajes.nationalgeographic.com.es/medio/2021/12/02/le-meurice_9cdff39f_1200x630.png'
);
WITH s AS (SELECT id FROM stays WHERE name = 'Le Meurice' LIMIT 1)
INSERT INTO stay_images (link, stay_id)
SELECT 'https://images.trvl-media.com/lodging/1000000/450000/441700/441665/46f488ba.jpg?impolicy=resizecrop&rw=575&rh=575&ra=fill', s.id FROM s
WHERE NOT EXISTS (
  SELECT 1 FROM stay_images si WHERE si.stay_id = s.id AND si.link = 'https://images.trvl-media.com/lodging/1000000/450000/441700/441665/46f488ba.jpg?impolicy=resizecrop&rw=575&rh=575&ra=fill'
);

-- Palermo House
WITH s AS (SELECT id FROM stays WHERE name = 'Palermo House' LIMIT 1)
INSERT INTO stay_images (link, stay_id)
SELECT 'https://media.architecturaldigest.com/photos/5801061106d6622c7c27fdd3/16:9/w_2560%2Cc_limit/casina-cinese-palermo-italy-2.jpg', s.id FROM s
WHERE NOT EXISTS (
  SELECT 1 FROM stay_images si WHERE si.stay_id = s.id AND si.link = 'https://media.architecturaldigest.com/photos/5801061106d6622c7c27fdd3/16:9/w_2560%2Cc_limit/casina-cinese-palermo-italy-2.jpg'
);
WITH s AS (SELECT id FROM stays WHERE name = 'Palermo House' LIMIT 1)
INSERT INTO stay_images (link, stay_id)
SELECT 'https://cdn.shopify.com/s/files/1/0475/5842/6788/files/Palermo_House_-_Find_Your_Cozy.jpg?v=1743629059', s.id FROM s
WHERE NOT EXISTS (
  SELECT 1 FROM stay_images si WHERE si.stay_id = s.id AND si.link = 'https://cdn.shopify.com/s/files/1/0475/5842/6788/files/Palermo_House_-_Find_Your_Cozy.jpg?v=1743629059'
);
WITH s AS (SELECT id FROM stays WHERE name = 'Palermo House' LIMIT 1)
INSERT INTO stay_images (link, stay_id)
SELECT 'https://media.licdn.com/dms/image/v2/C4E1BAQFsuKdgalnhQQ/company-background_10000/company-background_10000/0/1639436829591/palermohouse_cover?e=2147483647&v=beta&t=uLz700tyw0ol0URoACGKjgqmzoLhkTIfiPwTdMVCJhI', s.id FROM s
WHERE NOT EXISTS (
  SELECT 1 FROM stay_images si WHERE si.stay_id = s.id AND si.link = 'https://media.licdn.com/dms/image/v2/C4E1BAQFsuKdgalnhQQ/company-background_10000/company-background_10000/0/1639436829591/palermohouse_cover?e=2147483647&v=beta&t=uLz700tyw0ol0URoACGKjgqmzoLhkTIfiPwTdMVCJhI'
);

-- Bondi Beach House
WITH s AS (SELECT id FROM stays WHERE name = 'Bondi Beach House' LIMIT 1)
INSERT INTO stay_images (link, stay_id)
SELECT 'https://www.huntingforgeorge.com/wp-content/uploads/Feature-North-Bondi-House-Josephine-Hurley-Architecture-Hunting-for-George-116636-Frontofhouse.jpg', s.id FROM s
WHERE NOT EXISTS (
  SELECT 1 FROM stay_images si WHERE si.stay_id = s.id AND si.link = 'https://www.huntingforgeorge.com/wp-content/uploads/Feature-North-Bondi-House-Josephine-Hurley-Architecture-Hunting-for-George-116636-Frontofhouse.jpg'
);
WITH s AS (SELECT id FROM stays WHERE name = 'Bondi Beach House' LIMIT 1)
INSERT INTO stay_images (link, stay_id)
SELECT 'https://cf.bstatic.com/xdata/images/hotel/max1024x768/276205322.jpg?k=e875b2354e51db8acc3b949fb0b85728be5287ca542fe9e1811a6fb2443602b2&o=', s.id FROM s
WHERE NOT EXISTS (
  SELECT 1 FROM stay_images si WHERE si.stay_id = s.id AND si.link = 'https://cf.bstatic.com/xdata/images/hotel/max1024x768/276205322.jpg?k=e875b2354e51db8acc3b949fb0b85728be5287ca542fe9e1811a6fb2443602b2&o='
);
WITH s AS (SELECT id FROM stays WHERE name = 'Bondi Beach House' LIMIT 1)
INSERT INTO stay_images (link, stay_id)
SELECT 'https://images.squarespace-cdn.com/content/v1/688c5c9751a61122ca119e57/d223c148-603f-46d8-89c6-65fa47db8e66/Tamarama+Beachfront+House+-+Bondi+Beach+Holiday+Homes2.webp', s.id FROM s
WHERE NOT EXISTS (
  SELECT 1 FROM stay_images si WHERE si.stay_id = s.id AND si.link = 'https://images.squarespace-cdn.com/content/v1/688c5c9751a61122ca119e57/d223c148-603f-46d8-89c6-65fa47db8e66/Tamarama+Beachfront+House+-+Bondi+Beach+Holiday+Homes2.webp'
);

-- Muskoka Lake Cabin
WITH s AS (SELECT id FROM stays WHERE name = 'Muskoka Lake Cabin' LIMIT 1)
INSERT INTO stay_images (link, stay_id)
SELECT 'https://i0.wp.com/rumemagazine.com/wp-content/uploads/2025/07/rosseau-manner-muskoka-cottage.webp', s.id FROM s
WHERE NOT EXISTS (
  SELECT 1 FROM stay_images si WHERE si.stay_id = s.id AND si.link = 'https://i0.wp.com/rumemagazine.com/wp-content/uploads/2025/07/rosseau-manner-muskoka-cottage.webp'
);
WITH s AS (SELECT id FROM stays WHERE name = 'Muskoka Lake Cabin' LIMIT 1)
INSERT INTO stay_images (link, stay_id)
SELECT 'https://cf.bstatic.com/xdata/images/hotel/max1024x768/558430800.jpg?k=17e148a044a281615814ddef40f805dad13ff9f3480f863bb08225d1ec2d00b2&o=', s.id FROM s
WHERE NOT EXISTS (
  SELECT 1 FROM stay_images si WHERE si.stay_id = s.id AND si.link = 'https://cf.bstatic.com/xdata/images/hotel/max1024x768/558430800.jpg?k=17e148a044a281615814ddef40f805dad13ff9f3480f863bb08225d1ec2d00b2&o='
);
WITH s AS (SELECT id FROM stays WHERE name = 'Muskoka Lake Cabin' LIMIT 1)
INSERT INTO stay_images (link, stay_id)
SELECT 'https://www.cottagevacations.com/wp-content/uploads/2025/11/xdby3w71qikqe27sdzqh.jpg', s.id FROM s
WHERE NOT EXISTS (
  SELECT 1 FROM stay_images si WHERE si.stay_id = s.id AND si.link = 'https://www.cottagevacations.com/wp-content/uploads/2025/11/xdby3w71qikqe27sdzqh.jpg'
);

-- Cerro Alegre Cabin
WITH s AS (SELECT id FROM stays WHERE name = 'Cerro Alegre Cabin' LIMIT 1)
INSERT INTO stay_images (link, stay_id)
SELECT 'https://dynamic-media-cdn.tripadvisor.com/media/photo-o/14/7a/1c/9a/entrada.jpg?w=900&h=500&s=1', s.id FROM s
WHERE NOT EXISTS (
  SELECT 1 FROM stay_images si WHERE si.stay_id = s.id AND si.link = 'https://dynamic-media-cdn.tripadvisor.com/media/photo-o/14/7a/1c/9a/entrada.jpg?w=900&h=500&s=1'
);
WITH s AS (SELECT id FROM stays WHERE name = 'Cerro Alegre Cabin' LIMIT 1)
INSERT INTO stay_images (link, stay_id)
SELECT 'https://a0.muscache.com/im/pictures/hosting/Hosting-1332638704717942293/original/acaaf5ac-8b46-4061-a206-6a220411e98b.jpeg', s.id FROM s
WHERE NOT EXISTS (
  SELECT 1 FROM stay_images si WHERE si.stay_id = s.id AND si.link = 'https://a0.muscache.com/im/pictures/hosting/Hosting-1332638704717942293/original/acaaf5ac-8b46-4061-a206-6a220411e98b.jpeg'
);
WITH s AS (SELECT id FROM stays WHERE name = 'Cerro Alegre Cabin' LIMIT 1)
INSERT INTO stay_images (link, stay_id)
SELECT 'https://a0.muscache.com/im/pictures/d6ce61b5-87e5-4f45-b54d-42f2f7ef9a55.jpg', s.id FROM s
WHERE NOT EXISTS (
  SELECT 1 FROM stay_images si WHERE si.stay_id = s.id AND si.link = 'https://a0.muscache.com/im/pictures/d6ce61b5-87e5-4f45-b54d-42f2f7ef9a55.jpg'
);

COMMIT;
