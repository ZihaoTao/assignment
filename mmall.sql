SET NAMES utf8;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
--  Table structure for `mmall_product`
-- ----------------------------
DROP TABLE IF EXISTS `mmall_product`;
CREATE TABLE `mmall_product` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'product id',
  `name` varchar(100) NOT NULL COMMENT 'product name',
  `subtitle` varchar(200) DEFAULT NULL COMMENT 'subtitle',
  `price` decimal(20,2) NOT NULL COMMENT 'unit price, dollar, round to two decimals',
  `status` int(6) DEFAULT '1' COMMENT 'Status.1-onSale 2-offSale 3-delete',
  `create_time` datetime DEFAULT NULL COMMENT 'Create time',
  `update_time` datetime DEFAULT NULL COMMENT 'Update time',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Records of `mmall_product`
-- ----------------------------
BEGIN;
INSERT INTO `mmall_product` VALUES ('1', 'Hair Cut', '45 minutes $55.00', '55.00', '1', '2017-04-09 23:50:14', '2017-04-09 23:50:14'), ('2', 'Beard Trim', '30 minutes $30.00', '30.00', '1', '2017-04-09 23:50:14', '2017-04-09 23:50:14'), ('3', 'Complete Grooming', '60 minutes $70.00', '70.00', '1', '2017-04-09 23:50:14', '2017-04-09 23:50:14');
COMMIT;

-- ----------------------------
--  Table structure for `mmall_order`
-- ----------------------------
DROP TABLE IF EXISTS `mmall_order`;
CREATE TABLE `mmall_order` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Order id',
  `order_no` bigint(20) DEFAULT NULL COMMENT 'Order number',
  `user_id` int(11) DEFAULT NULL COMMENT 'User id',
  `shipping_id` int(11) DEFAULT NULL,
  `payment` decimal(20,2) DEFAULT NULL COMMENT 'Actual amount paid, dollar, round to two decimals',
  `status` int(10) DEFAULT NULL COMMENT 'status:0-cancelled, 10-unpaid, 20-paid, 40-shipped, 50-success, 60-close',
  `payment_time` datetime DEFAULT NULL COMMENT 'payment time',
  `send_time` datetime DEFAULT NULL COMMENT 'send-time',
  `end_time` datetime DEFAULT NULL COMMENT 'end-time',
  `close_time` datetime DEFAULT NULL COMMENT 'close-time',
  `create_time` datetime DEFAULT NULL COMMENT 'Create time',
  `update_time` datetime DEFAULT NULL COMMENT 'Update time',
  PRIMARY KEY (`id`),
  UNIQUE KEY `order_no_index` (`order_no`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=118 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Records of `mmall_order`
-- ----------------------------
BEGIN;
INSERT INTO `mmall_order` VALUES ('103', '1491753014256', '1', '25', '13998.00', '10', null, null, null, null, '2017-04-09 23:50:14', '2017-04-09 23:50:14');
COMMIT;

-- ----------------------------
--  Table structure for `mmall_order_item`
-- ----------------------------
DROP TABLE IF EXISTS `mmall_order_item`;
CREATE TABLE `mmall_order_item` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `user_id` int(11) DEFAULT NULL,
  `order_no` bigint(20) DEFAULT NULL,
  `product_id` int(11) DEFAULT NULL COMMENT 'product id',
  `product_name` varchar(100) DEFAULT NULL COMMENT 'name',
  `current_unit_price` decimal(20,2) DEFAULT NULL COMMENT 'unit price, dollar, round to two decimals',
  `quantity` int(10) DEFAULT NULL COMMENT 'number',
  `total_price` decimal(20,2) DEFAULT NULL COMMENT 'total price, dollar, round to two decimals',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `order_no_index` (`order_no`) USING BTREE,
  KEY `order_no_user_id_index` (`user_id`,`order_no`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=135 DEFAULT CHARSET=utf8;


-- ----------------------------
--  Table structure for `mmall_shipping`
-- ----------------------------
DROP TABLE IF EXISTS `mmall_shipping`;
CREATE TABLE `mmall_shipping` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL COMMENT 'user id',
  `receiver_name` varchar(20) DEFAULT NULL COMMENT 'name',
  `receiver_phone` varchar(20) DEFAULT NULL COMMENT 'phone',
  `receiver_mobile` varchar(20) DEFAULT NULL COMMENT 'mobile',
  `receiver_state` varchar(20) DEFAULT NULL COMMENT 'state',
  `receiver_city` varchar(20) DEFAULT NULL COMMENT 'city',
  `receiver_district` varchar(20) DEFAULT NULL COMMENT 'district',
  `receiver_address` varchar(200) DEFAULT NULL COMMENT 'address',
  `receiver_zip` varchar(6) DEFAULT NULL COMMENT 'zip-code',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `mmall_user`
-- ----------------------------
DROP TABLE IF EXISTS `mmall_user`;
CREATE TABLE `mmall_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'user id',
  `username` varchar(50) NOT NULL COMMENT 'user name',
  `password` varchar(50) NOT NULL COMMENT 'password, MD5 encrypted',
  `email` varchar(50) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `question` varchar(100) DEFAULT NULL COMMENT 'security question',
  `answer` varchar(100) DEFAULT NULL COMMENT 'security answer',
  `role` int(4) NOT NULL COMMENT '0-admin,1-user',
  `create_time` datetime NOT NULL COMMENT 'Create time',
  `update_time` datetime NOT NULL COMMENT 'Update time',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_name_unique` (`username`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Records of `mmall_user`
-- ----------------------------
BEGIN;
INSERT INTO `mmall_user` VALUES ('1', 'admin', '427338237BD929443EC5D48E24FD2B1A', 'admin@happymmall.com', '13800138000', 'q', 'a', '1', '2016-11-06 16:56:45', '2017-04-04 19:27:36'), ('13', 'geely', '08E9A6EA287E70E7E3F7C982BF7923AC', 'geely@happymmall.com', '13800138000', '问题', '答案', '0', '2016-11-19 22:19:25', '2016-11-19 22:19:25'), ('17', 'rosen', '095AC193FE2212EEC7A93E8FEFF11902', 'rosen1@happymmall.com', '13800138000', '问题', '答案', '0', '2017-03-17 10:51:33', '2017-04-09 23:13:26'), ('21', 'soonerbetter', 'DE6D76FE7C40D5A1A8F04213F2BEFBEE', 'test06@happymmall.com', '13800138000', '105204', '105204', '0', '2017-04-13 21:26:22', '2017-04-13 21:26:22');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
