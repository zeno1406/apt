       create schema java_sql;
use java_sql;
-- drop database java_sql;

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
(1, 'huyhoang119763', '$2a$12$BltP6IjOrQCIZ7g1ezMsDu9wyVU6tO5150AnbKdkkYHJYL1t3YUV6'),
(2, 'thanhlong', '$2a$12$BltP6IjOrQCIZ7g1ezMsDu9wyVU6tO5150AnbKdkkYHJYL1t3YUV6');

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

INSERT INTO `role` (`id`, `name`, `description`, `salary_coefficient`) VALUES
(4, 'Nhân viên chăm sóc khách hàng', 'Tư vấn và hỗ trợ khách hàng sau bán', 2.3),
(5, 'Nhân viên marketing', 'Chạy chương trình khuyến mãi LEGO', 2.8),
(6, 'Nhân viên kỹ thuật', 'Hỗ trợ lắp ráp và bảo trì LEGO kỹ thuật', 2.7);


INSERT INTO `employee` (`first_name`, `last_name`, `salary`, `date_of_birth`, `role_id`, `status`) 
VALUES 
('Nguyễn Minh', 'Khôi', 3200000, '1997-03-21', 3, 1),
('Lê Anh', 'Thư', 3400000, '1996-06-15', 3, 1),
('Trần Quốc', 'Bảo', 3600000, '1995-08-10', 3, 1),
('Phạm Hoàng', 'Nam', 3700000, '1994-09-18', 3, 1),
('Đỗ Hồng', 'Nhung', 3300000, '1993-11-22', 3, 1),
('Vũ Thị', 'Lan', 3500000, '1992-12-30', 4, 1),
('Ngô Minh', 'Phương', 3700000, '1991-01-19', 4, 1),
('Dương Gia', 'Bảo', 3800000, '1990-04-14', 4, 1),
('Bùi Thanh', 'Hà', 3900000, '1989-05-09', 4, 1),
('Lý Thị', 'Thu', 4000000, '1988-07-27', 4, 1),
('Trịnh Công', 'Sơn', 4200000, '1997-02-12', 5, 1),
('Đinh Thị', 'Ngọc', 4300000, '1996-03-30', 5, 1),
('Tạ Đức', 'Anh', 4400000, '1995-05-17', 5, 1),
('Phan Thanh', 'Tùng', 4500000, '1994-08-04', 5, 1),
('Lương Thị', 'Hương', 4600000, '1993-09-25', 5, 1),
('Mai Anh', 'Tuấn', 4100000, '1992-11-11', 2, 1),
('Hồ Bích', 'Ngọc', 3900000, '1991-01-01', 2, 1),
('Chu Thị', 'Kim', 3700000, '1990-02-15', 2, 1),
('La Văn', 'Tài', 3600000, '1989-04-05', 2, 1),
('Cao Ngọc', 'Châu', 3500000, '1988-06-20', 2, 1);


INSERT INTO `customer` (`first_name`, `last_name`, `date_of_birth`, `phone`, `address`, `status`) VALUES
('Phạm', 'Tuấn', '1990-11-25', '0912345682', '345 Đường Hai Bà Trưng, Quận 1, Hồ Chí Minh', 1),
('Nguyễn', 'Hương', '1992-03-14', '0987654327', '456 Đường Nguyễn Thị Minh Khai, Quận 3, Hồ Chí Minh', 0),
('Lê', 'Nam', '1988-05-30', '0356789015', '789 Đường Trần Cao Vân, Quận 1, Hồ Chí Minh', 1),
('Võ', 'Quỳnh', '1993-07-18', '0123456786', '135 Đường Lý Chính Thắng, Quận 3, Hồ Chí Minh', 1),
('Trần', 'Tài', '1995-09-22', '0934567892', '246 Đường Nguyễn Thị Nghĩa, Quận 1, Hồ Chí Minh', 0),
('Phan', 'Vy', '1991-12-05', '0345678905', '357 Đường Nguyễn Văn Trỗi, Quận Phú Nhuận, Hồ Chí Minh', 1),
('Đặng', 'Dũng', '1989-02-10', '0912345683', '468 Đường Lê Lai, Quận 1, Hồ Chí Minh', 1),
('Nguyễn', 'Tâm', '1994-06-16', '0987654328', '579 Đường Nguyễn Bỉnh Khiêm, Quận 1, Hồ Chí Minh', 0),
('Lê', 'Chi', '1996-08-24', '0356789016', '680 Đường Võ Thị Sáu, Quận 3, Hồ Chí Minh', 1),
('Đỗ', 'Khang', '1992-10-12', '0123456787', '791 Đường Phan Xích Long, Quận Phú Nhuận, Hồ Chí Minh', 1),
('Bùi', 'Thảo', '1987-04-28', '0934567893', '902 Đường Nguyễn Hữu Cảnh, Quận Bình Thạnh, Hồ Chí Minh', 0),
('Vũ', 'Long', '1991-11-03', '0345678906', '113 Đường D2, Quận Bình Thạnh, Hồ Chí Minh', 1),
('Trương', 'Như', '1988-06-20', '0912345684', '224 Đường Phạm Văn Đồng, Quận Thủ Đức, Hồ Chí Minh', 1),
('Mai', 'Hiền', '1990-08-15', '0987654329', '335 Đường Ung Văn Khiêm, Quận Bình Thạnh, Hồ Chí Minh', 0),
('Ngô', 'Sơn', '1993-01-05', '0356789017', '446 Đường D5, Quận Bình Thạnh, Hồ Chí Minh', 1),
('Phạm', 'Trúc', '1995-03-19', '0123456788', '557 Đường Lê Văn Việt, Quận 9, Hồ Chí Minh', 1),
('Lê', 'Phong', '1989-09-25', '0934567894', '668 Đường Tô Hiến Thành, Quận 10, Hồ Chí Minh', 0),
('Đỗ', 'Nhật', '1991-05-11', '0345678907', '779 Đường Cộng Hòa, Quận Tân Bình, Hồ Chí Minh', 1),
('Nguyễn', 'Khánh', '1994-12-01', '0912345685', '880 Đường Hoàng Văn Thụ, Quận Tân Bình, Hồ Chí Minh', 1),
('Trần', 'Oanh', '1992-07-07', '0987654330', '991 Đường Âu Cơ, Quận Tân Phú, Hồ Chí Minh', 0);


INSERT INTO `customer` (`first_name`, `last_name`, `date_of_birth`, `phone`, `address`, `status`) VALUES
('Nguyễn Thị', 'Mai', '1997-03-15', '0967123456', '12 Đường Lý Thái Tổ, Quận 10, Hồ Chí Minh', 1),
('Phạm', 'Đức Anh', '1990-07-22', '0938123457', '34 Đường Trần Phú, Quận 5, Hồ Chí Minh', 1),
('Trần Thị', 'Ngọc', '1992-05-18', '0919123458', '56 Đường Nguyễn Thái Học, Quận 1, Hồ Chí Minh', 0),
('Lê', 'Minh Tuấn', '1988-09-09', '0988123459', '78 Đường Lê Duẩn, Quận 3, Hồ Chí Minh', 1),
('Võ', 'Phương', '1995-12-25', '0977123460', '90 Đường Pasteur, Quận 1, Hồ Chí Minh', 1),
('Đặng', 'Bình', '1989-06-05', '0956123461', '123 Đường Hai Bà Trưng, Quận 1, Hồ Chí Minh', 1),
('Ngô Thị', 'Tâm', '1994-11-30', '0945123462', '234 Đường Nguyễn Tri Phương, Quận 10, Hồ Chí Minh', 0),
('Bùi', 'Gia Bảo', '1996-02-17', '0937123463', '345 Đường Hoàng Văn Thụ, Quận Tân Bình, Hồ Chí Minh', 1),
('Đỗ', 'Nhật Nam', '1993-08-04', '0926123464', '456 Đường Cộng Hòa, Quận Tân Bình, Hồ Chí Minh', 1),
('Huỳnh', 'Anh Tuấn', '1991-10-12', '0915123465', '567 Đường Lũy Bán Bích, Quận Tân Phú, Hồ Chí Minh', 1),
('Trịnh', 'Công Sơn', '1987-03-23', '0904123466', '678 Đường Nguyễn Văn Luông, Quận 6, Hồ Chí Minh', 0),
('Phan', 'Tuấn', '1998-12-02', '0893123467', '789 Đường Âu Cơ, Quận Tân Bình, Hồ Chí Minh', 1),
('Dương', 'Hồng', '1995-05-19', '0882123468', '890 Đường Trường Chinh, Quận 12, Hồ Chí Minh', 1);


INSERT INTO `discount` (`code`, `name`, `type`, `startDate`, `endDate`) VALUES
('SUMMER2025', 'Khuyến mãi Hè 2025', 0, '2025-05-01', '2025-08-31'),
('FALL2025', 'Khuyến mãi Thu 2025', 0, '2025-09-01', '2025-11-30'),
('WINTER2025', 'Khuyến mãi Đông 2025', 1, '2025-12-01', '2026-02-28'),
('SPRING2026', 'Khuyến mãi Xuân 2026', 0, '2026-03-01', '2026-05-31'),
('WELCOME10', 'Chào mừng khách hàng mới', 0, '2025-01-01', '2025-12-31'),
('LOYAL15', 'Khách hàng thân thiết', 0, '2025-01-01', '2025-12-31'),
('BIRTHDAY', 'Quà sinh nhật', 1, '2025-01-01', '2025-12-31'),
('FLASH24H', 'Flash Sale 24h', 0, '2025-05-15', '2025-05-16'),
('WEEKEND', 'Khuyến mãi cuối tuần', 0, '2025-01-01', '2025-12-31'),
('BOOKS10', 'Giảm giá sách 10%', 0, '2025-04-10', '2025-12-31'),
('TECH2025', 'Giảm giá công nghệ', 1, '2025-07-01', '2025-08-31'),
('BACK2SCHOOL', 'Back to School', 0, '2025-08-15', '2025-09-15'),
('TET2026', 'Tết Nguyên Đán 2026', 0, '2026-01-15', '2026-02-15'),
('WOMENSDAY', 'Ngày Phụ nữ', 1, '2025-03-01', '2025-03-08'),
('CHILDRENSDAY', 'Ngày Quốc tế Thiếu nhi', 0, '2025-05-25', '2025-06-05'),
('MID2025', 'Giữa năm 2025', 1, '2025-06-15', '2025-07-15'),
('BLACK_FRIDAY', 'Black Friday', 0, '2025-11-25', '2025-11-30'),
('CYBER_MONDAY', 'Cyber Monday', 0, '2025-12-01', '2025-12-02'),
('NEWYEAR2026', 'Đón năm mới 2026', 1, '2025-12-25', '2026-01-10'),
('VALENTINE', 'Lễ tình nhân', 0, '2025-02-01', '2025-02-14'),
('TEACHER_DAY', 'Ngày Nhà giáo', 0, '2025-11-15', '2025-11-20'),
('HOLIDAY_SPECIAL', 'Đặc biệt ngày lễ', 1, '2025-04-29', '2025-05-02'),
('FIRST_PURCHASE', 'Lần mua đầu tiên', 0, '2025-01-01', '2025-12-31'),
('APP_ONLY', 'Chỉ áp dụng qua ứng dụng', 0, '2025-03-01', '2025-12-31'),
('CLEARANCE', 'Xả kho cuối mùa', 0, '2025-08-20', '2025-09-10'),
('LIMITED_OFFER', 'Ưu đãi có hạn', 1, '2025-05-10', '2025-05-20'),
('MEMBER_SPECIAL', 'Đặc quyền thành viên', 0, '2025-01-01', '2025-12-31'),
('ANNIVERSARY', 'Kỷ niệm thành lập', 1, '2025-10-01', '2025-10-15');

INSERT INTO `detail_discount` (`discount_code`, `total_price_invoice`, `discount_amount`) VALUES
-- 30T4 (Percentage discount - type 0)
('30T4', 500000, 12.00),

-- 30T6 (Fixed amount discount - type 1)
('30T6', 500000, 25000),

-- SUMMER2025 (Percentage discount)
('SUMMER2025', 100000, 8.00),
('SUMMER2025', 250000, 10.00),
('SUMMER2025', 500000, 15.00),

-- FALL2025 (Percentage discount)
('FALL2025', 150000, 6.00),
('FALL2025', 300000, 9.00),
('FALL2025', 600000, 12.00),

-- WINTER2025 (Fixed amount)
('WINTER2025', 200000, 15000),
('WINTER2025', 400000, 30000),
('WINTER2025', 700000, 50000),

-- SPRING2026 (Percentage discount)
('SPRING2026', 100000, 7.00),
('SPRING2026', 300000, 10.00),
('SPRING2026', 500000, 15.00),

-- WELCOME10 (Percentage)
('WELCOME10', 50000, 10.00),
('WELCOME10', 200000, 10.00),

-- LOYAL15 (Percentage)
('LOYAL15', 100000, 15.00),
('LOYAL15', 300000, 15.00),

-- BIRTHDAY (Fixed amount)
('BIRTHDAY', 100000, 20000),
('BIRTHDAY', 300000, 30000),

-- FLASH24H (Percentage)
('FLASH24H', 100000, 20.00),
('FLASH24H', 300000, 25.00),

-- WEEKEND (Percentage)
('WEEKEND', 200000, 5.00),
('WEEKEND', 400000, 8.00),

-- BOOKS10 (Percentage)
('BOOKS10', 100000, 10.00),
('BOOKS10', 300000, 12.00),

-- TECH2025 (Fixed amount)
('TECH2025', 500000, 40000),
('TECH2025', 1000000, 80000),

-- BACK2SCHOOL (Percentage)
('BACK2SCHOOL', 200000, 15.00),
('BACK2SCHOOL', 500000, 20.00);


INSERT INTO `supplier` (`id`, `name`, `phone`, `address`, `status`) VALUES
(3, 'LEGO Việt Nam Official', '0283822456', '65 Lê Lợi, Phường Bến Nghé, Quận 1, TP Hồ Chí Minh', 1),
(4, 'Mykingdom', '1800577767', 'Tầng 5 TTTM Vincom Center, 72 Lê Thánh Tôn, Quận 1, TP Hồ Chí Minh', 1),
(5, 'ToyStation', '0902456789', '123 Nguyễn Huệ, Phường Bến Nghé, Quận 1, TP Hồ Chí Minh', 1),
(6, 'LEGO Store Aeon Mall', '0283456789', 'Tầng 3, AEON MALL Tân Phú, 30 Bờ Bao Tân Thắng, Phường Sơn Kỳ, Quận Tân Phú, TP Hồ Chí Minh', 1),
(7, 'BRICKxBRICK', '0902123456', '45 Thảo Điền, Phường Thảo Điền, Quận 2, TP Thủ Đức, TP Hồ Chí Minh', 1),
(8, 'Toy World VN', '0283123456', '22 Phạm Ngọc Thạch, Phường 6, Quận 3, TP Hồ Chí Minh', 1),
(9, 'BrickLand Vietnam', '0904567890', '56 Võ Văn Tần, Phường 6, Quận 3, TP Hồ Chí Minh', 1),
(10, 'Nhà Phân Phối LEGO Miền Bắc', '0243678901', '88 Trần Hưng Đạo, Quận Hoàn Kiếm, Hà Nội', 1),
(11, 'Toysmart', '0904123789', '345 Nguyễn Văn Linh, Quận 7, TP Hồ Chí Minh', 1),
(12, 'LEGO Certified Store', '0283567890', 'Tầng 1, Saigon Centre, 65 Lê Lợi, Phường Bến Nghé, Quận 1, TP Hồ Chí Minh', 1),
(13, 'Smartkids', '0283914567', '29 Thái Văn Lung, Phường Bến Nghé, Quận 1, TP Hồ Chí Minh', 1),
(14, 'BrickVille', '0904678123', '87 Láng Hạ, Quận Đống Đa, Hà Nội', 1),
(15, 'Legogifts', '0913456789', '12 Nguyễn Đình Chiểu, Phường Đa Kao, Quận 1, TP Hồ Chí Minh', 1);



INSERT INTO `product` (`id`, `name`, `stock_quantity`, `selling_price`, `status`, `description`, `image_url`, `category_id`) VALUES
('SP00003', 'Sasuke Uchiha', 12, 32500, 1, 'Minifigure nhân vật Sasuke Uchiha từ series Naruto.', NULL, 2),
('SP00004', 'Kakashi Hatake', 8, 34000, 1, 'Minifigure nhân vật Kakashi với Sharingan.', NULL, 2),
('SP00005', 'Sakura Haruno', 10, 29900, 1, 'Minifigure nhân vật Sakura từ series Naruto.', NULL, 2),
('SP00006', 'Luke Skywalker', 15, 42000, 1, 'Minifigure Luke Skywalker với lightsaber xanh.', NULL, 2),
('SP00007', 'Darth Vader', 7, 45000, 1, 'Minifigure Darth Vader với lightsaber đỏ và mặt nạ.', NULL, 2),
('SP00008', 'Iron Man Mark 85', 9, 48500, 1, 'Minifigure Iron Man trong bộ giáp Mark 85 từ Avengers: Endgame.', NULL, 2),
('SP00009', 'Spider-Man', 14, 39900, 1, 'Minifigure Spider-Man với trang phục đỏ và xanh đặc trưng.', NULL, 2),
('SP00010', 'Harry Potter', 12, 38500, 1, 'Minifigure Harry Potter với đũa phép và kính tròn.', NULL, 2),

-- Technic sets (category_id = 3)
('SP00011', 'LEGO Technic Bugatti Chiron', 5, 2499000, 1, 'Mô hình kỹ thuật cao của siêu xe Bugatti Chiron, 3599 chi tiết.', NULL, 3),
('SP00012', 'LEGO Technic Land Rover Defender', 7, 1799000, 1, 'Mô hình chi tiết của Land Rover Defender với hộp số hoạt động, 2573 chi tiết.', NULL, 3),
('SP00013', 'LEGO Technic Excavator', 8, 899000, 1, 'Máy xúc điều khiển từ xa với chức năng nâng và xoay, 569 chi tiết.', NULL, 3),
('SP00014', 'LEGO Technic Race Car', 10, 1199000, 1, 'Xe đua công thức 1 với động cơ pistons hoạt động, 1580 chi tiết.', NULL, 3),
('SP00015', 'LEGO Technic Liebherr Crane', 3, 3799000, 1, 'Cần cẩu Liebherr điều khiển qua Bluetooth, mô hình lớn nhất của LEGO Technic, 4108 chi tiết.', NULL, 3),

-- Architecture (category_id = 4)
('SP00016', 'LEGO Architecture Statue of Liberty', 9, 999000, 1, 'Mô hình tượng Nữ thần Tự do, 1685 chi tiết.', NULL, 4),
('SP00017', 'LEGO Architecture Tokyo Skyline', 11, 599000, 1, 'Đường chân trời Tokyo với các công trình nổi tiếng, 547 chi tiết.', NULL, 4),
('SP00018', 'LEGO Architecture Paris Skyline', 13, 599000, 1, 'Đường chân trời Paris với tháp Eiffel, 649 chi tiết.', NULL, 4),
('SP00019', 'LEGO Architecture Empire State Building', 6, 1299000, 1, 'Mô hình chi tiết của tòa nhà Empire State, 1767 chi tiết.', NULL, 4),
('SP00020', 'LEGO Architecture Great Wall of China', 7, 799000, 1, 'Mô hình Vạn Lý Trường Thành, 551 chi tiết.', NULL, 4),

-- Classic (category_id = 5)
('SP00021', 'LEGO Classic Large Creative Brick Box', 20, 599000, 1, 'Hộp gạch sáng tạo lớn với nhiều màu sắc, 790 chi tiết.', NULL, 5),
('SP00022', 'LEGO Classic Medium Creative Brick Box', 25, 399000, 1, 'Hộp gạch sáng tạo vừa, 484 chi tiết.', NULL, 5),
('SP00023', 'LEGO Classic Creative Transparent Bricks', 18, 299000, 1, 'Bộ gạch trong suốt nhiều màu sắc, 500 chi tiết.', NULL, 5),
('SP00024', 'LEGO Classic Bricks and Wheels', 22, 349000, 1, 'Bộ gạch với bánh xe để xây dựng các phương tiện, 653 chi tiết.', NULL, 5),
('SP00025', 'LEGO Classic Creative Fun', 30, 249000, 1, 'Bộ gạch sáng tạo cơ bản, 333 chi tiết.', NULL, 5),

-- MOC (My Own Creation) (category_id = 6)
('SP00026', 'MOC - Nhà Cổ Việt Nam', 4, 1299000, 1, 'Mô hình nhà cổ Việt Nam thiết kế riêng, 2205 chi tiết.', NULL, 6),
('SP00027', 'MOC - Chợ Bến Thành', 3, 1599000, 1, 'Mô hình chợ Bến Thành tỉ mỉ, 2789 chi tiết.', NULL, 6),
('SP00028', 'MOC - Cầu Rồng Đà Nẵng', 5, 999000, 1, 'Mô hình Cầu Rồng Đà Nẵng với đèn LED, 1876 chi tiết.', NULL, 6),
('SP00029', 'MOC - Phố Cổ Hội An', 2, 1499000, 1, 'Mô hình một góc phố cổ Hội An với đèn lồng, 2340 chi tiết.', NULL, 6),
('SP00030', 'MOC - Tháp Rùa Hồ Gươm', 6, 899000, 1, 'Mô hình Tháp Rùa trên Hồ Gươm, 1250 chi tiết.', NULL, 6),

-- City (category_id = 7)
('SP00031', 'LEGO City Police Station', 8, 1399000, 1, 'Trụ sở cảnh sát thành phố với xe cảnh sát và nhà tù, 743 chi tiết.', NULL, 6),
('SP00032', 'LEGO City Fire Station', 7, 1299000, 1, 'Trạm cứu hỏa với xe cứu hỏa và trực thăng, 809 chi tiết.', NULL, 6),
('SP00033', 'LEGO City Hospital', 5, 1199000, 1, 'Bệnh viện thành phố với xe cứu thương, 861 chi tiết.', NULL, 6),
('SP00034', 'LEGO City Cargo Train', 4, 1799000, 1, 'Tàu hỏa chở hàng điều khiển từ xa với đường ray, 1226 chi tiết.', NULL, 6),
('SP00035', 'LEGO City Town Center', 6, 1599000, 1, 'Trung tâm thành phố với nhiều cửa hàng và phương tiện, 790 chi tiết.', NULL, 6),

-- Star Wars (category_id = 8)
('SP00036', 'LEGO Star Wars Millennium Falcon', 3, 2999000, 1, 'Tàu Millennium Falcon với nhiều nhân vật, 1353 chi tiết.', NULL, 4),
('SP00037', 'LEGO Star Wars Imperial Star Destroyer', 2, 3499000, 1, 'Tàu chiến Imperial Star Destroyer, 4784 chi tiết.', NULL, 4),
('SP00038', 'LEGO Star Wars AT-AT', 4, 1899000, 1, 'Walker AT-AT từ phim The Empire Strikes Back, 1267 chi tiết.', NULL, 4),
('SP00039', 'LEGO Star Wars Death Star', 1, 4999000, 1, 'Ngôi sao tử thần Death Star với nhiều phòng và nhân vật, 4016 chi tiết.', NULL, 4),
('SP00040', 'LEGO Star Wars X-Wing Starfighter', 7, 999000, 1, 'Tàu chiến X-Wing của Luke Skywalker, 761 chi tiết.', NULL, 4),

-- Marvel Super Heroes (category_id = 14)
('SP00041', 'LEGO Marvel Avengers Tower', 5, 1799000, 1, 'Tháp Avengers với nhiều nhân vật siêu anh hùng, 685 chi tiết.', NULL, 3),
('SP00042', 'LEGO Marvel Sanctum Sanctorum', 4, 1999000, 1, 'Sanctum Sanctorum của Doctor Strange, 2708 chi tiết.', NULL, 3),
('SP00043', 'LEGO Marvel Guardians Ship', 6, 1499000, 1, 'Tàu của đội Guardians of the Galaxy, 1901 chi tiết.', NULL, 3),
('SP00044', 'LEGO Marvel Spider-Man Daily Bugle', 2, 2999000, 1, 'Tòa nhà Daily Bugle với nhiều nhân vật Spider-Man, 3772 chi tiết.', NULL, 3),
('SP00045', 'LEGO Marvel Hulkbuster', 7, 1099000, 1, 'Bộ giáp Hulkbuster của Iron Man, 1363 chi tiết.', NULL, 3),

-- Harry Potter (category_id = 17)
('SP00046', 'LEGO Harry Potter Hogwarts Castle', 3, 3999000, 1, 'Lâu đài Hogwarts chi tiết với nhiều phòng và nhân vật, 6020 chi tiết.', NULL, 1),
('SP00047', 'LEGO Harry Potter Diagon Alley', 4, 2799000, 1, 'Con phố Diagon Alley với nhiều cửa hàng, 5544 chi tiết.', NULL, 1),
('SP00048', 'LEGO Harry Potter Hogwarts Express', 8, 899000, 1, 'Tàu Hogwarts Express với sân ga 9¾, 801 chi tiết.', NULL, 1),
('SP00049', 'LEGO Harry Potter Chamber of Secrets', 6, 1299000, 1, 'Phòng chứa bí mật với rắn Basilisk, 1176 chi tiết.', NULL, 1),
('SP00050', 'LEGO Harry Potter Quidditch Match', 9, 499000, 1, 'Sân đấu Quidditch với các cầu thủ và khán đài, 500 chi tiết.', NULL, 1),

-- Creator Expert (category_id = 9)
('SP00051', 'LEGO Creator Expert Bookshop', 5, 1799000, 1, 'Hiệu sách chi tiết với căn hộ ở trên, 2504 chi tiết.', NULL, 2),
('SP00052', 'LEGO Creator Expert Assembly Square', 3, 2499000, 1, 'Quảng trường trung tâm với nhiều tòa nhà, 4002 chi tiết.', NULL, 4);


INSERT INTO `import` (`create_date`, `employee_id`, `supplier_id`, `total_price`) VALUES
('2025-03-18 09:15:00', 2, 3, 450000),
('2025-03-18 11:00:00', 3, 4, 620000),
('2025-03-18 13:45:00', 4, 5, 310000),
('2025-03-19 10:30:00', 5, 6, 780000),
('2025-03-19 14:00:00', 1, 7, 550000),
('2025-03-19 16:15:00', 2, 8, 910000),
('2025-03-20 08:45:00', 3, 9, 480000),
('2025-03-20 12:30:00', 4, 10, 730000),
('2025-03-20 15:00:00', 5, 11, 600000),
('2025-03-21 09:45:00', 1, 12, 850000),
('2025-03-21 11:30:00', 2, 13, 390000),
('2025-03-21 14:15:00', 3, 14, 980000),
('2025-03-22 10:00:00', 4, 15, 520000),
('2025-03-22 13:30:00', 5, 3, 700000),
('2025-03-22 16:00:00', 1, 4, 410000),
('2025-03-23 09:30:00', 2, 5, 880000),
('2025-03-23 12:00:00', 3, 6, 650000),
('2025-03-23 14:45:00', 4, 7, 340000),
('2025-03-24 10:15:00', 5, 8, 950000),
('2025-03-24 13:00:00', 1, 9, 580000),
('2025-03-24 15:45:00', 2, 10, 760000),
('2025-03-25 09:00:00', 3, 11, 430000),
('2025-03-25 11:45:00', 4, 12, 820000),
('2025-03-25 14:30:00', 5, 13, 680000),
('2025-03-26 10:45:00', 1, 14, 370000),
('2025-03-26 13:15:00', 2, 15, 920000),
('2025-03-26 16:00:00', 3, 3, 510000),
('2025-03-27 09:15:00', 4, 4, 790000),
('2025-03-27 11:00:00', 5, 5, 630000),
('2025-03-27 13:45:00', 1, 6, 400000);

INSERT INTO `detail_import` (`import_id`, `product_id`, `quantity`, `price`, `total_price`) VALUES
(3, 'SP00031', 5, 90000, 450000),
(4, 'SP00032', 2, 310000, 620000),
(5, 'SP00033', 1, 310000, 310000),
(6, 'SP00034', 3, 260000, 780000),
(7, 'SP00035', 2, 275000, 550000),
(8, 'SP00036', 1, 910000, 910000),
(9, 'SP00037', 1, 480000, 480000),
(10, 'SP00038', 2, 365000, 730000),
(11, 'SP00039', 1, 600000, 600000),
(12, 'SP00040', 1, 850000, 850000),
(13, 'SP00041', 3, 130000, 390000),
(14, 'SP00042', 1, 980000, 980000),
(15, 'SP00029', 2, 260000, 520000),
(16, 'SP00044', 1, 700000, 700000),
(17, 'SP00045', 4, 102500, 410000),
(18, 'SP00046', 1, 880000, 880000),
(19, 'SP00047', 2, 325000, 650000),
(20, 'SP00048', 1, 340000, 340000),
(21, 'SP00049', 1, 950000, 950000),
(22, 'SP00050', 2, 290000, 580000),
(23, 'SP00051', 1, 760000, 760000),
(24, 'SP00052', 2, 215000, 430000),
(25, 'SP00031', 1, 820000, 820000),
(26, 'SP00032', 3, 226666.67, 680000.01),
(27, 'SP00033', 1, 370000, 370000),
(28, 'SP00034', 1, 920000, 920000),
(29, 'SP00035', 3, 170000, 510000),
(30, 'SP00036', 1, 790000, 790000),
(31, 'SP00037', 2, 315000, 630000),
(32, 'SP00038', 1, 400000, 400000),
(3, 'SP00036', 1, 450000, 450000),
(4, 'SP00033', 1, 310000, 310000),
(5, 'SP00038', 2, 155000, 310000),
(6, 'SP00031', 1, 260000, 260000),
(7, 'SP00039', 1, 275000, 275000),
(8, 'SP00032', 1, 910000, 910000),
(9, 'SP00040', 2, 240000, 480000),
(10, 'SP00035', 1, 365000, 365000),
(11, 'SP00041', 2, 300000, 600000),
(12, 'SP00034', 1, 850000, 850000),
(13, 'SP00042', 2, 195000, 390000),
(14, 'SP00037', 1, 980000, 980000),
(15, 'SP00043', 1, 260000, 260000),
(16, 'SP00039', 1, 700000, 700000),
(17, 'SP00044', 2, 220000, 440000),
(18, 'SP00040', 1, 880000, 880000),
(19, 'SP00045', 2, 325000, 650000),
(20, 'SP00041', 1, 340000, 340000),
(21, 'SP00046', 1, 950000, 950000),
(22, 'SP00042', 2, 260000, 520000),
(23, 'SP00047', 1, 760000, 760000),
(24, 'SP00043', 3, 273333.33, 820000.00),
(25, 'SP00048', 2, 340000, 680000),
(26, 'SP00044', 1, 370000, 370000),
(27, 'SP00049', 1, 920000, 920000),
(28, 'SP00045', 2, 200000, 400000),
(29, 'SP00050', 1, 510000, 510000),
(30, 'SP00046', 2, 395000, 790000),
(31, 'SP00047', 1, 630000, 630000),
(32, 'SP00048', 2, 215000, 430000);


-- Insert 50 new records into the invoice table
INSERT INTO `invoice` (`create_date`, `employee_id`, `customer_id`, `discount_code`, `discount_amount`, `total_price`) VALUES
('2025-04-09 10:00:00', 2, 3, 'SUMMER2025', 8000, 92000),       -- Invoice 3
('2025-04-09 14:30:00', 3, 4, 'FALL2025', 12000, 118000),     -- Invoice 4
('2025-04-10 09:00:00', 4, 5, NULL, 0, 80000),             -- Invoice 5
('2025-04-10 16:00:00', 1, 1, 'WINTER2025', 15000, 185000),    -- Invoice 6
('2025-04-11 11:00:00', 2, 2, NULL, 0, 150000),            -- Invoice 7
('2025-04-11 15:00:00', 3, 3, 'SPRING2026', 7000, 93000),   -- Invoice 8
('2025-04-12 10:30:00', 4, 4, NULL, 0, 220000),            -- Invoice 9
('2025-04-12 14:00:00', 5, 5, 'WELCOME10', 18000, 162000),  -- Invoice 10
('2025-04-13 09:30:00', 1, 1, NULL, 0, 70000),             -- Invoice 11
('2025-04-13 16:30:00', 2, 2, 'LOYAL15', 38250, 211750),    -- Invoice 12
('2025-04-14 10:00:00', 3, 3, NULL, 0, 125000),            -- Invoice 13
('2025-04-14 13:30:00', 4, 4, 'BIRTHDAY', 24000, 216000),   -- Invoice 14
('2025-04-15 09:00:00', 5, 5, NULL, 0, 90000),             -- Invoice 15
('2025-04-15 1600:00', 1, 1, 'FLASH24H', 56250, 168750),   -- Invoice 16
('2025-04-16 11:00:00', 2, 2, NULL, 0, 160000),            -- Invoice 17
('2025-04-16 15:00:00', 3, 3, 'WEEKEND', 18400, 351600),    -- Invoice 18
('2025-04-17 10:30:00', 4, 4, NULL, 0, 230000),            -- Invoice 19
('2025-04-17 14:00:00', 5, 5, 'BOOKS10', 21600, 194400),    -- Invoice 20
('2025-04-18 09:30:00', 1, 1, NULL, 0, 80000),             -- Invoice 21
('2025-04-18 16:30:00', 2, 2, 'TECH2025', 64000, 656000),   -- Invoice 22
('2025-04-19 10:00:00', 3, 3, NULL, 0, 130000),            -- Invoice 23
('2025-04-19 13:30:00', 4, 4, 'BACK2SCHOOL', 80000, 320000), -- Invoice 24
('2025-04-20 09:00:00', 5, 5, NULL, 0, 100000),             -- Invoice 25
('2025-04-20 16:00:00', 1, 1, 'TET2026', 60000, 540000),    -- Invoice 26
('2025-04-21 11:00:00', 2, 2, NULL, 0, 170000),            -- Invoice 27
('2025-04-21 15:00:00', 3, 3, 'WOMENSDAY', 24000, 216000),    -- Invoice 28
('2025-04-22 10:30:00', 4, 4, NULL, 0, 240000),            -- Invoice 29
('2025-04-22 14:00:00', 5, 5, 'CHILDRENSDAY', 30000, 270000),  -- Invoice 30
('2025-04-23 09:30:00', 1, 1, NULL, 0, 90000),             -- Invoice 31
('2025-04-23 16:30:00', 2, 2, 'MID2025', 60000, 540000),    -- Invoice 32
('2025-04-24 10:00:00', 3, 3, NULL, 0, 140000),            -- Invoice 33
('2025-04-24 13:30:00', 4, 4, 'BLACK_FRIDAY', 80000, 720000), -- Invoice 34
('2025-04-25 09:00:00', 5, 5, NULL, 0, 110000),             -- Invoice 35
('2025-04-25 16:00:00', 1, 1, 'CYBER_MONDAY', 90000, 810000),  -- Invoice 36
('2025-04-26 11:00:00', 2, 2, NULL, 0, 180000),            -- Invoice 37
('2025-04-26 15:00:00', 3, 3, 'NEWYEAR2026', 45000, 405000), -- Invoice 38
('2025-04-27 10:30:00', 4, 4, NULL, 0, 250000),            -- Invoice 39
('2025-04-27 14:00:00', 5, 5, 'VALENTINE', 20000, 180000),  -- Invoice 40
('2025-04-28 09:30:00', 1, 1, NULL, 0, 100000),             -- Invoice 41
('2025-04-28 16:30:00', 2, 2, 'TEACHER_DAY', 30000, 270000),  -- Invoice 42
('2025-04-29 10:00:00', 3, 3, NULL, 0, 150000),            -- Invoice 43
('2025-04-29 13:30:00', 4, 4, 'HOLIDAY_SPECIAL', 70000, 630000), -- Invoice 44
('2025-04-30 09:00:00', 5, 5, NULL, 0, 120000),             -- Invoice 45
('2025-04-30 16:00:00', 1, 1, 'FIRST_PURCHASE', 10000, 90000),  -- Invoice 46
('2025-05-01 11:00:00', 2, 2, NULL, 0, 190000),            -- Invoice 47
('2025-05-01 15:00:00', 3, 3, 'APP_ONLY', 50000, 450000),    -- Invoice 48
('2025-05-02 10:30:00', 4, 4, NULL, 0, 260000),            -- Invoice 49
('2025-05-02 14:00:00', 5, 5, 'CLEARANCE', 40000, 360000),  -- Invoice 50
('2025-05-03 09:30:00', 1, 1, NULL, 0, 110000),             -- Invoice 51
('2025-05-03 16:30:00', 2, 2, 'LIMITED_OFFER', 65000, 585000),  -- Invoice 52
('2025-05-04 10:00:00', 3, 3, NULL, 0, 160000);            -- Invoice 53

-- Insert data into the detail_invoice table
INSERT INTO `detail_invoice` (`invoice_id`, `product_id`, `quantity`, `price`, `total_price`) VALUES
(3, 'SP00001', 2, 56500, 113000),
(4, 'SP00002', 2, 91000, 182000),
(5, 'SP00003', 1, 80000, 80000),
(6, 'SP00004', 2, 150000, 300000),
(6, 'SP00005', 1, 30000, 30000),
(7, 'SP00006', 3, 50000, 150000),
(8, 'SP00007', 1, 85000, 85000),
(9, 'SP00008', 2, 110000, 220000),
(10, 'SP00009', 2, 90000, 180000),
(11, 'SP00010', 1, 70000, 70000),
(12, 'SP00011', 3, 85000, 255000),
(13, 'SP00012', 1, 125000, 125000),
(14, 'SP00013', 2, 135000, 270000),
(15, 'SP00014', 1, 90000, 90000),
(16, 'SP00015', 3, 75000, 225000),
(17, 'SP00016', 1, 160000, 160000),
(18, 'SP00017', 2, 184000, 368000),
(19, 'SP00018', 1, 230000, 230000),
(20, 'SP00019', 2, 108000, 216000),
(21, 'SP00020', 1, 80000, 80000),
(22, 'SP00021', 4, 180000, 720000),
(23, 'SP00022', 1, 130000, 130000),
(24, 'SP00023', 2, 200000, 400000),
(25, 'SP00024', 1, 100000, 100000),
(26, 'SP00025', 3, 180000, 540000),
(27, 'SP00026', 1, 170000, 170000),
(28, 'SP00027', 2, 108000, 216000),
(29, 'SP00028', 1, 240000, 240000),
(30, 'SP00029', 3, 90000, 270000),
(31, 'SP00030', 1, 90000, 90000),
(32, 'SP00031', 3, 180000, 540000),
(33, 'SP00032', 1, 140000, 140000),
(34, 'SP00033', 4, 180000, 720000),
(35, 'SP00034', 1, 110000, 110000),
(36, 'SP00035', 3, 270000, 810000),
(37, 'SP00036', 1, 180000, 180000),
(38, 'SP00037', 2, 202500, 405000),
(39, 'SP00038', 1, 250000, 250000),
(40, 'SP00039', 2, 90000, 180000),
(41, 'SP00040', 1, 100000, 100000),
(42, 'SP00041', 3, 90000, 270000),
(43, 'SP00042', 1, 150000, 150000),
(44, 'SP00043', 3, 210000, 630000),
(45, 'SP00044', 1, 120000, 120000),
(46, 'SP00045', 1, 90000, 90000),
(47, 'SP00046', 1, 190000, 190000),
(48, 'SP00047', 3, 150000, 450000),
(49, 'SP00048', 1, 260000, 260000),
(50, 'SP00049', 2, 180000, 360000),
(51, 'SP00050', 1, 110000, 110000),
(52, 'SP00051', 3, 195000, 585000),
(53, 'SP00052', 1, 160000, 160000);

