-- ------------------------------ --
-- DDL (Data Definition Language) --
-- ------------------------------ --

-- Create DataBase
CREATE DATABASE IF NOT EXISTS restauranthub_db CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

USE restauranthub_db;

-- User Type Table
CREATE TABLE IF NOT EXISTS user_types (
    id BINARY(16) NOT NULL,
    description VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    role ENUM(
        'CUSTOMER',
        'RESTAURANT_OWNER',
        'ADMIN'
    ) NOT NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

-- User Table
CREATE TABLE IF NOT EXISTS users (
    id BINARY(16) NOT NULL,
    email VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    user_type_id BINARY(16) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_users_email (email),
    KEY idx_users_user_type (user_type_id),
    CONSTRAINT fk_users_user_type FOREIGN KEY (user_type_id) REFERENCES user_types (id) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

-- Restaurant Table
CREATE TABLE IF NOT EXISTS restaurants (
    id BINARY(16) NOT NULL,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL,
    cuisine_type VARCHAR(255) NOT NULL,
    opening_hours VARCHAR(255),
    owner_id BINARY(16) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_restaurants_name (name),
    KEY idx_restaurants_owner (owner_id),
    CONSTRAINT fk_restaurants_owner FOREIGN KEY (owner_id) REFERENCES users (id) ON DELETE RESTRICT
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

-- Menu Item Table
CREATE TABLE IF NOT EXISTS menu_items (
    id BINARY(16) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    image_path VARCHAR(255),
    price DECIMAL(10, 2) NOT NULL,
    currency CHAR(3) NOT NULL,
    dine_in_only BOOLEAN NOT NULL,
    restaurant_id BINARY(16) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_menu_items_restaurant_name (restaurant_id, name),
    KEY idx_menu_items_restaurant (restaurant_id),
    CONSTRAINT fk_menu_items_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;