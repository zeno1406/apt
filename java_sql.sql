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
(1, 'Admin', 'Quản trị hệ thống', 3.50),
(2, 'Nhân viên quản lý kho', NULL, 2.50),
(3, 'Nhân viên bán hàng', 'Bán hàng', 2.00),
(4, 'Nhân viên chăm sóc khách hàng', NULL, 2.20),
(5, 'Quản lý cửa hàng', NULL, 2.60),
(6, 'Quản lý chức vụ', NULL, 2.80);

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
(14, 'Xem đơn hàng', 5),
(15, 'Tạo phiếu nhập hàng', 6),
(16, 'Xem phiếu nhập hàng', 6),
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
VALUES
	(1,1,1),(1,2,1),(1,3,1),(1,4,1),(1,5,1),(1,6,1),(1,7,1),(1,8,1),(1,9,1),(1,10,1),(1,11,1),(1,12,1),(1,13,1),(1,14,1),(1,15,1),(1,16,1),(1,17,1),(1,18,1),(1,19,1),(1,20,1),(1,21,1),(1,22,1),(1,23,1),(1,24,1),(1,25,1),(1,26,1),(1,27,1),(1,28,1),(1,29,1),(1,30,1),(2,1,0),(2,2,0),(2,3,0),(2,4,0),(2,5,0),(2,6,0),(2,7,0),(2,8,0),(2,9,0),(2,10,1),(2,11,0),(2,12,0),(2,13,0),(2,14,0),(2,15,1),(2,16,1),(2,17,0),(2,18,0),(2,19,0),(2,20,0),(2,21,0),(2,22,0),(2,23,0),(2,24,0),(2,25,0),(2,26,0),(2,27,0),(2,28,0),(2,29,0),(2,30,0),(3,1,0),(3,2,0),(3,3,0),(3,4,1),(3,5,0),(3,6,0),(3,7,0),(3,8,0),(3,9,0),(3,10,0),(3,11,0),(3,12,0),(3,13,1),(3,14,1),(3,15,0),(3,16,0),(3,17,0),(3,18,0),(3,19,0),(3,20,0),(3,21,0),(3,22,0),(3,23,0),(3,24,0),(3,25,0),(3,26,0),(3,27,0),(3,28,0),(3,29,0),(3,30,0),(4,1,0),(4,2,0),(4,3,0),(4,4,1),(4,5,1),(4,6,1),(4,7,0),(4,8,0),(4,9,0),(4,10,0),(4,11,0),(4,12,0),(4,13,0),(4,14,0),(4,15,0),(4,16,0),(4,17,0),(4,18,0),(4,19,0),(4,20,0),(4,21,0),(4,22,0),(4,23,0),(4,24,0),(4,25,0),(4,26,0),(4,27,0),(4,28,0),(4,29,0),(4,30,0),(5,1,1),(5,2,1),(5,3,1),(5,4,1),(5,5,1),(5,6,1),(5,7,0),(5,8,0),(5,9,0),(5,10,0),(5,11,0),(5,12,0),(5,13,0),(5,14,0),(5,15,0),(5,16,0),(5,17,0),(5,18,0),(5,19,0),(5,20,0),(5,21,0),(5,22,0),(5,23,0),(5,24,0),(5,25,0),(5,26,0),(5,27,1),(5,28,1),(5,29,1),(5,30,0),(6,1,1),(6,2,1),(6,3,1),(6,4,1),(6,5,1),(6,6,1),(6,7,0),(6,8,0),(6,9,0),(6,10,0),(6,11,0),(6,12,0),(6,13,0),(6,14,0),(6,15,0),(6,16,0),(6,17,0),(6,18,0),(6,19,0),(6,20,0),(6,21,0),(6,22,0),(6,23,1),(6,24,1),(6,25,1),(6,26,1),(6,27,1),(6,28,1),(6,29,1),(6,30,1);

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
	('Đặng Huy', 'Hoàng', 5000000.00, '2004-06-11', 1, 1),
	('Nguyễn Thành', 'Long', 2000000.00, '2003-04-11', 2, 1),
	('Tần Thiên', 'Lang', 3000000.00, '2000-01-15', 3, 1),
	('Lê Thị', 'B', 3500000.00, '1988-02-20', 3, 1),
	('Phạm Minh', 'C', 4000000.00, '1985-03-25', 3, 1),
	('Nguyễn Thị', 'D', 4500000.00, '1992-04-30', 3, 1),
	('Đỗ Văn', 'E', 2800000.00, '1995-05-05', 6, 1),
	('Bùi Thị', 'F', 3200000.00, '1993-06-10', 5, 1),
	('Ngô Minh', 'G', 2900000.00, '1991-07-15', 4, 1),
	('Trịnh Văn', 'H', 3100000.00, '1989-08-20', 2, 0),
	('Vũ Thị', 'I', 3500000.00, '1994-09-25', 1, 1),
	('Lý Văn', 'J', 3700000.00, '1996-10-30', 1, 1);

CREATE TABLE `account` (
  `employee_id` INT NOT NULL,
  `username` VARCHAR(50) NOT NULL UNIQUE,
  `password` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`employee_id`),
  FOREIGN KEY (`employee_id`) REFERENCES `employee` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

INSERT INTO `account` (`employee_id`, `username`, `password`) VALUES
(1, 'huyhoang119763', '$2a$12$ipuwsQs46H2VAcT1hwS/kuCpv.MXEvJ2IlcPWTyss6Gsm5hpsHWmy'),
(2, 'thanhlong123456', '$2a$12$ipuwsQs46H2VAcT1hwS/kuCpv.MXEvJ2IlcPWTyss6Gsm5hpsHWmy'),
(3, 'tlang01', '$2a$12$ipuwsQs46H2VAcT1hwS/kuCpv.MXEvJ2IlcPWTyss6Gsm5hpsHWmy');

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
('Vãng', 'Lai', null, '0000000000', '', 1),
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
  `endDate` DATE NOT NULL,
  PRIMARY KEY (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

INSERT INTO `discount` (`code`, `name`, `type`, `startDate`, `endDate`) VALUES
('CODE01','Khuyến mãi mùa hạ',0,'2024-02-05','2025-03-31'),
('CODE02','Khuyến mãi mùa hè',1,'2024-03-22','2025-03-31'),
('30T4','30 Tháng 4',0,'2025-04-30','2025-06-11');
CREATE TABLE `detail_discount` (
  `discount_code` VARCHAR(50) NOT NULL,
  `total_price_invoice` DECIMAL(12,2) NOT NULL,
  `discount_amount` DECIMAL(10,2) NOT NULL,
  PRIMARY KEY (`discount_code`, `total_price_invoice`),
  FOREIGN KEY (`discount_code`) REFERENCES `discount` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

INSERT INTO `detail_discount` (`discount_code`, `total_price_invoice`, `discount_amount`) VALUES
('CODE01', 50000.00, 5.00),
('CODE01', 100000.00, 7.00),
('CODE02', 30000.00, 2000.00),
('CODE02', 60000.00, 5000.00),
('30T4', 100000.00, 5.00),
('30T4', 200000.00, 7.00),
('30T4', 300000.00, 9.00);

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
(2, 'Nhà cung cấp B', '0903344556', '04 Tôn Đức Thắng, Phường Bến Nghé, Quận 1, TP Hồ Chí Minh', 1),
(3, 'Nhà cung cấp C', '0903344557', '123 Nguyễn Thị Minh Khai, Quận 3, TP Hồ Chí Minh', 1),
(4, 'Nhà cung cấp D', '0903344558', '456 Lê Lợi, Quận 1, TP Hồ Chí Minh', 1),
(5, 'Nhà cung cấp E', '0903344559', '789 Trường Chinh, Quận Tân Bình, TP Hồ Chí Minh', 1),
(6, 'Nhà cung cấp F', '0903344560', '101 Nguyễn Văn Cừ, Quận 5, TP Hồ Chí Minh', 1),
(7, 'Nhà cung cấp G', '0903344561', '202 Phan Văn Trị, Quận Bình Thạnh, TP Hồ Chí Minh', 1),
(8, 'Nhà cung cấp H', '0903344562', '303 Nguyễn Huệ, Quận 1, TP Hồ Chí Minh', 1),
(9, 'Nhà cung cấp I', '0903344563', '404 Lê Văn Sỹ, Quận 3, TP Hồ Chí Minh', 1),
(10, 'Nhà cung cấp J', '0903344564', '505 Bến Vân Đồn, Quận 4, TP Hồ Chí Minh', 1),
(11, 'Nhà cung cấp K', '0903344565', '606 Đinh Tiên Hoàng, Quận Bình Thạnh, TP Hồ Chí Minh', 1),
(12, 'Nhà cung cấp L', '0903344566', '707 Trần Hưng Đạo, Quận 1, TP Hồ Chí Minh', 1),
(13, 'Nhà cung cấp M', '0903344567', '808 Hoàng Văn Thụ, Quận Tân Bình, TP Hồ Chí Minh', 1),
(14, 'Nhà cung cấp N', '0903344568', '909 Nguyễn Thái Sơn, Quận Gò Vấp, TP Hồ Chí Minh', 1),
(15, 'Nhà cung cấp O', '0903344569', '1001 Lạc Long Quân, Quận 11, TP Hồ Chí Minh', 1),
(16, 'Nhà cung cấp P', '0903344570', '1102 Âu Cơ, Quận Tân Phú, TP Hồ Chí Minh', 1),
(17, 'Nhà cung cấp Q', '0903344571', '1203 Trần Quốc Toản, Quận 3, TP Hồ Chí Minh', 1),
(18, 'Nhà cung cấp R', '0903344572', '1304 Ngô Quyền, Quận 10, TP Hồ Chí Minh', 1),
(19, 'Nhà cung cấp S', '0903344573', '1405 Đinh Bộ Lĩnh, Quận Bình Thạnh, TP Hồ Chí Minh', 1),
(20, 'Nhà cung cấp T', '0903344574', '1506 Huỳnh Tấn Phát, Quận 7, TP Hồ Chí Minh', 1);

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
('SP00001', 'Naruto - 01', 55, 30000.00, 1, 'Minifigure nhân vật Naruto.', 'images/product/sp00001.png', 2),
('SP00002', 'Naruto - 02', 13, 27500.00, 1, 'Minifigure Naruto trong trạng thái chiến đấu.', 'images/product/sp00002.png', 2),
('SP00003', 'Sasuke Uchiha', 0, 0.00, 0, 'Minifigure nhân vật Sasuke Uchiha từ series Naruto.', NULL, 2),
('SP00004', 'Kakashi Hatake', 85, 25000.00, 1, 'Minifigure nhân vật Kakashi với Sharingan.', NULL, 2),
('SP00005', 'Sakura Haruno', 98, 38000.00, 1, 'Minifigure nhân vật Sakura từ series Naruto.', NULL, 2),
('SP00006', 'Luke Skywalker', 0, 0.00, 1, 'Minifigure Luke Skywalker với lightsaber xanh.', NULL, 2),
('SP00007', 'Darth Vader', 51, 60000.00, 1, 'Minifigure Darth Vader với lightsaber đỏ và mặt nạ.', NULL, 2),
('SP00008', 'Iron Man Mark 85', 8, 45000.00, 1, 'Minifigure Iron Man trong bộ giáp Mark 85 từ Avengers: Endgame.', NULL, 2),
('SP00009', 'Spider-Man', 0, 0.00, 1, 'Minifigure Spider-Man với trang phục đỏ và xanh đặc trưng.', NULL, 2),
('SP00010', 'Harry Potter', 86, 150000.00, 1, 'Minifigure Harry Potter với đũa phép và kính tròn.', NULL, 2),
('SP00011', 'LEGO Technic Bugatti Chiron', 0, 43000.00, 1, 'Mô hình kỹ thuật cao của siêu xe Bugatti Chiron, 3599 chi tiết.', NULL, 3),
('SP00012', 'LEGO Technic Land Rover Defender', 0, 0.00, 1, 'Mô hình chi tiết của Land Rover Defender với hộp số hoạt động, 2573 chi tiết.', NULL, 3),
('SP00013', 'LEGO Technic Excavator', 0, 0.00, 1, 'Máy xúc điều khiển từ xa với chức năng nâng và xoay, 569 chi tiết.', NULL, 3),
('SP00014', 'LEGO Technic Race Car', 0, 35600.00, 1, 'Xe đua công thức 1 với động cơ pistons hoạt động, 1580 chi tiết.', NULL, 3),
('SP00015', 'LEGO Technic Liebherr Crane', 0, 0.00, 1, 'Cần cẩu Liebherr điều khiển qua Bluetooth, mô hình lớn nhất của LEGO Technic, 4108 chi tiết.', NULL, 3),
('SP00016', 'LEGO Architecture Statue of Liberty', 0, 0.00, 1, 'Mô hình tượng Nữ thần Tự do, 1685 chi tiết.', NULL, 4),
('SP00017', 'LEGO Architecture Tokyo Skyline', 0, 0.00, 1, 'Đường chân horizon Tokyo với các công trình nổi tiếng, 547 chi tiết.', NULL, 4),
('SP00018', 'LEGO Architecture Paris Skyline', 0, 0.00, 1, 'Đường chân horizon Paris với tháp Eiffel, 649 chi tiết.', NULL, 4),
('SP00019', 'LEGO Architecture Empire State Building', 28, 30500.00, 1, 'Mô hình chi tiết của tòa nhà Empire State, 1767 chi tiết.', NULL, 4),
('SP00020', 'LEGO Architecture Great Wall of China', 0, 0.00, 1, 'Mô hình Vạn Lý Trường Thành, 551 chi tiết.', NULL, 4),
('SP00021', 'LEGO Classic Large Creative Brick Box', 0, 0.00, 1, 'Hộp gạch sáng tạo lớn với nhiều màu sắc, 790 chi tiết.', NULL, 5),
('SP00022', 'LEGO Classic Medium Creative Brick Box', 0, 0.00, 1, 'Hộp gạch sáng tạo vừa, 484 chi tiết.', NULL, 5),
('SP00023', 'LEGO Classic Creative Transparent Bricks', 0, 0.00, 1, 'Bộ gạch trong suốt nhiều màu sắc, 500 chi tiết.', NULL, 5),
('SP00024', 'LEGO Classic Bricks and Wheels', 0, 0.00, 1, 'Bộ gạch với bánh xe để xây dựng các phương tiện, 653 chi tiết.', NULL, 5),
('SP00025', 'LEGO Classic Creative Fun', 0, 0.00, 1, 'Bộ gạch sáng tạo cơ bản, 333 chi tiết.', NULL, 5),
('SP00026', 'MOC - Nhà Cổ Việt Nam', 0, 0.00, 1, 'Mô hình nhà cổ Việt Nam thiết kế riêng, 2205 chi tiết.', NULL, 6),
('SP00027', 'MOC - Chợ Bến Thành', 0, 0.00, 1, 'Mô hình chợ Bến Thành tỉ mỉ, 2789 chi tiết.', NULL, 6),
('SP00028', 'MOC - Cầu Rồng Đà Nẵng', 0, 0.00, 1, 'Mô hình Cầu Rồng Đà Nẵng với đèn LED, 1876 chi tiết.', NULL, 6),
('SP00029', 'MOC - Phố Cổ Hội An', 0, 0.00, 1, 'Mô hình một góc phố cổ Hội An với đèn lồng, 2340 chi tiết.', NULL, 6),
('SP00030', 'MOC - Tháp Rùa Hồ Gươm', 0, 899000.00, 1, 'Mô hình Tháp Rùa trên Hồ Gươm, 1250 chi tiết.', NULL, 6),
('SP00031', 'LEGO City Police Station', 0, 40000.00, 1, 'Trụ sở cảnh sát thành phố với xe cảnh sát và nhà tù, 743 chi tiết.', NULL, 6),
('SP00032', 'LEGO City Fire Station', 0, 0.00, 1, 'Trạm cứu hỏa với xe cứu hỏa và trực thăng, 809 chi tiết.', NULL, 6),
('SP00033', 'LEGO City Hospital', 0, 0.00, 1, 'Bệnh viện thành phố với xe cứu thương, 861 chi tiết.', NULL, 6),
('SP00034', 'LEGO City Cargo Train', 0, 0.00, 1, 'Tàu hỏa chở hàng điều khiển từ xa với đường ray, 1226 chi tiết.', NULL, 6),
('SP00035', 'LEGO City Town Center', 0, 0.00, 1, 'Trung tâm thành phố với nhiều cửa hàng và phương tiện, 790 chi tiết.', NULL, 6),
('SP00036', 'LEGO Star Wars Millennium Falcon', 20, 130000.00, 1, 'Tàu Millennium Falcon với nhiều nhân vật, 1353 chi tiết.', NULL, 4),
('SP00037', 'LEGO Star Wars Imperial Star Destroyer', 0, 0.00, 1, 'Tàu chiến Imperial Star Destroyer, 4784 chi tiết.', NULL, 4),
('SP00038', 'LEGO Star Wars AT-AT', 0, 50000.00, 1, 'Walker AT-AT từ phim The Empire Strikes Back, 1267 chi tiết.', NULL, 4),
('SP00039', 'LEGO Star Wars Death Star', 0, 39000.00, 1, 'Ngôi sao tử thần Death Star với nhiều phòng và nhân vật, 4016 chi tiết.', NULL, 4),
('SP00040', 'LEGO Star Wars X-Wing Starfighter', 0, 95000.00, 1, 'Tàu chiến X-Wing của Luke Skywalker, 761 chi tiết.', NULL, 4),
('SP00041', 'LEGO Marvel Avengers Tower', 0, 0.00, 1, 'Tháp Avengers với nhiều nhân vật siêu anh hùng, 685 chi tiết.', NULL, 3),
('SP00042', 'LEGO Marvel Sanctum Sanctorum', 67, 130000.00, 1, 'Sanctum Sanctorum của Doctor Strange, 2708 chi tiết.', NULL, 3),
('SP00043', 'LEGO Marvel Guardians Ship', 0, 45900.00, 1, 'Tàu của đội Guardians of the Galaxy, 1901 chi tiết.', NULL, 3),
('SP00044', 'LEGO Marvel Spider-Man Daily Bugle', 100, 40000.00, 1, 'Tòa nhà Daily Bugle với nhiều nhân vật Spider-Man, 3772 chi tiết.', NULL, 3),
('SP00045', 'LEGO Marvel Hulkbuster', 0, 45000.00, 1, 'Bộ giáp Hulkbuster của Iron Man, 1363 chi tiết.', NULL, 3),
('SP00046', 'LEGO Harry Potter Hogwarts Castle', 0, 0.00, 1, 'Lâu đài Hogwarts chi tiết với nhiều phòng và nhân vật, 6020 chi tiết.', NULL, 1),
('SP00047', 'LEGO Harry Potter Diagon Alley', 0, 0.00, 1, 'Con phố Diagon Alley với nhiều cửa hàng, 5544 chi tiết.', NULL, 1),
('SP00048', 'LEGO Harry Potter Hogwarts Express', 118, 40000.00, 1, 'Tàu Hogwarts Express với sân ga 9¾, 801 chi tiết.', NULL, 1),
('SP00049', 'LEGO Harry Potter Chamber of Secrets', 0, 60000.00, 1, 'Phòng chứa bí mật với rắn Basilisk, 1176 chi tiết.', NULL, 1),
('SP00050', 'LEGO Harry Potter Quidditch Match', 0, 70000.00, 1, 'Sân đấu Quidditch với các cầu thủ và khán đài, 500 chi tiết.', NULL, 1),
('SP00051', 'LEGO Creator Expert Bookshop', 0, 50000.00, 1, 'Hiệu sách chi tiết với căn hộ ở trên, 2504 chi tiết.', NULL, 2),
('SP00052', 'LEGO Creator Expert Assembly Square', 28, 30000.00, 1, 'Quảng trường trung tâm với nhiều tòa nhà, 4002 chi tiết.', NULL, 4);

CREATE TABLE `invoice` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `create_date` DATETIME NOT NULL,
  `employee_id` INT(11) NOT NULL,
  `customer_id` INT(11) NOT NULL,
  `discount_code` VARCHAR(50),
  `discount_amount` DECIMAL(10,2) NOT NULL,
  `total_price` DECIMAL(12,2) NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`employee_id`) REFERENCES `employee` (`id`),
  FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`),
  FOREIGN KEY (`discount_code`) REFERENCES `discount` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

INSERT INTO `invoice` (`id`, `create_date`, `employee_id`, `customer_id`, `discount_code`, `discount_amount`, `total_price`) VALUES
(1, '2024-01-01 09:07:35', 1, 1, NULL, 0.00, 92400.00),
(2, '2024-01-12 10:51:09', 3, 1, NULL, 0.00, 57500.00),
(3, '2024-01-24 09:08:42', 5, 1, NULL, 0.00, 3249100.00),
(4, '2024-02-05 10:53:47', 1, 15, 'CODE01', 359842.00, 5140600.00),
(5, '2024-02-16 12:02:19', 4, 5, NULL, 0.00, 125000.00),
(6, '2024-02-28 08:35:21', 1, 23, NULL, 0.00, 622000.00),
(7, '2024-03-11 19:47:38', 6, 14, NULL, 0.00, 1290000.00),
(8, '2024-03-22 18:41:30', 4, 28, 'CODE02', 5000.00, 463700.00),
(9, '2024-04-03 09:34:36', 3, 24, NULL, 0.00, 350000.00),
(10, '2024-04-15 20:56:41', 1, 9, NULL, 0.00, 674400.00),
(11, '2024-04-26 11:28:32', 5, 23, NULL, 0.00, 975000.00),
(12, '2024-05-08 12:47:20', 5, 26, 'CODE02', 5000.00, 1635000.00),
(13, '2024-05-20 14:46:16', 1, 24, 'CODE02', 5000.00, 907900.00),
(14, '2024-05-31 15:41:00', 5, 3, NULL, 0.00, 1307700.00),
(15, '2024-06-12 11:50:23', 1, 9, NULL, 0.00, 520000.00),
(16, '2024-06-24 18:51:02', 3, 12, NULL, 0.00, 620000.00),
(17, '2024-07-05 11:12:53', 3, 8, 'CODE01', 107800.00, 1540000.00),
(18, '2024-07-17 10:59:56', 3, 19, 'CODE02', 5000.00, 1071500.00),
(19, '2024-07-29 16:58:43', 1, 12, NULL, 0.00, 580000.00),
(20, '2024-08-09 12:08:25', 3, 7, NULL, 0.00, 680000.00),
(21, '2024-08-21 18:42:37', 6, 20, NULL, 0.00, 520400.00),
(22, '2024-09-02 18:00:24', 1, 23, NULL, 0.00, 627200.00),
(23, '2024-09-13 10:08:16', 6, 3, 'CODE01', 135170.00, 1931000.00),
(24, '2024-09-25 14:51:54', 1, 7, NULL, 0.00, 1694400.00),
(25, '2024-10-07 20:04:55', 6, 9, NULL, 0.00, 591200.00),
(26, '2024-10-18 12:12:07', 1, 12, 'CODE01', 336000.00, 4800000.00),
(27, '2024-10-30 20:22:00', 5, 1, NULL, 0.00, 731700.00),
(28, '2024-11-11 12:12:24', 1, 5, NULL, 0.00, 1260000.00),
(29, '2024-11-22 13:19:37', 3, 11, NULL, 0.00, 1510000.00),
(30, '2024-12-04 20:06:27', 4, 5, NULL, 0.00, 2185000.00),
(31, '2024-12-16 15:45:15', 6, 26, NULL, 0.00, 1960000.00),
(32, '2024-12-27 08:50:04', 1, 9, NULL, 0.00, 1150000.00),
(33, '2025-01-08 20:25:06', 1, 15, NULL, 0.00, 3640000.00),
(34, '2025-01-20 17:02:58', 4, 8, 'CODE01', 54908.00, 784400.00),
(35, '2025-01-31 12:19:22', 1, 1, NULL, 0.00, 3235000.00),
(36, '2025-02-12 15:58:37', 3, 25, 'CODE01', 304801.00, 4354300.00),
(37, '2025-02-24 13:51:05', 1, 14, NULL, 0.00, 5260000.00),
(38, '2025-03-07 19:34:32', 5, 11, 'CODE01', 573125.00, 8187500.00),
(39, '2025-03-19 10:46:02', 5, 20, 'CODE01', 458255.00, 6546500.00),
(40, '2025-03-31 16:27:59', 5, 1, NULL, 0.00, 1787000.00);

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
(1, 'SP00001', 3, 30800.00, 92400.00),
(2, 'SP00002', 2, 28500.00, 57500.00),
(3, 'SP00001', 5, 30000.00, 150000.00),
(3, 'SP00005', 2, 38000.00, 76000.00),
(3, 'SP00008', 4, 45000.00, 180000.00),
(3, 'SP00010', 5, 150000.00, 750000.00),
(3, 'SP00042', 6, 130000.00, 780000.00),
(3, 'SP00043', 9, 45900.00, 413100.00),
(3, 'SP00051', 12, 50000.00, 600000.00),
(3, 'SP00052', 10, 30000.00, 300000.00),
(4, 'SP00019', 12, 30500.00, 366000.00),
(4, 'SP00039', 13, 39000.00, 507000.00),
(4, 'SP00043', 14, 45900.00, 642600.00),
(4, 'SP00045', 13, 45000.00, 585000.00),
(4, 'SP00049', 14, 60000.00, 840000.00),
(4, 'SP00051', 44, 50000.00, 2200000.00),
(5, 'SP00004', 5, 25000.00, 125000.00),
(6, 'SP00010', 3, 150000.00, 450000.00),
(6, 'SP00011', 4, 43000.00, 172000.00),
(7, 'SP00031', 5, 40000.00, 200000.00),
(7, 'SP00042', 5, 130000.00, 650000.00),
(7, 'SP00045', 2, 45000.00, 90000.00),
(7, 'SP00050', 5, 70000.00, 350000.00),
(8, 'SP00039', 4, 39000.00, 156000.00),
(8, 'SP00043', 3, 45900.00, 137700.00),
(8, 'SP00049', 2, 60000.00, 120000.00),
(8, 'SP00051', 1, 50000.00, 50000.00),
(9, 'SP00044', 2, 40000.00, 80000.00),
(9, 'SP00045', 2, 45000.00, 90000.00),
(9, 'SP00049', 3, 60000.00, 180000.00),
(10, 'SP00014', 4, 35600.00, 142400.00),
(10, 'SP00019', 4, 30500.00, 122000.00),
(10, 'SP00038', 4, 50000.00, 200000.00),
(10, 'SP00050', 3, 70000.00, 210000.00),
(11, 'SP00004', 6, 25000.00, 150000.00),
(11, 'SP00008', 5, 45000.00, 225000.00),
(11, 'SP00010', 4, 150000.00, 600000.00),
(12, 'SP00002', 2, 27500.00, 55000.00),
(12, 'SP00004', 4, 25000.00, 100000.00),
(12, 'SP00038', 3, 50000.00, 150000.00),
(12, 'SP00040', 14, 95000.00, 1330000.00),
(13, 'SP00007', 4, 60000.00, 240000.00),
(13, 'SP00019', 4, 30500.00, 122000.00),
(13, 'SP00040', 4, 95000.00, 380000.00),
(13, 'SP00043', 1, 45900.00, 45900.00),
(13, 'SP00049', 2, 60000.00, 120000.00),
(14, 'SP00038', 13, 50000.00, 650000.00),
(14, 'SP00042', 4, 130000.00, 520000.00),
(14, 'SP00043', 3, 45900.00, 137700.00),
(15, 'SP00036', 4, 130000.00, 520000.00),
(16, 'SP00040', 4, 95000.00, 380000.00),
(16, 'SP00049', 4, 60000.00, 240000.00),
(17, 'SP00050', 22, 70000.00, 1540000.00),
(18, 'SP00019', 13, 30500.00, 396500.00),
(18, 'SP00038', 4, 50000.00, 200000.00),
(18, 'SP00040', 5, 95000.00, 475000.00),
(19, 'SP00045', 4, 45000.00, 180000.00),
(19, 'SP00048', 5, 40000.00, 200000.00),
(19, 'SP00051', 4, 50000.00, 200000.00),
(20, 'SP00042', 4, 130000.00, 520000.00),
(20, 'SP00044', 4, 40000.00, 160000.00),
(21, 'SP00011', 6, 43000.00, 258000.00),
(21, 'SP00014', 4, 35600.00, 142400.00),
(21, 'SP00031', 3, 40000.00, 120000.00),
(22, 'SP00014', 12, 35600.00, 427200.00),
(22, 'SP00038', 4, 50000.00, 200000.00),
(23, 'SP00008', 41, 45000.00, 1845000.00),
(23, 'SP00011', 2, 43000.00, 86000.00),
(24, 'SP00014', 24, 35600.00, 854400.00),
(24, 'SP00031', 21, 40000.00, 840000.00),
(25, 'SP00014', 2, 35600.00, 71200.00),
(25, 'SP00042', 4, 130000.00, 520000.00),
(26, 'SP00010', 32, 150000.00, 4800000.00),
(27, 'SP00043', 13, 45900.00, 596700.00),
(27, 'SP00045', 3, 45000.00, 135000.00),
(28, 'SP00049', 21, 60000.00, 1260000.00),
(29, 'SP00051', 23, 50000.00, 1150000.00),
(29, 'SP00052', 12, 30000.00, 360000.00),
(30, 'SP00040', 23, 95000.00, 2185000.00),
(31, 'SP00044', 7, 40000.00, 280000.00),
(31, 'SP00048', 42, 40000.00, 1680000.00),
(32, 'SP00038', 23, 50000.00, 1150000.00),
(33, 'SP00038', 12, 50000.00, 600000.00),
(33, 'SP00040', 32, 95000.00, 3040000.00),
(34, 'SP00014', 4, 35600.00, 142400.00),
(34, 'SP00019', 4, 30500.00, 122000.00),
(34, 'SP00036', 4, 130000.00, 520000.00),
(35, 'SP00038', 21, 50000.00, 1050000.00),
(35, 'SP00040', 23, 95000.00, 2185000.00),
(36, 'SP00011', 18, 43000.00, 774000.00),
(36, 'SP00031', 21, 40000.00, 840000.00),
(36, 'SP00038', 20, 50000.00, 1000000.00),
(36, 'SP00043', 17, 45900.00, 780300.00),
(36, 'SP00045', 16, 45000.00, 720000.00),
(36, 'SP00049', 4, 60000.00, 240000.00),
(37, 'SP00036', 22, 130000.00, 2860000.00),
(37, 'SP00048', 40, 40000.00, 1600000.00),
(37, 'SP00051', 16, 50000.00, 800000.00),
(38, 'SP00007', 45, 60000.00, 2700000.00),
(38, 'SP00010', 25, 150000.00, 3750000.00),
(38, 'SP00019', 25, 30500.00, 762500.00),
(38, 'SP00039', 25, 39000.00, 975000.00),
(39, 'SP00008', 42, 45000.00, 1890000.00),
(39, 'SP00019', 23, 30500.00, 701500.00),
(39, 'SP00039', 45, 39000.00, 1755000.00),
(39, 'SP00044', 55, 40000.00, 2200000.00),
(40, 'SP00039', 13, 39000.00, 507000.00),
(40, 'SP00044', 32, 40000.00, 1280000.00);

CREATE TABLE `import` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `create_date` DATETIME NOT NULL,
  `employee_id` INT(11) NOT NULL,
  `supplier_id` INT(11) NOT NULL,
  `total_price` DECIMAL(12,2) NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`employee_id`) REFERENCES `employee` (`id`),
  FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

INSERT INTO `import` (`create_date`, `employee_id`, `supplier_id`, `total_price`) VALUES
('2024-03-17 14:00:00', 1, 1, 280000.00),
('2024-03-17 15:30:00', 2, 2, 375000.00),
('2024-04-01 19:39:29', 2, 15, 41700000.00),
('2024-07-01 19:40:52', 1, 17, 46170000.00),
('2024-10-01 19:42:14', 1, 18, 30754000.00);

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
(1, 'SP00001', 10, 28000.00, 280000.00),
(2, 'SP00002', 15, 25000.00, 375000.00),
(3, 'SP00001', 50, 30000.00, 1500000.00),
(3, 'SP00031', 50, 40000.00, 2000000.00),
(3, 'SP00038', 100, 50000.00, 5000000.00),
(3, 'SP00039', 100, 39000.00, 3900000.00),
(3, 'SP00040', 100, 95000.00, 9500000.00),
(3, 'SP00044', 200, 40000.00, 8000000.00),
(3, 'SP00048', 205, 40000.00, 8200000.00),
(3, 'SP00050', 30, 70000.00, 2100000.00),
(3, 'SP00052', 50, 30000.00, 1500000.00),
(4, 'SP00004', 100, 25000.00, 2500000.00),
(4, 'SP00005', 100, 38000.00, 3800000.00),
(4, 'SP00007', 100, 60000.00, 6000000.00),
(4, 'SP00008', 100, 45000.00, 4500000.00),
(4, 'SP00010', 155, 150000.00, 23250000.00),
(4, 'SP00011', 30, 43000.00, 1290000.00),
(4, 'SP00014', 50, 35600.00, 1780000.00),
(4, 'SP00019', 100, 30500.00, 3050000.00),
(5, 'SP00036', 50, 130000.00, 6500000.00),
(5, 'SP00042', 90, 130000.00, 11700000.00),
(5, 'SP00043', 60, 45900.00, 2754000.00),
(5, 'SP00045', 40, 45000.00, 1800000.00),
(5, 'SP00049', 50, 60000.00, 3000000.00),
(5, 'SP00051', 100, 50000.00, 5000000.00);