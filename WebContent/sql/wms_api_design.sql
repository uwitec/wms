OrderInfoRequest设计

#1  `order_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
#1  `order_sn` char(32) NOT NULL,
#1  `order_status` char(20) NOT NULL COMMENT '订单物流状态：WMS_ACCEPT -仓库接单,WMS_PRINT – 打印,WMS_PICK – 捡货,WMS_CHECK – 复核,WMS_PACKAGE – 打包,WMS_WEIGH - 称重,DELIVERED - 已发货,EXCEPTION - 异常,CLOSED - 关闭,CANCELED - 取消,TO_BE_RECOVERED - 待追回,RECOVERED - 已追回',
#1(客户端传过来appkey)  `customer_id` int(11) NOT NULL COMMENT '货主id',
#1  `order_type` varchar(64) DEFAULT NULL COMMENT '订单类型：SALE,RETURN,PURCHASE,SUPPLIER_RETURN,VARIANCE_ADD,VARIANCE_MINUS',
#1  `is_reserved` char(1) NOT NULL DEFAULT 'N' COMMENT '预定是否成功',
#1  `reserved_time` datetime DEFAULT NULL COMMENT '预定时间',
#1  `shipping_time` datetime DEFAULT NULL COMMENT '发货时间（yyyy-MM-dd HH:mm:ss）',
#1  `province_id` int(11) DEFAULT NULL COMMENT '收件人所在省id',
#1  `city_id` int(11) DEFAULT NULL COMMENT '收件人所在市id',
#1  `district_id` int(11) DEFAULT NULL COMMENT '收件人所在县id',
#1 `created_user` varchar(64) DEFAULT NULL COMMENT '创建者',
#1  created_time` datetime DEFAULT NULL COMMENT '创建时间',
#1   `last_updated_user` varchar(64) DEFAULT NULL COMMENT '最后更新人',
#1  `last_updated_time` datetime DEFAULT NULL COMMENT '最后更新时间',

//订单号及相关
  `oms_order_sn` varchar(64) NOT NULL COMMENT '客户ERP 交易生成订单号，卖家系统保证唯一',
  `oms_order_type` varchar(32) DEFAULT '' COMMENT 'SALE,RMA_EXCHANGE,RMA_RETURN,PURCHASE,SUPPLIER_SALE,SUPPLIER_RETURN,VARIANCE_ADD,VARIANCE_MINUS',
  `order_source` varchar(64) DEFAULT NULL COMMENT '订单来源（ 如： 360BUY, PAIPAI,DANGDANG, TAOBAO,OTHER）',

//订单时间相关
  `order_time` datetime DEFAULT NULL COMMENT '下单时间（yyyy-MM-dd HH:mm:ss）',
  `pay_time` datetime DEFAULT NULL COMMENT '付款时间（yyyy-MM-dd HH:mm:ss）',
 
//订单金额相关
  `goods_amount` decimal(15,6) DEFAULT NULL COMMENT '商品总金额，可用于买保险',
  `shipping_amount` decimal(15,6) DEFAULT NULL COMMENT '运费',
  `discount_amount` decimal(15,6) DEFAULT NULL COMMENT '折扣费',
  `pay_amount` decimal(15,6) DEFAULT NULL COMMENT '实际支付费',
  
//备注  
  `buyer_note` varchar(512) DEFAULT NULL COMMENT '买家备注',
  `seller_note` varchar(512) DEFAULT NULL COMMENT '卖家备注',
  `note` varchar(512) DEFAULT NULL COMMENT '订单备注',

//收货人及联系方式
  `receive_name` varchar(64) NOT NULL COMMENT '收件人姓名',  #销售订单需要校验
  `sex` char(10) NOT NULL DEFAULT '' COMMENT '性别  male , female  ',
  `postal_code` varchar(64) DEFAULT NULL COMMENT '收件人邮编',
  `phone_number` varchar(64) DEFAULT NULL COMMENT '收件人电话，包括区号、电话号码及分机号，中间用“-”分隔；',
  `mobile_number` varchar(64) DEFAULT NULL COMMENT '收件人移动电话',
  `email` varchar(128) DEFAULT NULL COMMENT '邮件',
  
//收货地址
  `province_name` varchar(64) DEFAULT NULL COMMENT '收件人所在省，如浙江省、北京',
  `city_name` varchar(64) DEFAULT NULL COMMENT '收件人所在市，如杭州市、上海市',
  `district_name` varchar(64) DEFAULT NULL COMMENT '收件人所在县（区），注意有些市下面是没有区的，如：义乌市',
  `shipping_address` varchar(256) DEFAULT NULL COMMENT '收件人详细地址，不包含省市',
  
//发票
  `invoice_title` varchar(512) DEFAULT NULL COMMENT '发票抬头',
  `invoice_note` varchar(512) DEFAULT NULL COMMENT '发票内容',
  `invoice_amount` decimal(15,6) DEFAULT NULL COMMENT '发票金额',
  
//保价和贷款
  `is_value_declared` char(1) NOT NULL DEFAULT 'N' COMMENT '是否保价',
  `declaring_value_amount` decimal(15,6) DEFAULT NULL COMMENT '保价金额',
  `is_payment_collected` char(1) NOT NULL DEFAULT 'N' COMMENT '是否代收货款',
  `collecting_payment_amount` decimal(10,2) DEFAULT NULL COMMENT '代收货款金额',

//退货订单相关
  `oms_parent_order_sn` int(10) unsigned DEFAULT NULL COMMENT '父订单id',  #退货订单必须要有
  `oms_root_order_sn` int(10) unsigned DEFAULT NULL COMMENT '根订单id',    #退货订单必须要有

//店铺和品牌相关
  `brand_name` varchar(32) NOT NULL COMMENT '品牌',
  `shop_name` varchar(64) DEFAULT NULL COMMENT '店铺名称',
  `shop_order_sn` varchar(64) DEFAULT NULL COMMENT '卖家销售订单编号' ,   #是外部订单号(淘宝订单号)

//买家基本信息
  `buyer_name` varchar(64) DEFAULT NULL COMMENT '下单人',
  `buyer_phone` varchar(64) DEFAULT NULL COMMENT '下单人电话',
  `nick_name` varchar(256) DEFAULT NULL COMMENT '买家昵称',

//-gt和采购需要(必须要有的)
  `provider_code` varchar(32) NOT NULL COMMENT '供应商',    #-gt订单必须要有的吗
  `provider_name` varchar(32) NOT NULL COMMENT '供应名称',    #-gt订单必须要有的吗
  `provider_order_type` varchar(32) NOT NULL COMMENT '供应商',    #-gt订单必须要有的吗
  
//tms相关
  `tms_company` varchar(256) DEFAULT NULL COMMENT '运输公司名称',
  `tms_contact` varchar(32) DEFAULT NULL COMMENT '联系人',
  `tms_mobile` varchar(20) DEFAULT NULL COMMENT '电话',
  `tms_shipping_no` varchar(64) DEFAULT NULL COMMENT '运单号',

//仓库相关
  `physical_warehouse_id` int(11) NOT NULL COMMENT '物理仓储id',
  `warehouse_id` int(11) NOT NULL COMMENT '仓库编号',
  
//快递相关
  `shipping_code` varchar(64) DEFAULT NULL COMMENT '快递商编码：HTKY：汇通 STO：圆通 YUNDA：韵达 SF：顺丰 EMS：EMS SF-COD：顺丰COD YTO-COD：圆通COD 等',
//其他
  `currency` varchar(5) DEFAULT 'RMB' COMMENT '币种',

  PRIMARY KEY (`order_id`)
) ENGINE=InnoDB AUTO_INCREMENT=17955483 DEFAULT CHARSET=utf8 COMMENT=' 订单表';