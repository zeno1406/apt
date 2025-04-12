create schema java_sql;
use java_sql;
drop database java_sql;

CREATE TABLE `role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `salary_coefficient` DECIMAL(5,2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

INSERT INTO `role` (`id`, `name`, `description`, `salary_coefficient`) VALUES
(1, 'Admin', 'Quản trị hệ thống', 3.5),
(2, 'Nhân viên quản lý kho', null, 2.5),
(3, 'Nhân viên bán hàng', null, 2.5);

CREATE TABLE `module` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

INSERT INTO `module` (`id`, `name`) VALUES
(1, 'Quản lý nhân viên'),
(2, 'Quản lý khách hàng'),
(3, 'Quản lý sản phẩm'),
(4, 'Quản lý nhà cung cấp'),
(5, 'Quản lý bán hàng'),
(6, 'Quản lý nhập hàng'),
(7, 'Quản lý thể loại'),
(8, 'Quản lý khuyến mãi'),
(9, 'Quản lý chức vụ'),
(10, 'Quản lý tài khoản'),
(11, 'Thống kê');

CREATE TABLE `permission` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `module_id` INT(11) NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`module_id`) REFERENCES `module` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;


INSERT INTO `permission` (`id`, `name`, `module_id`) VALUES
(1, 'Thêm nhân viên', 1),
(2, 'Xóa nhân viên', 1),
(3, 'Sửa nhân viên', 1),
(4, 'Thêm khách hàng', 2),
(5, 'Xóa khách hàng', 2),
(6, 'Sửa khách hàng', 2),
(7, 'Thêm sản phẩm', 3),
(8, 'Xóa sản phẩm', 3),
(9, 'Sửa sản phẩm', 3),
(10, 'Thêm nhà cung cấp', 4),
(11, 'Xóa nhà cung cấp', 4),
(12, 'Sửa nhà cung cấp', 4),
(13, 'Tạo đơn hàng', 5),
(14, 'Hủy đơn hàng', 5),
(15, 'Tạo phiếu nhập hàng', 6),
(16, 'Xóa phiếu nhập hàng', 6),
(17, 'Thêm thể loại', 7),
(18, 'Xóa thể loại', 7),
(19, 'Sửa thể loại', 7),
(20, 'Thêm mã giảm giá', 8),
(21, 'Xóa mã giảm giá', 8),
(22, 'Sửa mã giảm giá', 8),
(23, 'Thêm chức vụ', 9),
(24, 'Xóa chức vụ', 9),
(25, 'Sửa chức vụ', 9),
(26, 'Sửa phân quyền', 9),
(27, 'Tạo tài khoản', 10),
(28, 'Xóa tài khoản', 10),
(29, 'Sửa tài khoản', 10),
(30, 'Xem thống kê', 11);

CREATE TABLE `role_permission` (
  `role_id` int(11) NOT NULL,
  `permission_id` int(11) NOT NULL,
  `status` TINYINT(1) NOT NULL DEFAULT 1 CHECK (`status` IN (0,1)),
   PRIMARY KEY (`role_id`, `permission_id`),
   FOREIGN KEY (`role_id`) REFERENCES `role` (`id`),
   FOREIGN KEY (`permission_id`) REFERENCES `permission` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

INSERT INTO `role_permission` (`role_id`, `permission_id`, `status`)
SELECT 1, `id`, 1 FROM `permission`;

INSERT INTO `role_permission` (`role_id`, `permission_id`, `status`)
SELECT 2, `id`, 0 FROM `permission`;

CREATE TABLE `employee` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `first_name` VARCHAR(100) NOT NULL,
  `last_name` VARCHAR(100) NOT NULL,
  `salary` DECIMAL(10,2) NOT NULL,
  `date_of_birth` DATE DEFAULT NULL,
  `role_id` INT(11) DEFAULT NULL,
  `status` TINYINT(1) NOT NULL DEFAULT 1 CHECK (`status` IN (0,1)),
  PRIMARY KEY (`id`),
  FOREIGN KEY (`role_id`) REFERENCES `role` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;


INSERT INTO `employee` (`first_name`, `last_name`, `salary`, `date_of_birth`, `role_id`, `status`) 
VALUES 
    ('Đặng Huy', 'Hoàng', 5000000, '2004-06-11', 1, 1),
    ('Nguyễn Thành', 'Long', 2000000, '2003-04-11', 2, 1),
    ('Trần Văn', 'A', 3000000, '1990-01-15', 2, 1),
    ('Lê Thị', 'B', 3500000, '1988-02-20', 2, 1),
    ('Phạm Minh', 'C', 4000000, '1985-03-25', 2, 1),
    ('Nguyễn Thị', 'D', 4500000, '1992-04-30', 2, 1),
    ('Đỗ Văn', 'E', 2800000, '1995-05-05', 2, 1),
    ('Bùi Thị', 'F', 3200000, '1993-06-10', 2, 1),
    ('Ngô Minh', 'G', 2900000, '1991-07-15', 2, 1),
    ('Trịnh Văn', 'H', 3100000,  '1989-08-20', 2, 0),
    ('Vũ Thị', 'I', 3500000,  '1994-09-25', 1, 1),
    ('Lý Văn', 'J', 3700000, '1996-10-30', 1, 1);

CREATE TABLE `account` (
  `employee_id` INT NOT NULL,
  `username` VARCHAR(50) NOT NULL UNIQUE,
  `password` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`employee_id`),
  FOREIGN KEY (`employee_id`) REFERENCES `employee` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

INSERT INTO `account` (`employee_id`, `username`, `password`) VALUES
(1, 'huyhoang119763', '$2a$12$Yqnmw3n0OHa7KGtMA3FWgOHl1z.5Pl8MEqYidf6TTIMSsIcX77zsi'),
(2, 'thanhlong', '$2a$12$Yqnmw3n0OHa7KGtMA3FWgOHl1z.5Pl8MEqYidf6TTIMSsIcX77zsi');

CREATE TABLE `customer` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `first_name` VARCHAR(100) NOT NULL,
  `last_name` VARCHAR(100) NOT NULL,
  `date_of_birth` DATE DEFAULT NULL,
  `phone` VARCHAR(15) NOT NULL,
  `address` VARCHAR(255) NOT NULL,
  `status` TINYINT(1) NOT NULL DEFAULT 1 CHECK (`status` IN (0,1)),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

INSERT INTO `customer` (`first_name`, `last_name`, `date_of_birth`, `phone`, `address`, `status`) VALUES
('Hoàng Huy', 'Đặng', '2004-06-11', '0585822397', '260/7 Tô Ngọc Vân, Linh Đông, Thủ Đức, Hồ Chí Minh', 1),
('Nguyễn', 'Thành', '1990-02-15', '0123456789', '123 Đường Lê Lợi, Quận 1, Hồ Chí Minh', 0),
('Trần', 'Minh', '1985-04-20', '0987654321', '456 Đường Nguyễn Huệ, Quận 1, Hồ Chí Minh', 1),
('Lê', 'Hằng', '1995-08-30', '0912345678', '789 Đường Trần Hưng Đạo, Quận 5, Hồ Chí Minh', 0),
('Phạm', 'Hải', '1988-12-01', '0934567890', '321 Đường Bùi Viện, Quận 1, Hồ Chí Minh', 1),
('Đỗ', 'Lan', '1992-05-16', '0345678901', '654 Đường Lê Văn Sĩ, Quận 3, Hồ Chí Minh', 0),
('Nguyễn', 'Văn', '1993-11-11', '0123456780', '987 Đường Nguyễn Văn Cừ, Quận 5, Hồ Chí Minh', 1),
('Trần', 'Kiên', '1994-03-23', '0912345679', '234 Đường Trần Quốc Thảo, Quận 3, Hồ Chí Minh', 1),
('Lê', 'Phú', '1991-07-07', '0987654320', '567 Đường Phạm Ngọc Thạch, Quận 1, Hồ Chí Minh', 1),
('Ngô', 'Thảo', '1996-09-09', '0356789012', '890 Đường Võ Văn Tần, Quận 3, Hồ Chí Minh', 0),
('Bùi', 'Bích', '1987-01-20', '0123456781', '135 Đường Hàn Hải Nguyên, Quận 4, Hồ Chí Minh', 1),
('Mai', 'An', '1999-06-18', '0987654322', '246 Đường Cách Mạng Tháng 8, Quận 10, Hồ Chí Minh', 1),
('Vũ', 'Khoa', '1992-10-10', '0345678902', '357 Đường Nguyễn Trãi, Quận 5, Hồ Chí Minh', 0),
('Hà', 'Trang', '1989-05-21', '0934567891', '468 Đường Lê Quý Đôn, Quận 3, Hồ Chí Minh', 1),
('Phan', 'Nhi', '1995-12-30', '0123456782', '579 Đường Nguyễn Thị Minh Khai, Quận 1, Hồ Chí Minh', 1),
('Nguyễn', 'Lộc', '1994-04-14', '0987654323', '680 Đường Nam Kỳ Khởi Nghĩa, Quận 3, Hồ Chí Minh', 0),
('Lê', 'Quân', '1986-08-08', '0356789013', '791 Đường Điện Biên Phủ, Quận 1, Hồ Chí Minh', 1),
('Trương', 'Duy', '1993-11-02', '0123456783', '902 Đường Nguyễn Đình Chiểu, Quận 3, Hồ Chí Minh', 1),
('Ngô', 'Việt', '1988-07-19', '0912345680', '113 Đường Phan Đăng Lưu, Quận Bình Thạnh, Hồ Chí Minh', 1),
('Đỗ', 'Hòa', '1991-09-29', '0987654324', '224 Đường Huỳnh Văn Bánh, Quận Phú Nhuận, Hồ Chí Minh', 1),
('Nguyễn', 'Phúc', '1992-04-05', '0345678903', '456 Đường Nguyễn Thái Bình, Quận 1, Hồ Chí Minh', 1),
('Lê', 'Hưng', '1989-12-12', '0912345670', '567 Đường Lê Văn Sĩ, Quận 3, Hồ Chí Minh', 0),
('Đỗ', 'Nghĩa', '1995-05-25', '0987654325', '678 Đường Phạm Hồng Thái, Quận 10, Hồ Chí Minh', 1),
('Trần', 'Tú', '1994-07-30', '0356789014', '789 Đường Trần Bình Trọng, Quận 5, Hồ Chí Minh', 1),
('Lê', 'Đức', '1991-01-01', '0123456785', '890 Đường Lê Thánh Tôn, Quận 1, Hồ Chí Minh', 1),
('Nguyễn', 'Giang', '1993-03-03', '0987654326', '901 Đường Nguyễn Đình Chiểu, Quận 3, Hồ Chí Minh', 1),
('Trần', 'Thành', '1987-08-08', '0345678904', '123 Đường Trần Hưng Đạo, Quận 5, Hồ Chí Minh', 0),
('Mai', 'Hương', '1996-09-09', '0912345681', '234 Đường Cách Mạng Tháng 8, Quận 10, Hồ Chí Minh', 1);

CREATE TABLE `discount` (
  `code` VARCHAR(50) NOT NULL,
  `name` VARCHAR(100) NOT NULL,
  `type` TINYINT(1) NOT NULL DEFAULT 0,
  `startDate` DATE NOT NULL,
  `endDate` DATE NULL,
  PRIMARY KEY (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

INSERT INTO `discount` (`code`, `name`, `type`, `startDate`, `endDate`) VALUES
('30T4', '30 Tháng 4', 0, '2025-03-11', '2025-06-11'),
('30T6', '30 Tháng 6', 1, '2025-03-11', '2025-06-11');

CREATE TABLE `detail_discount` (
  `discount_code` VARCHAR(50) NOT NULL,
  `total_price_invoice` DECIMAL(10,2) NOT NULL,
  `discount_amount` DECIMAL(10,2) NOT NULL,
  PRIMARY KEY (`discount_code`, `total_price_invoice`),
  FOREIGN KEY (`discount_code`) REFERENCES `discount` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

INSERT INTO `detail_discount` (`discount_code`, `total_price_invoice`, `discount_amount`) VALUES
('30T4', 100000, 5.00),
('30T4', 200000, 7.00),
('30T4', 300000, 9.00),
('30T6', 100000, 10000),
('30T6', 200000, 12000),
('30T6', 300000, 14000);

CREATE TABLE `category` (
  `id` int(11) NOT NULL Auto_increment,
  `name` varchar(100) NOT NULL,
  `status` TINYINT(1) NOT NULL DEFAULT 1 CHECK (`status` IN (0,1)),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

INSERT INTO `category` (`id`, `name`, `status`) VALUES
(1, 'Chưa xác định', 1),
(2, 'Minifigure', 1),
(3, 'Technic', 1),
(4, 'Architecture', 1),
(5, 'Classic', 1),
(6, 'Moc', 1);

CREATE TABLE `supplier` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `phone` VARCHAR(15) NOT NULL,
  `address` VARCHAR(255) NOT NULL,
  `status` TINYINT(1) NOT NULL DEFAULT 1 CHECK (`status` IN (0,1)),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

INSERT INTO `supplier` (`id`, `name`, `phone`, `address`, `status`) VALUES
(1, 'Nhà cung cấp A', '0903344554', '99 An Dương Vương, Phường 16, Quận 8, TP Hồ Chí Minh', 1),
(2, 'Nhà cung cấp B', '0903344556', '04 Tôn Đức Thắng, Phường Bến Nghé, Quận 1, TP Hồ Chí Minh', 1);

CREATE TABLE `product` (
  `id` NVARCHAR(50) NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  `stock_quantity` INT(11) NOT NULL DEFAULT 0,
  `selling_price` DECIMAL(10,2) NOT NULL,
  `status` TINYINT(1) NOT NULL DEFAULT 1 CHECK (`status` IN (0,1)),
  `description` TEXT DEFAULT NULL,
  `image_url` VARCHAR(255) DEFAULT NULL,
  `category_id` INT(11) NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`category_id`) REFERENCES `category` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

INSERT INTO `product` (`id`, `name`, `stock_quantity`, `selling_price`, `status`, `description`, `image_url`, `category_id`) VALUES
('SP00001', 'Naruto - 01', 10, 30800, 1, 'Minifigure nhân vật Naruto.', '/images/product/sp00001.png', 2),
('SP00002', 'Naruto - 02', 15, 27500, 1, 'Minifigure Naruto trong trạng thái chiến đấu.', '/images/product/sp00002.png', 2);

CREATE TABLE `invoice` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `create_date` DATETIME NOT NULL,
  `employee_id` INT(11) NOT NULL,
  `customer_id` INT(11) NOT NULL,
  `discount_code` VARCHAR(50),
  `discount_amount` DECIMAL(10,2) NOT NULL,
  `total_price` DECIMAL(10,2) NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`employee_id`) REFERENCES `employee` (`id`),
  FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`),
  FOREIGN KEY (`discount_code`) REFERENCES `discount` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

INSERT INTO `invoice` (`create_date`, `employee_id`, `customer_id`, `discount_code`, `discount_amount`, `total_price`) VALUES
('2025-03-17 14:00:00', 1, 1, '30T4', 0, 92400),  -- Hóa đơn có giảm giá
('2025-03-17 15:30:00', 1, 1, NULL, 0, 57500); -- Hóa đơn không có giảm giá

CREATE TABLE `detail_invoice` (
  `invoice_id` INT(11) NOT NULL,
  `product_id` NVARCHAR(50) NOT NULL,
  `quantity` INT(11) NOT NULL DEFAULT 1,
  `price` DECIMAL(10,2) NOT NULL,
  `total_price` DECIMAL(10,2) NOT NULL,
  PRIMARY KEY (`invoice_id`, `product_id`),
  FOREIGN KEY (`invoice_id`) REFERENCES `invoice` (`id`),
  FOREIGN KEY (`product_id`) REFERENCES `product` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

INSERT INTO `detail_invoice` (`invoice_id`, `product_id`, `quantity`, `price`, `total_price`) VALUES
(1, 'SP00001', 3, 30800, 92400),
(2, 'SP00002', 2, 28500, 57500);

CREATE TABLE `import` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `create_date` DATETIME NOT NULL,
  `employee_id` INT(11) NOT NULL,
  `supplier_id` INT(11) NOT NULL,
  `total_price` DECIMAL(10,2) NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`employee_id`) REFERENCES `employee` (`id`),
  FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

INSERT INTO `import` (`create_date`, `employee_id`, `supplier_id`, `total_price`) VALUES
('2025-03-17 14:00:00', 1, 1, 280000), 
('2025-03-17 15:30:00', 1, 2, 375000);

CREATE TABLE `detail_import` (
  `import_id` INT(11) NOT NULL,
  `product_id` NVARCHAR(50) NOT NULL,
  `quantity` INT(11) NOT NULL DEFAULT 1,
  `price` DECIMAL(10,2) NOT NULL,
  `total_price` DECIMAL(10,2) NOT NULL,
  PRIMARY KEY (`import_id`, `product_id`),
  FOREIGN KEY (`import_id`) REFERENCES `import` (`id`),
  FOREIGN KEY (`product_id`) REFERENCES `product` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

INSERT INTO `detail_import` (`import_id`, `product_id`, `quantity`, `price`, `total_price`) VALUES
(1, 'SP00001', 10, 28000, 280000),
(2, 'SP00002', 15, 25000, 375000);
