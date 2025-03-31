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
(1, 'Admin', 'Quản trị hệ thống', 3.5);

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
(10, 'Quản lý phân quyền'),
(11, 'Quản lý tài khoản'),
(12, 'Thống kê');

CREATE TABLE `permission` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `module_id` INT(11) NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`module_id`) REFERENCES `module` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;


INSERT INTO `permission` (`id`, `name`, `module_id`) VALUES
(1, 'Thêm nhân viên', 1),
(2, 'Xóa nhân viên', 1),
(3, 'Sửa nhân viên', 1),
(4, 'Tìm kiếm nhân viên', 1),
(5, 'Thêm khách hàng', 2),
(6, 'Xóa khách hàng', 2),
(7, 'Sửa khách hàng', 2),
(8, 'Tìm kiếm khách hàng', 2),
(9, 'Thêm sản phẩm', 3),
(10, 'Xóa sản phẩm', 3),
(11, 'Sửa sản phẩm', 3),
(12, 'Tìm kiếm sản phẩm', 3),
(13, 'Thêm nhà cung cấp', 4),
(14, 'Xóa nhà cung cấp', 4),
(15, 'Sửa nhà cung cấp', 4),
(16, 'Tìm kiếm nhà cung cấp', 4),
(17, 'Tạo đơn hàng', 5),
(18, 'Hủy đơn hàng', 5),
(19, 'Sửa đơn hàng', 5),
(20, 'Xem lịch sử đơn hàng', 5),
(21, 'Thêm phiếu nhập hàng', 6),
(22, 'Xóa phiếu nhập hàng', 6),
(23, 'Sửa phiếu nhập hàng', 6),
(24, 'Tìm kiếm phiếu nhập hàng', 6),
(25, 'Thêm thể loại', 7),
(26, 'Xóa thể loại', 7),
(27, 'Sửa thể loại', 7),
(28, 'Tìm kiếm thể loại', 7),
(29, 'Thêm mã giảm giá', 8),
(30, 'Xóa mã giảm giá', 8),
(31, 'Sửa mã giảm giá', 8),
(32, 'Tìm kiếm mã giảm giá', 8),
(33, 'Thêm chức vụ', 9),
(34, 'Xóa chức vụ', 9),
(35, 'Sửa chức vụ', 9),
(36, 'Tìm kiếm chức vụ', 9),
(37, 'Thêm quyền', 10),
(38, 'Xóa quyền', 10),
(39, 'Tạo tài khoản', 11),
(40, 'Xóa tài khoản', 11),
(41, 'Xem thống kê', 12);

CREATE TABLE `role_permission` (
  `role_id` int(11) NOT NULL,
  `permission_id` int(11) NOT NULL,
  `status` TINYINT(1) NOT NULL DEFAULT 1 CHECK (`status` IN (0,1)),
   PRIMARY KEY (`role_id`, `permission_id`),
   FOREIGN KEY (`role_id`) REFERENCES `role` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
   FOREIGN KEY (`permission_id`) REFERENCES `permission` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

INSERT INTO `role_permission` (`role_id`, `permission_id`, `status`)
SELECT 1, `id`, 1 FROM `permission`;

CREATE TABLE `employee` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `first_name` VARCHAR(100) NOT NULL,
  `last_name` VARCHAR(100) NOT NULL,
  `salary` DECIMAL(10,2) NOT NULL,
  `image_url` VARCHAR(255) DEFAULT NULL,
  `date_of_birth` DATE DEFAULT NULL,
  `role_id` INT(11) NOT NULL,
  `status` TINYINT(1) NOT NULL DEFAULT 1 CHECK (`status` IN (0,1)),
  PRIMARY KEY (`id`),
  FOREIGN KEY (`role_id`) REFERENCES `role` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

INSERT INTO `employee` (`first_name`, `last_name`, `salary`, `image_url`, `date_of_birth`, `role_id`, `status`) 
VALUES ('Đặng Huy', 'Hoàng', 5000000, './assets/img/user-img/employee_default.png', '2004-06-11', 1, 1);

CREATE TABLE `account` (
  `employee_id` INT NOT NULL,
  `username` VARCHAR(100) NOT NULL UNIQUE,
  `password` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`employee_id`),
  FOREIGN KEY (`employee_id`) REFERENCES `employee` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

INSERT INTO `account` (`employee_id`, `username`, `password`) VALUES
(1, 'huyhoang119763', '$2a$12$16qVYVlZdsEJIQTC1YDBZeVMAgNZtvOaKra8BGrYdl.KCrvj0BF1O');

CREATE TABLE `customer` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `first_name` VARCHAR(100) NOT NULL,
  `last_name` VARCHAR(100) NOT NULL,
  `image_url` VARCHAR(255) DEFAULT NULL,
  `date_of_birth` DATE DEFAULT NULL,
  `phone` VARCHAR(15) NOT NULL,
  `address` VARCHAR(255) NOT NULL,
  `status` TINYINT(1) NOT NULL DEFAULT 1 CHECK (`status` IN (0,1)),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

INSERT INTO `customer` (`first_name`, `last_name`, `image_url`, `date_of_birth`, `phone`, `address`, `status`) VALUES
('Hoàng Huy', 'Đặng', './assets/img/user-img/customer_default.png', '2004-06-11', '0585822397', '260/7 Tô Ngọc Vân, Linh Đông, Thủ Đức, Hồ Chí Minh', 1);

CREATE TABLE `discount` (
  `code` VARCHAR(50) NOT NULL,
  `name` VARCHAR(100) NOT NULL,
  `type` TINYINT(1) NOT NULL DEFAULT 0,
  `startDate` DATETIME NOT NULL,
  `endDate` DATETIME NOT NULL,
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
  FOREIGN KEY (`discount_code`) REFERENCES `discount` (`code`) ON DELETE CASCADE ON UPDATE CASCADE
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
(1, 'Minifigure', 1),
(2, 'Technic', 1),
(3, 'Architecture', 1),
(4, 'Classic', 1),
(5, 'Moc', 1);

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
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

INSERT INTO `product` (`id`, `name`, `stock_quantity`, `selling_price`, `status`) VALUES
('MF001', 'Naruto - 01', 10, 30800, 1),
('MF002', 'Naruto - 02', 15, 27500, 1);

CREATE TABLE `detail_product` (
  `product_id` NVARCHAR(50) NOT NULL,
  `description` TEXT DEFAULT NULL,
  `image_url` VARCHAR(255) DEFAULT NULL,
  `category_id` INT(11) NOT NULL,
  `supplier_id` INT(11) NOT NULL,
  PRIMARY KEY (`product_id`),
  FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (`category_id`) REFERENCES `category` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

INSERT INTO `detail_product` (`product_id`, `description`, `image_url`, `category_id`, `supplier_id`) VALUES
('MF001', 'Minifigure nhân vật Naruto.', './assets/img/lego-minifigure/mini-01.png', 1, 1),
('MF002', 'Minifigure Naruto trong trạng thái chiến đấu.', './assets/img/lego-minifigure/mini-02.png', 1, 2);

CREATE TABLE `invoice` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `create_date` DATETIME NOT NULL,
  `employee_id` INT(11) NOT NULL,
  `customer_id` INT(11) NOT NULL,
  `discount_code` VARCHAR(50),
  `total_price` DECIMAL(10,2) NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`employee_id`) REFERENCES `employee` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (`discount_code`) REFERENCES `discount` (`code`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

INSERT INTO `invoice` (`create_date`, `employee_id`, `customer_id`, `discount_code`, `total_price`) VALUES
('2025-03-17 14:00:00', 1, 1, '30T4', 99000),  -- Hóa đơn có giảm giá
('2025-03-17 15:30:00', 1, 1, NULL, 55000); -- Hóa đơn không có giảm giá

CREATE TABLE `detail_invoice` (
  `invoice_id` INT(11) NOT NULL,
  `product_id` NVARCHAR(50) NOT NULL,
  `quantity` INT(11) NOT NULL DEFAULT 1,
  `price` DECIMAL(10,2) NOT NULL,
  `total_price` DECIMAL(10,2) NOT NULL,
  PRIMARY KEY (`invoice_id`, `product_id`),
  FOREIGN KEY (`invoice_id`) REFERENCES `invoice` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

INSERT INTO `detail_invoice` (`invoice_id`, `product_id`, `quantity`, `price`, `total_price`) VALUES
(1, 'MF001', 3, 30800, 92400),
(2, 'MF002', 2, 28500, 57500);

CREATE TABLE `import` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `create_date` DATETIME NOT NULL,
  `employee_id` INT(11) NOT NULL,
  `supplier_id` INT(11) NOT NULL,
  `total_price` DECIMAL(10,2) NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`employee_id`) REFERENCES `employee` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

INSERT INTO `import` (`create_date`, `employee_id`, `supplier_id`, `total_price`) VALUES
('2025-03-17 14:00:00', 1, 1, 280000), 
('2025-03-17 15:30:00', 1, 2, 375000);

CREATE TABLE `detail_import` (
  `import_id` INT(11) NOT NULL,
  `product_id` NVARCHAR(50) NOT NULL,
  `quantity` INT(11) NOT NULL DEFAULT 1,
  `price` DECIMAL(10,2) NOT NULL,
  `profit_percent` DECIMAL(5,2) NOT NULL,
  `total_price` DECIMAL(10,2) NOT NULL,
  PRIMARY KEY (`import_id`, `product_id`),
  FOREIGN KEY (`import_id`) REFERENCES `import` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

INSERT INTO `detail_import` (`import_id`, `product_id`, `quantity`, `price`, `profit_percent`, `total_price`) VALUES
(1, 'MF001', 10, 28000, 10, 280000),
(2, 'MF002', 15, 25000, 15, 375000);

CREATE TABLE `statistic` (
  `save_date` DATETIME NOT NULL,
  `invoice_id` INT(11) NOT NULL,
  `total_capital` DECIMAL(20,2) NOT NULL,
  PRIMARY KEY (`save_date`, `invoice_id`),
  FOREIGN KEY (`invoice_id`) REFERENCES `invoice` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

INSERT INTO `statistic` (`save_date`, `invoice_id`, `total_capital`) VALUES
('2025-03-17 14:05:00', 1, 84000.00), -- Hóa đơn 1 có MF001, lấy giá nhập gần nhất là 28,000
('2025-03-17 15:35:00', 2, 50000.00); -- Hóa đơn 2 có MF002, lấy giá nhập gần nhất là 25,000
