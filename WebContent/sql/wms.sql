/*
Navicat MySQL Data Transfer

Source Server         : test
Source Server Version : 50149
Source Host           : localhost:33306
Source Database       : wms

Target Server Type    : MYSQL
Target Server Version : 50149
File Encoding         : 65001

Date: 2016-02-16 19:23:50
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for inventory_item
-- ----------------------------
DROP TABLE IF EXISTS `inventory_item`;
CREATE TABLE `inventory_item` (
  `inventory_item_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `physical_warehouse_id` int(11) NOT NULL COMMENT '物理仓储id',
  `warehouse_id` int(11) NOT NULL COMMENT '仓库编号',
  `customer_id` int(11) NOT NULL COMMENT '卖家编码，保证唯一',
  `product_id` int(10) unsigned NOT NULL COMMENT '商品id',
  `quantity_on_hand_total` int(10) unsigned NOT NULL DEFAULT '0',
  `status` varchar(30) DEFAULT '' COMMENT '商品新旧状态,良品:NORMAL,不良品:DEFECTIVE',
  `unit_cost` decimal(15,6) DEFAULT NULL COMMENT '商品采购单价',
  `provider_code` varchar(32) NOT NULL COMMENT '供应商',
  `validity` datetime DEFAULT '1970-01-01 00:00:00' COMMENT '生产日期',
  `batch_sn` varchar(32) DEFAULT '' COMMENT '批次号',
  `serial_number` varchar(256) DEFAULT NULL COMMENT '串号',
  `inventory_item_acct_type_id` varchar(30) DEFAULT NULL COMMENT '库存类型B2C,DX等',
  `currency` char(10) DEFAULT 'RMB',
  `parent_inventory_item_id` int(10) unsigned DEFAULT NULL,
  `root_inventory_item_id` int(10) unsigned DEFAULT NULL,
  `created_user` varchar(64) DEFAULT NULL COMMENT '创建者',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `last_updated_user` varchar(64) DEFAULT NULL COMMENT '最后更新人',
  `last_updated_time` datetime DEFAULT NULL COMMENT '最后更新时间',
  PRIMARY KEY (`inventory_item_id`),
  KEY `product_id` (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=44 DEFAULT CHARSET=utf8 COMMENT='商品库存表';

-- ----------------------------
-- Records of inventory_item
-- ----------------------------
INSERT INTO `inventory_item` VALUES ('1', '4534', '12306', '54', '54', '8', 'AVAILABLE', '10.000000', '12345', '1970-01-01 00:00:00', '2016-02-04', null, null, 'RMB', null, null, null, null, 'xjye', '2016-02-15 17:24:36');
INSERT INTO `inventory_item` VALUES ('2', '4534', '2', '0', '1', '1', 'NORMAL', '2.000000', '', '1970-01-01 00:00:00', '', null, null, 'RMB', null, null, null, null, null, null);
INSERT INTO `inventory_item` VALUES ('3', '0', '0', '65553', '1', '5', 'AVAILABLE', '2.000000', '', '1970-01-01 00:00:00', '', null, 'B2C', 'RMB', null, null, null, null, 'qyyao', '2016-02-16 19:22:13');
INSERT INTO `inventory_item` VALUES ('4', '0', '0', '65553', '2', '0', 'AVAILABLE', '28.000000', '', '1970-01-01 00:00:00', '', '20160204110', 'B2C', 'RMB', null, null, null, null, 'qyyao', '2016-02-16 19:22:16');
INSERT INTO `inventory_item` VALUES ('5', '0', '0', '65553', '2', '1', 'AVAILABLE', '28.000000', '', '1970-01-01 00:00:00', '', '20160204111', 'B2C', 'RMB', null, null, null, null, 'qyyao', '2016-02-16 19:22:16');
INSERT INTO `inventory_item` VALUES ('6', '0', '12306', '65553', '55', '49', 'AVAILABLE', '20.000000', '', '1970-01-01 00:00:00', '', '20160204112', 'B2C', 'RMB', null, null, null, null, 'hello', '2016-02-16 18:18:20');
INSERT INTO `inventory_item` VALUES ('8', '1', '1', '65553', '1', '1', 'NORMAL', '0.000000', '', '2016-02-05 15:16:30', '2016-02-04', '', 'B2C', 'RMB', '0', '0', 'hzhang1', '2016-02-05 15:16:30', 'hzhang1', '2016-02-05 15:16:30');

-- ----------------------------
-- Table structure for inventory_item_detail
-- ----------------------------
DROP TABLE IF EXISTS `inventory_item_detail`;
CREATE TABLE `inventory_item_detail` (
  `inventory_item_detail_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `inventory_item_id` int(10) unsigned NOT NULL COMMENT '库存表id',
  `product_id` int(11) DEFAULT NULL,
  `change_quantity` int(11) NOT NULL,
  `order_id` int(10) unsigned NOT NULL COMMENT '订单id',
  `order_goods_id` int(10) unsigned NOT NULL COMMENT '订单商品表id',
  `customer_id` int(11) NOT NULL COMMENT '卖家编码，保证唯一',
  `warehouse_id` int(11) NOT NULL COMMENT '仓库编号',
  `packbox_customer_id` varchar(64) NOT NULL COMMENT '卖家编码，保证唯一',
  `packbox_warehouse_id` int(11) NOT NULL COMMENT '耗材仓库编号',
  `created_user` varchar(64) DEFAULT NULL COMMENT '创建者',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `last_updated_user` varchar(64) DEFAULT NULL COMMENT '最后更新人',
  `last_updated_time` datetime DEFAULT NULL COMMENT '最后更新时间',
  PRIMARY KEY (`inventory_item_detail_id`),
  KEY `order_id` (`order_id`),
  KEY `order_goods_id` (`order_goods_id`)
) ENGINE=InnoDB AUTO_INCREMENT=52 DEFAULT CHARSET=utf8 COMMENT='商品出入库明细表';

-- ----------------------------
-- Records of inventory_item_detail
-- ----------------------------
INSERT INTO `inventory_item_detail` VALUES ('1', '1', '1', '0', '17955476', '2', '65553', '0', '', '0', null, null, null, null);
INSERT INTO `inventory_item_detail` VALUES ('2', '1', '2', '1', '17955476', '2', '65553', '1', '', '0', '', '0000-00-00 00:00:00', '', '0000-00-00 00:00:00');
INSERT INTO `inventory_item_detail` VALUES ('3', '3', '1', '-8', '17955475', '8', '65553', '0', '', '0', 'zjli', '2016-02-02 00:00:00', 'zjli', '2016-02-02 00:00:00');
INSERT INTO `inventory_item_detail` VALUES ('4', '4', '2', '-1', '17955475', '9', '65553', '0', '', '0', 'zjli', '2016-02-02 00:00:00', 'zjli', '2016-02-02 00:00:00');
INSERT INTO `inventory_item_detail` VALUES ('5', '5', '2', '-1', '17955475', '9', '65553', '0', '', '0', 'zjli', '2016-02-02 00:00:00', 'zjli', '2016-02-02 00:00:00');
INSERT INTO `inventory_item_detail` VALUES ('6', '6', '2', '-1', '17955475', '9', '65553', '0', '', '0', 'zjli', '2016-02-02 00:00:00', 'zjli', '2016-02-02 00:00:00');
INSERT INTO `inventory_item_detail` VALUES ('40', '1', null, '-1', '17955480', '10', '555', '12306', '', '0', 'xjye', '2016-02-15 17:26:03', 'xjye', '2016-02-15 17:26:03');
INSERT INTO `inventory_item_detail` VALUES ('45', '6', null, '-1', '17955482', '12', '555', '12306', '', '0', 'hello', '2016-02-16 18:19:47', 'hello', '2016-02-16 18:19:47');
INSERT INTO `inventory_item_detail` VALUES ('49', '3', '1', '-5', '17955479', '5', '65553', '0', '', '0', 'qyyao', '2016-02-16 19:22:13', null, '2016-02-16 19:22:13');
INSERT INTO `inventory_item_detail` VALUES ('50', '4', '2', '-2', '17955479', '6', '65553', '0', '', '0', 'qyyao', '2016-02-16 19:22:16', null, '2016-02-16 19:22:16');
INSERT INTO `inventory_item_detail` VALUES ('51', '5', '2', '-1', '17955479', '6', '65553', '0', '', '0', 'qyyao', '2016-02-16 19:22:16', null, '2016-02-16 19:22:16');

-- ----------------------------
-- Table structure for order_goods
-- ----------------------------
DROP TABLE IF EXISTS `order_goods`;
CREATE TABLE `order_goods` (
  `order_goods_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `order_id` int(10) unsigned NOT NULL COMMENT '订单号',
  `warehouse_id` int(11) NOT NULL COMMENT '仓库编号',
  `customer_id` varchar(64) NOT NULL COMMENT '卖家编码，保证唯一',
  `product_id` int(10) unsigned NOT NULL COMMENT '商品id',
  `goods_name` varchar(256) NOT NULL COMMENT '商品名',
  `goods_number` int(10) unsigned NOT NULL DEFAULT '1' COMMENT '商品数量',
  `goods_price` decimal(16,6) NOT NULL DEFAULT '0.000000' COMMENT '商品单价',
  `discount` decimal(16,6) NOT NULL DEFAULT '0.000000' COMMENT '商品折扣金额',
  `inventory_change_number` int(11) DEFAULT NULL,
  `batch_sn` varchar(64) NOT NULL DEFAULT '' COMMENT '批次号',
  `status_id` varchar(30) DEFAULT 'AVAILABLE' COMMENT '商品新旧状态',
  `tax_rate` decimal(10,4) unsigned NOT NULL DEFAULT '1.1700' COMMENT '税率',
  `order_goods_type` char(10) DEFAULT NULL COMMENT 'GOODS  PACKBOX',
  `created_user` varchar(64) DEFAULT NULL COMMENT '创建者',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `last_updated_user` varchar(64) DEFAULT NULL COMMENT '最后更新人',
  `last_updated_time` datetime DEFAULT NULL COMMENT '最后更新时间',
  PRIMARY KEY (`order_goods_id`),
  KEY `order_id` (`order_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8 COMMENT='商品表';

-- ----------------------------
-- Records of order_goods
-- ----------------------------
INSERT INTO `order_goods` VALUES ('1', '17955476', '1', '65553', '1', '12237391#雀巢淡奶油250ml × 1', '1', '2.000000', '0.000000', null, '2016-02-04', 'AVAILABLE', '1.1700', null, 'hzhang1', '2016-02-02 17:33:06', 'hzhang1', '2016-02-02 17:33:25');
INSERT INTO `order_goods` VALUES ('2', '17955477', '0', '65553', '1', '12237391#雀巢淡奶油250ml × 1', '3', '0.000000', '0.000000', null, '', 'AVAILABLE', '1.1700', null, 'zjli', '2016-02-02 14:00:00', 'zjli', '2016-02-02 14:00:00');
INSERT INTO `order_goods` VALUES ('3', '17955477', '0', '65553', '2', '#雀巢牛奶250ml × 1', '3', '0.000000', '0.000000', null, '', 'AVAILABLE', '1.1700', null, 'zjli', '2016-02-02 14:00:00', 'zjli', '2016-02-02 14:00:00');
INSERT INTO `order_goods` VALUES ('4', '17955478', '0', '65553', '1', '12237391#雀巢淡奶油250ml × 1', '5', '0.000000', '0.000000', null, '', 'AVAILABLE', '1.1700', null, 'zjli', '2016-02-02 14:00:00', 'zjli', '2016-02-02 14:00:00');
INSERT INTO `order_goods` VALUES ('5', '17955479', '0', '65553', '1', '12237391#雀巢淡奶油250ml × 1', '5', '0.000000', '0.000000', null, '', 'AVAILABLE', '1.1700', '', 'qyyao', '2016-02-02 14:00:00', 'qyyao', '2016-02-03 14:00:00');
INSERT INTO `order_goods` VALUES ('6', '17955479', '0', '65553', '2', '#雀巢牛奶250ml × 1', '3', '0.000000', '0.000000', null, '', 'AVAILABLE', '1.1700', '', 'qyyao', '2016-02-03 14:00:00', 'qyyao', '2016-02-03 14:00:00');
INSERT INTO `order_goods` VALUES ('7', '17955476', '1', '65553', '2', '#雀巢牛奶250ml × 1', '2', '2.000000', '0.000000', null, '', 'AVAILABLE', '1.1700', '', 'hzhang1', '0000-00-00 00:00:00', 'hzhang1', '0000-00-00 00:00:00');
INSERT INTO `order_goods` VALUES ('8', '17955475', '0', '65553', '1', '12237391#雀巢淡奶油250ml × 1', '8', '0.000000', '0.000000', null, '', 'AVAILABLE', '1.1700', null, 'zjli', '2016-02-02 14:00:00', 'zjli', '2016-02-02 14:00:00');
INSERT INTO `order_goods` VALUES ('9', '17955475', '0', '65553', '2', '#雀巢牛奶250ml × 1', '3', '0.000000', '0.000000', null, '', 'AVAILABLE', '1.1700', null, 'zjli', '2016-02-02 14:00:00', 'zjli', '2016-02-02 14:00:00');
INSERT INTO `order_goods` VALUES ('10', '17955480', '12306', '65558  ', '54', '奶油小豆腐', '8', '10.000000', '0.000000', null, '2016-02-04', 'AVAILABLE', '1.1700', null, 'xjye', '2016-02-15 12:47:49', 'xjye', '2016-02-15 12:47:55');
INSERT INTO `order_goods` VALUES ('11', '17955481', '1', '65553', '1', '12237391#雀巢淡奶油250ml × 1', '1', '2.000000', '0.000000', null, '2016-02-04', 'AVAILABLE', '1.1700', '', 'hzhang1', '2016-02-15 17:33:16', 'hzhang1', '2016-02-15 17:33:29');
INSERT INTO `order_goods` VALUES ('12', '17955482', '12306', '61213', '55', '超级计算机', '1', '20.000000', '0.000000', null, '', 'AVAILABLE', '1.1700', null, 'xjye', '2016-02-16 18:02:04', 'xjye', '2016-02-16 18:02:14');

-- ----------------------------
-- Table structure for order_info
-- ----------------------------
DROP TABLE IF EXISTS `order_info`;
CREATE TABLE `order_info` (
  `order_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `order_sn` char(32) NOT NULL,
  `order_status` char(20) NOT NULL COMMENT '订单物流状态：WMS_ACCEPT -仓库接单,WMS_PRINT – 打印,WMS_PICK – 捡货,WMS_CHECK – 复核,WMS_PACKAGE – 打包,WMS_WEIGH - 称重,DELIVERED - 已发货,EXCEPTION - 异常,CLOSED - 关闭,CANCELED - 取消,TO_BE_RECOVERED - 待追回,RECOVERED - 已追回',
  `customer_id` int(11) NOT NULL COMMENT '货主id',
  `physical_warehouse_id` int(11) NOT NULL COMMENT '物理仓储id',
  `warehouse_id` int(11) NOT NULL COMMENT '仓库编号',
  `order_type` varchar(64) DEFAULT NULL COMMENT '订单类型：SALE,RETURN,PURCHASE,SUPPLIER_RETURN,VARIANCE_ADD,VARIANCE_MINUS',
  `oms_order_sn` varchar(64) NOT NULL COMMENT '客户ERP 交易生成订单号，卖家系统保证唯一',
  `oms_order_type` varchar(32) DEFAULT '' COMMENT 'SALE,RMA_EXCHANGE,RMA_RETURN,PURCHASE,SUPPLIER_SALE,SUPPLIER_RETURN,VARIANCE_ADD,VARIANCE_MINUS',
  `order_time` datetime DEFAULT NULL COMMENT '下单时间（yyyy-MM-dd HH:mm:ss）',
  `pay_time` datetime DEFAULT NULL COMMENT '付款时间（yyyy-MM-dd HH:mm:ss）',
  `is_reserved` char(1) NOT NULL DEFAULT 'N' COMMENT '预定是否成功',
  `reserved_time` datetime DEFAULT NULL COMMENT '预定时间',
  `shipping_time` datetime DEFAULT NULL COMMENT '发货时间（yyyy-MM-dd HH:mm:ss）',
  `goods_amount` decimal(15,6) DEFAULT NULL COMMENT '商品总金额，可用于买保险',
  `shipping_amount` decimal(15,6) DEFAULT NULL COMMENT '运费',
  `discount_amount` decimal(15,6) DEFAULT NULL COMMENT '折扣费',
  `pay_amount` decimal(15,6) DEFAULT NULL COMMENT '实际支付费',
  `buyer_note` varchar(512) DEFAULT NULL COMMENT '买家备注',
  `seller_note` varchar(512) DEFAULT NULL COMMENT '卖家备注',
  `note` varchar(512) DEFAULT NULL COMMENT '订单备注',
  `receive_name` varchar(64) NOT NULL COMMENT '收件人姓名',
  `postal_code` varchar(64) DEFAULT NULL COMMENT '收件人邮编',
  `phone_number` varchar(64) DEFAULT NULL COMMENT '收件人电话，包括区号、电话号码及分机号，中间用“-”分隔；',
  `mobile_number` varchar(64) DEFAULT NULL COMMENT '收件人移动电话',
  `province_id` int(11) DEFAULT NULL COMMENT '收件人所在省id',
  `province_name` varchar(64) DEFAULT NULL COMMENT '收件人所在省，如浙江省、北京',
  `city_id` int(11) DEFAULT NULL COMMENT '收件人所在市id',
  `city_name` varchar(64) DEFAULT NULL COMMENT '收件人所在市，如杭州市、上海市',
  `district_id` int(11) DEFAULT NULL COMMENT '收件人所在县id',
  `district_name` varchar(64) DEFAULT NULL COMMENT '收件人所在县（区），注意有些市下面是没有区的，如：义乌市',
  `shipping_address` varchar(256) DEFAULT NULL COMMENT '收件人详细地址，不包含省市',
  `email` varchar(128) DEFAULT NULL COMMENT '邮件',
  `sex` char(10) NOT NULL DEFAULT '' COMMENT '性别  male , female  ',
  `invoice_title` varchar(512) DEFAULT NULL COMMENT '发票抬头',
  `invoice_note` varchar(512) DEFAULT NULL COMMENT '发票内容',
  `invoice_amount` decimal(15,6) DEFAULT NULL COMMENT '发票金额',
  `is_value_declared` char(1) NOT NULL DEFAULT 'N' COMMENT '是否保价',
  `declaring_value_amount` decimal(15,6) DEFAULT NULL COMMENT '保价金额',
  `is_payment_collected` char(1) NOT NULL DEFAULT 'N' COMMENT '是否代收货款',
  `collecting_payment_amount` decimal(10,2) DEFAULT NULL COMMENT '代收货款金额',
  `priority_code` varchar(32) DEFAULT '' COMMENT '发货优先级',
  `order_source` varchar(64) DEFAULT NULL COMMENT '订单来源（ 如： 360BUY, PAIPAI,DANGDANG, TAOBAO,OTHER）',
  `parent_order_id` int(10) unsigned DEFAULT NULL COMMENT '父订单id',
  `root_order_id` int(10) unsigned DEFAULT NULL COMMENT '根订单id',
  `brand_name` varchar(32) NOT NULL COMMENT '品牌',
  `shop_name` varchar(64) DEFAULT NULL COMMENT '店铺名称',
  `shop_order_sn` varchar(64) DEFAULT NULL COMMENT '卖家销售订单编号',
  `buyer_name` varchar(64) DEFAULT NULL COMMENT '下单人',
  `buyer_phone` varchar(64) DEFAULT NULL COMMENT '下单人电话',
  `nick_name` varchar(256) DEFAULT NULL COMMENT '买家昵称',
  `logistics_provider_code` varchar(64) DEFAULT NULL COMMENT '快递商编码：HTKY：汇通 STO：圆通 YUNDA：韵达 SF：顺丰 EMS：EMS SF-COD：顺丰COD YTO-COD：圆通COD 等',
  `tracking_number` varchar(64) DEFAULT NULL COMMENT '面单号',
  `provider_code` varchar(32) NOT NULL COMMENT '供应商',
  `tms_company` varchar(256) DEFAULT NULL COMMENT '运输公司名称',
  `tms_contact` varchar(32) DEFAULT NULL COMMENT '联系人',
  `tms_mobile` varchar(20) DEFAULT NULL COMMENT '电话',
  `tms_shipping_no` varchar(64) DEFAULT NULL COMMENT '运单号',
  `currency` varchar(5) DEFAULT 'RMB' COMMENT '币种',
  `created_user` varchar(64) DEFAULT NULL COMMENT '创建者',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `last_updated_user` varchar(64) DEFAULT NULL COMMENT '最后更新人',
  `last_updated_time` datetime DEFAULT NULL COMMENT '最后更新时间',
  PRIMARY KEY (`order_id`)
) ENGINE=InnoDB AUTO_INCREMENT=17955483 DEFAULT CHARSET=utf8 COMMENT=' 订单表';

-- ----------------------------
-- Records of order_info
-- ----------------------------
INSERT INTO `order_info` VALUES ('17955475', '1602933187029', 'WMS_ACCEPT', '65553', '0', '0', '17955475', '1602933187029', '17955475', '2016-02-01 00:00:00', null, 'N', null, null, null, null, null, null, null, null, null, '李先生', '201512', '021-57261881', '15700163224', null, '上海市', null, '上海市', null, '普陀区', '福山路', 'zhu@email.com', 'male', null, null, null, 'N', null, 'N', null, '', 'TAOBAO', null, null, '金佰利', '金佰利官方旗舰店', '1111111', '李先生', '021-57261881', '淘宝小猪猪', null, null, '', null, null, null, null, 'RMB', 'zjli', '2016-02-02 14:00:00', 'zjli', '2016-02-02 14:00:00');
INSERT INTO `order_info` VALUES ('17955476', '1602933187043-c', 'WMS_ACCEPT', '65553', '0', '1', 'PURCHASE', '1602933187043-c', '', '2016-02-02 19:36:00', null, 'N', null, null, null, null, null, null, null, null, '科科', '张三', '201512', '021-57261881', '13736683426', '110', '浙江省', null, '杭州市', null, '滨江区', '聚财路', 'hzhang1@leqee.com', 'male', null, null, null, 'N', null, 'N', null, '', 'TAOBAO', null, null, '雀巢', '雀巢食品旗舰店', null, '张欢', '13736683426', 'novalist', null, null, '110', null, null, null, null, 'RMB', 'hzhang1', '2016-02-03 19:33:21', 'hzhang1', '2016-02-03 19:33:28');
INSERT INTO `order_info` VALUES ('17955477', '1602933187029-t', 'WMS_ACCEPT', '65553', '0', '0', 'RETURN', '1602933187029-t', 'RMA_RETURN', '2016-02-01 00:00:00', null, 'N', null, null, null, null, null, null, null, null, null, '李先生', '201512', '021-57261881', '15700163224', null, '上海市', null, '上海市', null, '普陀区', '福山路', 'zhu@email.com', 'male', null, null, null, 'N', null, 'N', null, '', 'TAOBAO', '17955475', null, '金佰利', '金佰利官方旗舰店', '1111111', '李先生', '021-57261881', '淘宝小猪猪', null, null, '', null, null, null, null, 'RMB', 'zjli', '2016-02-02 14:00:00', 'zjli', '2016-02-02 14:00:00');
INSERT INTO `order_info` VALUES ('17955478', '1602933187030-t', 'WMS_ACCEPT', '65553', '0', '0', 'RETURN', '1602933187030-t', 'RMA_RETURN', '2016-02-01 00:00:00', null, 'N', null, null, null, null, null, null, null, null, null, '李先生', '201512', '021-57261881', '15700163224', null, '上海市', null, '上海市', null, '普陀区', '福山路', 'zhu@email.com', 'male', null, null, null, 'N', null, 'N', null, '', 'TAOBAO', null, null, '金佰利', '金佰利官方旗舰店', '1111111', '李先生', '021-57261881', '淘宝小猪猪', null, null, '', null, null, null, null, 'RMB', 'zjli', '2016-02-02 14:00:00', 'zjli', '2016-02-02 14:00:00');
INSERT INTO `order_info` VALUES ('17955479', '1602933187032', 'WMS_ACCEPT', '65553', '0', '0', 'SALE', '1602933187032', 'SALE', '2016-02-01 00:00:00', null, 'N', null, null, null, null, null, null, null, null, null, '李先生', '201512', '021-57261881', '15700163224', null, '上海市', null, '上海市', null, '普陀区', '福山路', 'zhu@email.com', 'male', null, null, null, 'N', null, 'N', null, '', 'TAOBAO', null, null, '金佰利', '金佰利官方旗舰店', '1111111', 'yao先生', '021-57261881', '淘宝小猪猪', null, null, '', null, null, null, null, 'RMB', 'qyyao', '2016-02-02 14:00:00', 'qyyao', '2016-02-02 14:00:00');
INSERT INTO `order_info` VALUES ('17955480', '1602933187033', 'WMS_ACCEPT', '65558', '0', '12306', 'SUPPLIER_RETURN', '1602933187033', 'SUPPLIER_RETURN', '2016-02-15 11:52:45', null, 'N', null, null, null, null, null, null, null, null, null, '张三', '201512', '021-57261881', '15700163224', null, '上海市', null, '上海市', null, '普陀区', '福山路', 'zhu@email.com', 'male', null, null, null, 'N', null, 'N', null, '', 'ORTHER', null, null, '', null, null, null, null, null, null, null, '12345', null, null, null, null, 'RMB', 'xjye', '2016-02-15 11:54:45', 'xjye', '2016-02-15 11:54:49');
INSERT INTO `order_info` VALUES ('17955481', '1602933187053-c', 'WMS_ACCEPT', '65553', '0', '2', 'PURCHASE', '1602933187053-c', '', '2016-02-15 19:36:00', '0000-00-00 00:00:00', 'N', '0000-00-00 00:00:00', '0000-00-00 00:00:00', '0.000000', '0.000000', '0.000000', '0.000000', '', '', '狗带', '张三', '201512', '021-57261881', '13736683426', '110', '浙江省', null, '杭州市', null, '滨江区', '聚财路', 'hzhang1@leqee.com', 'male', '', '', '0.000000', 'N', '0.000000', 'N', null, '', 'TAOBAO', null, null, '雀巢', '雀巢食品旗舰店', '', '张欢', '13736683426', 'novalist', '', '', '110', '', '', '', '', 'RMB', 'hzhang1', '2016-02-03 19:33:21', 'hzhang1', '2016-02-03 19:33:28');
INSERT INTO `order_info` VALUES ('17955482', '1602933187088', 'WMS_ACCEPT', '61213', '0', '12306', 'SUPPLIER_RETURN', '1602933187037', 'SUPPLIER_RETURN', '2016-02-16 17:59:14', '2016-02-16 17:59:26', 'N', null, null, null, null, null, null, null, null, null, '张三', '325000', '021-57261881', '15700163228', '110', '上海市', null, '上海市', null, '普陀区', '福山路', null, '', null, null, null, 'N', null, 'N', null, '', null, null, null, '', null, null, null, null, null, null, null, '', null, null, null, null, 'RMB', 'xjye', '2016-02-16 18:00:27', 'xjye', '2016-02-16 18:00:32');

-- ----------------------------
-- Table structure for product
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
  `product_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `brand_name` varchar(64) DEFAULT NULL COMMENT '品牌名称',
  `barcode` varchar(64) DEFAULT NULL COMMENT '条码',
  `sku_code` varchar(64) NOT NULL COMMENT 'OMS商品编码，保证唯一',
  `product_name` varchar(256) NOT NULL COMMENT '商品中文名',
  `cat_name` varchar(32) NOT NULL COMMENT '类别 ',
  `customer_id` varchar(64) NOT NULL COMMENT '卖家编码，保证唯一',
  `volume` decimal(10,3) DEFAULT NULL COMMENT '商品体积(L)',
  `length` int(11) DEFAULT NULL COMMENT '长(cm)',
  `width` int(11) DEFAULT NULL COMMENT '宽(cm)',
  `height` int(11) DEFAULT NULL COMMENT '高(cm)',
  `weight` decimal(15,6) DEFAULT NULL COMMENT '商品重量(kg)',
  `unit_price` decimal(15,6) DEFAULT NULL COMMENT '商品单价',
  `validity` smallint(5) unsigned NOT NULL DEFAULT '0' COMMENT '保质期',
  `validity_unit` varchar(64) DEFAULT 'MONTH' COMMENT '时间单位 MONTH,DAY',
  `is_delete` char(1) NOT NULL DEFAULT 'N' COMMENT '是否删除',
  `is_maintain_weight` char(1) NOT NULL DEFAULT 'N' COMMENT '是否维护重量',
  `is_maintain_warranty` char(1) NOT NULL DEFAULT 'N' COMMENT '是否维护保质期',
  `is_maintain_batch_sn` char(1) NOT NULL DEFAULT 'N' COMMENT '是否维护批次号',
  `is_contraband` char(1) NOT NULL DEFAULT 'N' COMMENT '是否违禁品',
  `is_serial` char(1) NOT NULL DEFAULT 'N' COMMENT '是否串号控制',
  `created_user` varchar(64) DEFAULT NULL COMMENT '创建者',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `last_updated_user` varchar(64) DEFAULT NULL COMMENT '最后更新人',
  `last_updated_time` datetime DEFAULT NULL COMMENT '最后更新时间',
  PRIMARY KEY (`product_id`),
  KEY `sku_code` (`sku_code`),
  KEY `barcode` (`barcode`)
) ENGINE=InnoDB AUTO_INCREMENT=56 DEFAULT CHARSET=utf8 COMMENT='商品表';

-- ----------------------------
-- Records of product
-- ----------------------------
INSERT INTO `product` VALUES ('1', '雀巢', '6917878000466', '', '#雀巢淡奶油250ml × 1', '', '', null, null, null, null, null, null, '0', 'MONTH', 'N', 'N', 'N', 'N', 'N', 'N', null, null, null, null);
INSERT INTO `product` VALUES ('2', '雀巢', '6917878000400', '', '#雀巢牛奶250ml × 1', '', '', null, null, null, null, null, null, '0', 'MONTH', 'N', 'N', 'N', 'N', 'N', 'Y', 'zjli', '2016-02-03 00:00:00', 'zjli', '2016-02-03 00:00:00');
INSERT INTO `product` VALUES ('55', '电教', '6917878000488', '', '超级计算机', '', '', null, null, null, null, null, null, '0', 'MONTH', 'N', 'N', 'N', 'N', 'N', 'Y', 'xjye', '2016-02-16 18:03:18', 'xjye', '2016-02-16 18:03:22');

-- ----------------------------
-- Table structure for warehouse
-- ----------------------------
DROP TABLE IF EXISTS `warehouse`;
CREATE TABLE `warehouse` (
  `warehouse_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `warehouse_name` varchar(64) DEFAULT NULL,
  `warehouse_type` char(20) DEFAULT NULL,
  `phsical_warehouse_id` int(11) NOT NULL,
  `packbox_warehouse_id` int(11) NOT NULL,
  `province_id` int(11) DEFAULT NULL COMMENT '收件人所在省id',
  `province_name` varchar(64) DEFAULT NULL COMMENT '收件人所在省，如浙江省、北京',
  `city_id` int(11) DEFAULT NULL COMMENT '收件人所在市id',
  `city_name` varchar(64) DEFAULT NULL COMMENT '收件人所在市，如杭州市、上海市',
  `district_id` int(11) DEFAULT NULL COMMENT '收件人所在县id',
  `district_name` varchar(64) DEFAULT NULL COMMENT '收件人所在县（区），注意有些市下面是没有区的，如：义乌市',
  `address` varchar(256) DEFAULT NULL COMMENT '详细地址，不包含省市',
  `created_user` varchar(64) DEFAULT '' COMMENT '创建者',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`warehouse_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='仓库表';

-- ----------------------------
-- Records of warehouse
-- ----------------------------
INSERT INTO `warehouse` VALUES ('1', '电商服务上海仓', null, '0', '0', null, '上海市', null, '浦东区', null, '', 'A区', 'hzhang1', '2016-02-03 19:41:45');
INSERT INTO `warehouse` VALUES ('2', '电商服务北京仓', '', '0', '0', null, '上海市', null, '浦东区', null, '', 'A区', 'hzhang1', '2016-02-03 19:41:45');

-- ----------------------------
-- Table structure for warehouse_customer
-- ----------------------------
DROP TABLE IF EXISTS `warehouse_customer`;
CREATE TABLE `warehouse_customer` (
  `customer_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(64) DEFAULT NULL,
  `access_key` varchar(128) DEFAULT NULL,
  `address` varchar(128) DEFAULT NULL,
  `contact` char(10) DEFAULT NULL,
  `mobile` char(20) DEFAULT NULL,
  `phone` char(20) DEFAULT NULL,
  `email` varchar(64) DEFAULT NULL,
  `created_user` varchar(64) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='仓库货主表';

-- ----------------------------
-- Records of warehouse_customer
-- ----------------------------
