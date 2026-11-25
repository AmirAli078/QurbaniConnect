INSERT INTO roles (role_name) VALUES ('ROLE_CUSTOMER'), ('ROLE_QASAI'), ('ROLE_ADMIN');

INSERT INTO animal_categories (animal_name, urdu_name, description) VALUES
 ('GOAT',  'Bakra',        'Goat'),
 ('SHEEP', 'Dumba/Bhera',  'Sheep'),
 ('COW',   'Gai',          'Cow'),
 ('BULL',  'Bail',         'Bull'),
 ('CAMEL', 'Oont',         'Camel');

-- Default admin price limits (PKR). Admin can change them through the API.
INSERT INTO admin_price_limits (animal_category_id, minimum_price, maximum_price)
SELECT id, CASE animal_name WHEN 'GOAT' THEN 1500 WHEN 'SHEEP' THEN 1500 WHEN 'COW' THEN 6000 WHEN 'BULL' THEN 6000 ELSE 10000 END,
          CASE animal_name WHEN 'GOAT' THEN 6000 WHEN 'SHEEP' THEN 6000 WHEN 'COW' THEN 20000 WHEN 'BULL' THEN 20000 ELSE 40000 END
FROM animal_categories;

INSERT INTO cities (city_name, state_or_province) VALUES
 ('Karachi','Sindh'), ('Hyderabad','Sindh'), ('Nawabshah','Sindh'), ('Sukkur','Sindh'),
 ('Lahore','Punjab'), ('Islamabad','Islamabad Capital Territory'), ('Rawalpindi','Punjab');

INSERT INTO areas (city_id, area_name)
SELECT c.id, a.area_name FROM cities c JOIN (
  SELECT 'Karachi' city, 'Gulshan-e-Iqbal' area_name UNION ALL SELECT 'Karachi','North Nazimabad'
  UNION ALL SELECT 'Karachi','Clifton' UNION ALL SELECT 'Karachi','Korangi' UNION ALL SELECT 'Karachi','Malir'
  UNION ALL SELECT 'Hyderabad','Latifabad' UNION ALL SELECT 'Hyderabad','Qasimabad'
  UNION ALL SELECT 'Nawabshah','Model Colony' UNION ALL SELECT 'Sukkur','Military Road'
  UNION ALL SELECT 'Lahore','Gulberg' UNION ALL SELECT 'Lahore','Johar Town' UNION ALL SELECT 'Lahore','DHA'
  UNION ALL SELECT 'Islamabad','F-10' UNION ALL SELECT 'Islamabad','G-11' UNION ALL SELECT 'Islamabad','I-8'
  UNION ALL SELECT 'Rawalpindi','Bahria Town' UNION ALL SELECT 'Rawalpindi','Satellite Town'
) a ON a.city = c.city_name;
