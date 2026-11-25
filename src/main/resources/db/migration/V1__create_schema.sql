-- QurbaniConnect schema (MySQL 8.0, InnoDB, utf8mb4)

CREATE TABLE roles (
  id BIGINT NOT NULL AUTO_INCREMENT,
  role_name VARCHAR(30) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_roles_name (role_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE users (
  id BIGINT NOT NULL AUTO_INCREMENT,
  full_name VARCHAR(120) NOT NULL,
  email VARCHAR(160) NULL,
  phone_number VARCHAR(20) NOT NULL,
  password VARCHAR(100) NOT NULL,
  account_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  email_verified TINYINT(1) NOT NULL DEFAULT 0,
  phone_verified TINYINT(1) NOT NULL DEFAULT 0,
  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  PRIMARY KEY (id),
  UNIQUE KEY uk_users_email (email),
  UNIQUE KEY uk_users_phone (phone_number),
  CONSTRAINT ck_users_status CHECK (account_status IN ('ACTIVE','INACTIVE'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE user_roles (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_user_roles (user_id, role_id),
  CONSTRAINT fk_ur_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
  CONSTRAINT fk_ur_role FOREIGN KEY (role_id) REFERENCES roles (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE cities (
  id BIGINT NOT NULL AUTO_INCREMENT,
  city_name VARCHAR(80) NOT NULL,
  state_or_province VARCHAR(80) NOT NULL,
  country VARCHAR(60) NOT NULL DEFAULT 'Pakistan',
  is_active TINYINT(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (id),
  UNIQUE KEY uk_city (city_name, state_or_province)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE areas (
  id BIGINT NOT NULL AUTO_INCREMENT,
  city_id BIGINT NOT NULL,
  area_name VARCHAR(100) NOT NULL,
  is_active TINYINT(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (id),
  UNIQUE KEY uk_area (city_id, area_name),
  CONSTRAINT fk_area_city FOREIGN KEY (city_id) REFERENCES cities (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE customer_profiles (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  city_id BIGINT NULL,
  area_id BIGINT NULL,
  complete_address VARCHAR(300) NULL,
  profile_photo VARCHAR(255) NULL,
  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  PRIMARY KEY (id),
  UNIQUE KEY uk_customer_user (user_id),
  CONSTRAINT fk_cp_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
  CONSTRAINT fk_cp_city FOREIGN KEY (city_id) REFERENCES cities (id),
  CONSTRAINT fk_cp_area FOREIGN KEY (area_id) REFERENCES areas (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE qasai_profiles (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  city_id BIGINT NOT NULL,
  area_id BIGINT NULL,
  profile_photo VARCHAR(255) NULL,
  cnic_number VARCHAR(15) NOT NULL,
  years_of_experience INT NOT NULL DEFAULT 0,
  professional_description VARCHAR(1000) NULL,
  verification_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
  rejection_reason VARCHAR(500) NULL,
  approved_at DATETIME(6) NULL,
  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  PRIMARY KEY (id),
  UNIQUE KEY uk_qasai_user (user_id),
  UNIQUE KEY uk_qasai_cnic (cnic_number),
  KEY idx_qasai_city_status (city_id, verification_status),
  KEY idx_qasai_area (area_id),
  CONSTRAINT fk_qp_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
  CONSTRAINT fk_qp_city FOREIGN KEY (city_id) REFERENCES cities (id),
  CONSTRAINT fk_qp_area FOREIGN KEY (area_id) REFERENCES areas (id),
  CONSTRAINT ck_qp_status CHECK (verification_status IN ('PENDING','APPROVED','REJECTED','SUSPENDED')),
  CONSTRAINT ck_qp_exp CHECK (years_of_experience >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE qasai_verification_documents (
  id BIGINT NOT NULL AUTO_INCREMENT,
  qasai_id BIGINT NOT NULL,
  document_type VARCHAR(30) NOT NULL,
  file_name VARCHAR(255) NOT NULL,
  file_path VARCHAR(500) NOT NULL,
  verification_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
  uploaded_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  reviewed_at DATETIME(6) NULL,
  PRIMARY KEY (id),
  KEY idx_qvd_qasai (qasai_id),
  CONSTRAINT fk_qvd_qasai FOREIGN KEY (qasai_id) REFERENCES qasai_profiles (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE animal_categories (
  id BIGINT NOT NULL AUTO_INCREMENT,
  animal_name VARCHAR(40) NOT NULL,
  urdu_name VARCHAR(60) NOT NULL,
  description VARCHAR(300) NULL,
  image_path VARCHAR(255) NULL,
  is_active TINYINT(1) NOT NULL DEFAULT 1,
  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  PRIMARY KEY (id),
  UNIQUE KEY uk_animal_name (animal_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE admin_price_limits (
  id BIGINT NOT NULL AUTO_INCREMENT,
  animal_category_id BIGINT NOT NULL,
  minimum_price DECIMAL(12,2) NOT NULL,
  maximum_price DECIMAL(12,2) NOT NULL,
  is_active TINYINT(1) NOT NULL DEFAULT 1,
  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  PRIMARY KEY (id),
  UNIQUE KEY uk_limit_animal (animal_category_id),
  CONSTRAINT fk_apl_animal FOREIGN KEY (animal_category_id) REFERENCES animal_categories (id),
  CONSTRAINT ck_apl_range CHECK (minimum_price > 0 AND maximum_price >= minimum_price)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE qasai_animal_prices (
  id BIGINT NOT NULL AUTO_INCREMENT,
  qasai_id BIGINT NOT NULL,
  animal_category_id BIGINT NOT NULL,
  service_price DECIMAL(12,2) NOT NULL,
  is_available TINYINT(1) NOT NULL DEFAULT 1,
  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  PRIMARY KEY (id),
  UNIQUE KEY uk_qap (qasai_id, animal_category_id),
  KEY idx_qap_animal_price (animal_category_id, service_price),
  CONSTRAINT fk_qap_qasai FOREIGN KEY (qasai_id) REFERENCES qasai_profiles (id) ON DELETE CASCADE,
  CONSTRAINT fk_qap_animal FOREIGN KEY (animal_category_id) REFERENCES animal_categories (id),
  CONSTRAINT ck_qap_price CHECK (service_price > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE qasai_availability (
  id BIGINT NOT NULL AUTO_INCREMENT,
  qasai_id BIGINT NOT NULL,
  available_date DATE NOT NULL,
  start_time TIME NOT NULL,
  end_time TIME NOT NULL,
  slot_status VARCHAR(15) NOT NULL DEFAULT 'AVAILABLE',
  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  PRIMARY KEY (id),
  KEY idx_avail_lookup (qasai_id, available_date, slot_status),
  CONSTRAINT fk_av_qasai FOREIGN KEY (qasai_id) REFERENCES qasai_profiles (id) ON DELETE CASCADE,
  CONSTRAINT ck_av_status CHECK (slot_status IN ('AVAILABLE','BLOCKED','RESERVED')),
  CONSTRAINT ck_av_time CHECK (end_time > start_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE bookings (
  id BIGINT NOT NULL AUTO_INCREMENT,
  booking_reference VARCHAR(30) NOT NULL,
  customer_id BIGINT NOT NULL,
  qasai_id BIGINT NULL,
  booking_type VARCHAR(20) NOT NULL,
  city_id BIGINT NOT NULL,
  area_id BIGINT NOT NULL,
  service_address VARCHAR(300) NOT NULL,
  customer_phone VARCHAR(20) NOT NULL,
  preferred_date DATE NOT NULL,
  availability_id BIGINT NULL,
  total_amount DECIMAL(14,2) NOT NULL,
  price_status VARCHAR(15) NOT NULL DEFAULT 'FINAL',
  booking_status VARCHAR(15) NOT NULL DEFAULT 'PENDING',
  customer_notes VARCHAR(500) NULL,
  cancellation_reason VARCHAR(500) NULL,
  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  -- Non-NULL only while the booking holds its slot. The UNIQUE index below makes a double
  -- reservation of one slot impossible at the database level, whatever the application does.
  active_availability_id BIGINT GENERATED ALWAYS AS (
    CASE WHEN booking_status IN ('PENDING','ACCEPTED','ASSIGNED','CONFIRMED') THEN availability_id END
  ) STORED,
  PRIMARY KEY (id),
  UNIQUE KEY uk_booking_ref (booking_reference),
  UNIQUE KEY uk_booking_active_slot (active_availability_id),
  KEY idx_booking_customer (customer_id, created_at),
  KEY idx_booking_qasai_status (qasai_id, booking_status),
  KEY idx_booking_status_date (booking_status, preferred_date),
  CONSTRAINT fk_b_customer FOREIGN KEY (customer_id) REFERENCES customer_profiles (id),
  CONSTRAINT fk_b_qasai FOREIGN KEY (qasai_id) REFERENCES qasai_profiles (id),
  CONSTRAINT fk_b_city FOREIGN KEY (city_id) REFERENCES cities (id),
  CONSTRAINT fk_b_area FOREIGN KEY (area_id) REFERENCES areas (id),
  CONSTRAINT fk_b_avail FOREIGN KEY (availability_id) REFERENCES qasai_availability (id),
  CONSTRAINT ck_b_type CHECK (booking_type IN ('DIRECT','ADMIN_ASSISTED')),
  CONSTRAINT ck_b_status CHECK (booking_status IN ('PENDING','ACCEPTED','REJECTED','ASSIGNED','CONFIRMED','COMPLETED','CANCELLED')),
  CONSTRAINT ck_b_price_status CHECK (price_status IN ('FINAL','ESTIMATED')),
  CONSTRAINT ck_b_total CHECK (total_amount >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE booking_items (
  id BIGINT NOT NULL AUTO_INCREMENT,
  booking_id BIGINT NOT NULL,
  animal_category_id BIGINT NOT NULL,
  animal_name_snapshot VARCHAR(40) NOT NULL,
  quantity INT NOT NULL,
  unit_price DECIMAL(12,2) NOT NULL,
  subtotal DECIMAL(14,2) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_bi_booking_animal (booking_id, animal_category_id),
  CONSTRAINT fk_bi_booking FOREIGN KEY (booking_id) REFERENCES bookings (id) ON DELETE CASCADE,
  CONSTRAINT fk_bi_animal FOREIGN KEY (animal_category_id) REFERENCES animal_categories (id),
  CONSTRAINT ck_bi_qty CHECK (quantity > 0),
  CONSTRAINT ck_bi_price CHECK (unit_price > 0),
  CONSTRAINT ck_bi_subtotal CHECK (subtotal = quantity * unit_price)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE booking_status_history (
  id BIGINT NOT NULL AUTO_INCREMENT,
  booking_id BIGINT NOT NULL,
  previous_status VARCHAR(15) NULL,
  new_status VARCHAR(15) NOT NULL,
  changed_by BIGINT NULL,
  remarks VARCHAR(500) NULL,
  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  PRIMARY KEY (id),
  KEY idx_bsh_booking (booking_id, created_at),
  CONSTRAINT fk_bsh_booking FOREIGN KEY (booking_id) REFERENCES bookings (id) ON DELETE CASCADE,
  CONSTRAINT fk_bsh_user FOREIGN KEY (changed_by) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE qasai_assignments (
  id BIGINT NOT NULL AUTO_INCREMENT,
  booking_id BIGINT NOT NULL,
  qasai_id BIGINT NOT NULL,
  assigned_by BIGINT NOT NULL,
  assignment_status VARCHAR(15) NOT NULL DEFAULT 'PENDING',
  assigned_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  response_at DATETIME(6) NULL,
  rejection_reason VARCHAR(500) NULL,
  PRIMARY KEY (id),
  KEY idx_qa_booking (booking_id),
  KEY idx_qa_qasai_status (qasai_id, assignment_status),
  CONSTRAINT fk_qa_booking FOREIGN KEY (booking_id) REFERENCES bookings (id) ON DELETE CASCADE,
  CONSTRAINT fk_qa_qasai FOREIGN KEY (qasai_id) REFERENCES qasai_profiles (id),
  CONSTRAINT fk_qa_admin FOREIGN KEY (assigned_by) REFERENCES users (id),
  CONSTRAINT ck_qa_status CHECK (assignment_status IN ('PENDING','ACCEPTED','REJECTED','REASSIGNED','CANCELLED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE reviews (
  id BIGINT NOT NULL AUTO_INCREMENT,
  booking_id BIGINT NOT NULL,
  customer_id BIGINT NOT NULL,
  qasai_id BIGINT NOT NULL,
  rating TINYINT NOT NULL,
  review_text VARCHAR(1000) NULL,
  is_visible TINYINT(1) NOT NULL DEFAULT 1,
  moderation_note VARCHAR(300) NULL,
  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  PRIMARY KEY (id),
  UNIQUE KEY uk_review_booking (booking_id),
  KEY idx_review_qasai (qasai_id, is_visible),
  CONSTRAINT fk_r_booking FOREIGN KEY (booking_id) REFERENCES bookings (id),
  CONSTRAINT fk_r_customer FOREIGN KEY (customer_id) REFERENCES customer_profiles (id),
  CONSTRAINT fk_r_qasai FOREIGN KEY (qasai_id) REFERENCES qasai_profiles (id),
  CONSTRAINT ck_r_rating CHECK (rating BETWEEN 1 AND 5)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE notifications (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  title VARCHAR(120) NOT NULL,
  message VARCHAR(500) NOT NULL,
  notification_type VARCHAR(40) NOT NULL,
  is_read TINYINT(1) NOT NULL DEFAULT 0,
  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  PRIMARY KEY (id),
  KEY idx_notif_user (user_id, is_read, created_at),
  CONSTRAINT fk_n_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE password_reset_tokens (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  token_hash CHAR(64) NOT NULL,
  expiry_date DATETIME(6) NOT NULL,
  used TINYINT(1) NOT NULL DEFAULT 0,
  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  PRIMARY KEY (id),
  UNIQUE KEY uk_prt_hash (token_hash),
  CONSTRAINT fk_prt_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Additional table: server-side refresh tokens (hashed) enabling revocation/logout.
CREATE TABLE refresh_tokens (
  id BIGINT NOT NULL AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  token_hash CHAR(64) NOT NULL,
  expiry_date DATETIME(6) NOT NULL,
  revoked TINYINT(1) NOT NULL DEFAULT 0,
  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  PRIMARY KEY (id),
  UNIQUE KEY uk_rt_hash (token_hash),
  KEY idx_rt_user (user_id),
  CONSTRAINT fk_rt_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
