-- User Table
CREATE TABLE IF NOT EXISTS User (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(15),
    role VARCHAR(10) NOT NULL, -- BUYER, SELLER, ADMIN
    password VARCHAR(255) NOT NULL
);

-- Product Table
CREATE TABLE IF NOT EXISTS Product (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DOUBLE NOT NULL,
    product_condition VARCHAR(20),
    category VARCHAR(50),
    sellerUserId INT NOT NULL,
    sellerContact VARCHAR(100),
    FOREIGN KEY (sellerUserId) REFERENCES User(id) ON DELETE CASCADE
);
