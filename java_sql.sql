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
(2, 'Nhân viên quản lý kho', null, 2.5);

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
    ('Nguyễn Thành', 'Long', 2000000, '2003-04-11', 1, 1),
    ('Tần Thiên', 'Lang', 3000000, '2000-01-15', 1, 1),
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
('30T4', '30 Tháng 4', 0, '2025-03-11', '2025-06-11'),
('30T6', '30 Tháng 6', 1, '2025-03-11', '2025-06-11');

CREATE TABLE `detail_discount` (
  `discount_code` VARCHAR(50) NOT NULL,
  `total_price_invoice` DECIMAL(12,2) NOT NULL,
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
('SP00001', 'Naruto - 01', 10, 30800, 1, 'Minifigure nhân vật Naruto.', 'images/product/sp00001.png', 2),
('SP00002', 'Naruto - 02', 15, 27500, 1, 'Minifigure Naruto trong trạng thái chiến đấu.', 'images/product/sp00002.png', 2),
('SP00003', 'Sasuke Uchiha', 0, 32500, 1, 'Minifigure nhân vật Sasuke Uchiha từ series Naruto.', NULL, 2),
('SP00004', 'Kakashi Hatake', 0, 34000, 1, 'Minifigure nhân vật Kakashi với Sharingan.', NULL, 2),
('SP00005', 'Sakura Haruno', 0, 29900, 1, 'Minifigure nhân vật Sakura từ series Naruto.', NULL, 2),
('SP00006', 'Luke Skywalker', 0, 42000, 1, 'Minifigure Luke Skywalker với lightsaber xanh.', NULL, 2),
('SP00007', 'Darth Vader', 0, 45000, 1, 'Minifigure Darth Vader với lightsaber đỏ và mặt nạ.', NULL, 2),
('SP00008', 'Iron Man Mark 85', 0, 48500, 1, 'Minifigure Iron Man trong bộ giáp Mark 85 từ Avengers: Endgame.', NULL, 2),
('SP00009', 'Spider-Man', 0, 39900, 1, 'Minifigure Spider-Man với trang phục đỏ và xanh đặc trưng.', NULL, 2),
('SP00010', 'Harry Potter', 0, 38500, 1, 'Minifigure Harry Potter với đũa phép và kính tròn.', NULL, 2),

-- Technic sets (category_id = 3)
('SP00011', 'LEGO Technic Bugatti Chiron', 0, 2499000, 1, 'Mô hình kỹ thuật cao của siêu xe Bugatti Chiron, 3599 chi tiết.', NULL, 3),
('SP00012', 'LEGO Technic Land Rover Defender', 0, 1799000, 1, 'Mô hình chi tiết của Land Rover Defender với hộp số hoạt động, 2573 chi tiết.', NULL, 3),
('SP00013', 'LEGO Technic Excavator', 0, 899000, 1, 'Máy xúc điều khiển từ xa với chức năng nâng và xoay, 569 chi tiết.', NULL, 3),
('SP00014', 'LEGO Technic Race Car', 0, 1199000, 1, 'Xe đua công thức 1 với động cơ pistons hoạt động, 1580 chi tiết.', NULL, 3),
('SP00015', 'LEGO Technic Liebherr Crane', 0, 3799000, 1, 'Cần cẩu Liebherr điều khiển qua Bluetooth, mô hình lớn nhất của LEGO Technic, 4108 chi tiết.', NULL, 3),

-- Architecture (category_id = 4)
('SP00016', 'LEGO Architecture Statue of Liberty', 0, 999000, 1, 'Mô hình tượng Nữ thần Tự do, 1685 chi tiết.', NULL, 4),
('SP00017', 'LEGO Architecture Tokyo Skyline', 0, 599000, 1, 'Đường chân trời Tokyo với các công trình nổi tiếng, 547 chi tiết.', NULL, 4),
('SP00018', 'LEGO Architecture Paris Skyline', 0, 599000, 1, 'Đường chân trời Paris với tháp Eiffel, 649 chi tiết.', NULL, 4),
('SP00019', 'LEGO Architecture Empire State Building', 0, 1299000, 1, 'Mô hình chi tiết của tòa nhà Empire State, 1767 chi tiết.', NULL, 4),
('SP00020', 'LEGO Architecture Great Wall of China', 0, 799000, 1, 'Mô hình Vạn Lý Trường Thành, 551 chi tiết.', NULL, 4),

-- Classic (category_id = 5)
('SP00021', 'LEGO Classic Large Creative Brick Box', 0, 599000, 1, 'Hộp gạch sáng tạo lớn với nhiều màu sắc, 790 chi tiết.', NULL, 5),
('SP00022', 'LEGO Classic Medium Creative Brick Box', 0, 399000, 1, 'Hộp gạch sáng tạo vừa, 484 chi tiết.', NULL, 5),
('SP00023', 'LEGO Classic Creative Transparent Bricks', 0, 299000, 1, 'Bộ gạch trong suốt nhiều màu sắc, 500 chi tiết.', NULL, 5),
('SP00024', 'LEGO Classic Bricks and Wheels', 0, 349000, 1, 'Bộ gạch với bánh xe để xây dựng các phương tiện, 653 chi tiết.', NULL, 5),
('SP00025', 'LEGO Classic Creative Fun', 0, 249000, 1, 'Bộ gạch sáng tạo cơ bản, 333 chi tiết.', NULL, 5),

-- MOC (My Own Creation) (category_id = 6)
('SP00026', 'MOC - Nhà Cổ Việt Nam', 0, 1299000, 1, 'Mô hình nhà cổ Việt Nam thiết kế riêng, 2205 chi tiết.', NULL, 6),
('SP00027', 'MOC - Chợ Bến Thành', 0, 1599000, 1, 'Mô hình chợ Bến Thành tỉ mỉ, 2789 chi tiết.', NULL, 6),
('SP00028', 'MOC - Cầu Rồng Đà Nẵng', 0, 999000, 1, 'Mô hình Cầu Rồng Đà Nẵng với đèn LED, 1876 chi tiết.', NULL, 6),
('SP00029', 'MOC - Phố Cổ Hội An', 0, 1499000, 1, 'Mô hình một góc phố cổ Hội An với đèn lồng, 2340 chi tiết.', NULL, 6),
('SP00030', 'MOC - Tháp Rùa Hồ Gươm', 0, 899000, 1, 'Mô hình Tháp Rùa trên Hồ Gươm, 1250 chi tiết.', NULL, 6),

-- City (category_id = 7)
('SP00031', 'LEGO City Police Station', 0, 1399000, 1, 'Trụ sở cảnh sát thành phố với xe cảnh sát và nhà tù, 743 chi tiết.', NULL, 6),
('SP00032', 'LEGO City Fire Station', 0, 1299000, 1, 'Trạm cứu hỏa với xe cứu hỏa và trực thăng, 809 chi tiết.', NULL, 6),
('SP00033', 'LEGO City Hospital', 0, 1199000, 1, 'Bệnh viện thành phố với xe cứu thương, 861 chi tiết.', NULL, 6),
('SP00034', 'LEGO City Cargo Train', 0, 1799000, 1, 'Tàu hỏa chở hàng điều khiển từ xa với đường ray, 1226 chi tiết.', NULL, 6),
('SP00035', 'LEGO City Town Center', 0, 1599000, 1, 'Trung tâm thành phố với nhiều cửa hàng và phương tiện, 790 chi tiết.', NULL, 6),

-- Star Wars (category_id = 8)
('SP00036', 'LEGO Star Wars Millennium Falcon', 0, 2999000, 1, 'Tàu Millennium Falcon với nhiều nhân vật, 1353 chi tiết.', NULL, 4),
('SP00037', 'LEGO Star Wars Imperial Star Destroyer', 0, 3499000, 1, 'Tàu chiến Imperial Star Destroyer, 4784 chi tiết.', NULL, 4),
('SP00038', 'LEGO Star Wars AT-AT', 0, 1899000, 1, 'Walker AT-AT từ phim The Empire Strikes Back, 1267 chi tiết.', NULL, 4),
('SP00039', 'LEGO Star Wars Death Star', 0, 4999000, 1, 'Ngôi sao tử thần Death Star với nhiều phòng và nhân vật, 4016 chi tiết.', NULL, 4),
('SP00040', 'LEGO Star Wars X-Wing Starfighter', 0, 999000, 1, 'Tàu chiến X-Wing của Luke Skywalker, 761 chi tiết.', NULL, 4),

-- Marvel Super Heroes (category_id = 14)
('SP00041', 'LEGO Marvel Avengers Tower', 0, 1799000, 1, 'Tháp Avengers với nhiều nhân vật siêu anh hùng, 685 chi tiết.', NULL, 3),
('SP00042', 'LEGO Marvel Sanctum Sanctorum', 0, 1999000, 1, 'Sanctum Sanctorum của Doctor Strange, 2708 chi tiết.', NULL, 3),
('SP00043', 'LEGO Marvel Guardians Ship', 0, 1499000, 1, 'Tàu của đội Guardians of the Galaxy, 1901 chi tiết.', NULL, 3),
('SP00044', 'LEGO Marvel Spider-Man Daily Bugle', 0, 2999000, 1, 'Tòa nhà Daily Bugle với nhiều nhân vật Spider-Man, 3772 chi tiết.', NULL, 3),
('SP00045', 'LEGO Marvel Hulkbuster', 0, 1099000, 1, 'Bộ giáp Hulkbuster của Iron Man, 1363 chi tiết.', NULL, 3),

-- Harry Potter (category_id = 17)
('SP00046', 'LEGO Harry Potter Hogwarts Castle', 0, 3999000, 1, 'Lâu đài Hogwarts chi tiết với nhiều phòng và nhân vật, 6020 chi tiết.', NULL, 1),
('SP00047', 'LEGO Harry Potter Diagon Alley', 0, 2799000, 1, 'Con phố Diagon Alley với nhiều cửa hàng, 5544 chi tiết.', NULL, 1),
('SP00048', 'LEGO Harry Potter Hogwarts Express', 0, 899000, 1, 'Tàu Hogwarts Express với sân ga 9¾, 801 chi tiết.', NULL, 1),
('SP00049', 'LEGO Harry Potter Chamber of Secrets', 0, 1299000, 1, 'Phòng chứa bí mật với rắn Basilisk, 1176 chi tiết.', NULL, 1),
('SP00050', 'LEGO Harry Potter Quidditch Match', 0, 499000, 1, 'Sân đấu Quidditch với các cầu thủ và khán đài, 500 chi tiết.', NULL, 1),

-- Creator Expert (category_id = 9)
('SP00051', 'LEGO Creator Expert Bookshop', 0, 1799000, 1, 'Hiệu sách chi tiết với căn hộ ở trên, 2504 chi tiết.', NULL, 2),
('SP00052', 'LEGO Creator Expert Assembly Square', 0, 2499000, 1, 'Quảng trường trung tâm với nhiều tòa nhà, 4002 chi tiết.', NULL, 4);

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

INSERT INTO `invoice` (`create_date`, `employee_id`, `customer_id`, `discount_code`, `discount_amount`, `total_price`) VALUES
('2025-03-17 14:00:00', 1, 1, NULL, 0, 92400),  -- Hóa đơn có giảm giá
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
  `total_price` DECIMAL(12,2) NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`employee_id`) REFERENCES `employee` (`id`),
  FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

INSERT INTO `import` (`create_date`, `employee_id`, `supplier_id`, `total_price`) VALUES
('2024-03-17 14:00:00', 1, 1, 280000), 
('2024-03-17 15:30:00', 1, 2, 375000);

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
