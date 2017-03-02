/**
 * 波次相关
 * */
package com.leqee.wms.biz.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.leqee.wms.api.util.DateUtils;
import com.leqee.wms.api.util.Tools;
import com.leqee.wms.biz.BatchPickBiz;
import com.leqee.wms.dao.BatchPickDao;
import com.leqee.wms.dao.BatchPickTaskDao;
import com.leqee.wms.dao.ConfigPrintDispatchBillDao;
import com.leqee.wms.dao.LocationDao;
import com.leqee.wms.dao.OrderInfoDao;
import com.leqee.wms.dao.OrderProcessDao;
import com.leqee.wms.dao.ProductLocationDao;
import com.leqee.wms.dao.RegionDao;
import com.leqee.wms.dao.ShippingDao;
import com.leqee.wms.dao.SysUserDao;
import com.leqee.wms.dao.TaskDao;
import com.leqee.wms.dao.UserActionBatchPickDao;
import com.leqee.wms.dao.UserActionOrderDao;
import com.leqee.wms.dao.UserActionWarehouseLoadDao;
import com.leqee.wms.dao.WarehouseDao;
import com.leqee.wms.dao.WarehouseLoadDao;
import com.leqee.wms.entity.BatchPick;
import com.leqee.wms.entity.BatchPickTask;
import com.leqee.wms.entity.OrderGoods;
import com.leqee.wms.entity.OrderInfo;
import com.leqee.wms.entity.OrderProcess;
import com.leqee.wms.entity.ProductLocation;
import com.leqee.wms.entity.SameDetails;
import com.leqee.wms.entity.ScheduleJob;
import com.leqee.wms.entity.UserActionBatchPick;
import com.leqee.wms.entity.UserActionOrder;
import com.leqee.wms.entity.Warehouse;
import com.leqee.wms.response.Response;
import com.leqee.wms.response.ResponseFactory;
import com.leqee.wms.util.SequenceUtil;
import com.leqee.wms.util.WorkerUtil;

@Service
public class BatchPickBizImpl implements BatchPickBiz {
	private Logger logger = Logger.getLogger(BatchPickBizImpl.class);

	@Autowired
	SysUserDao sysUserDao;

	@Autowired
	RegionDao regionDao;

	@Autowired
	ShippingDao shippingDao;

	@Autowired
	WarehouseLoadDao warehouseLoadDao;
	
	@Autowired
	WarehouseDao warehouseDao;

	@Autowired
	ConfigPrintDispatchBillDao configPrintDispatchBillDao;

	@Autowired
	UserActionWarehouseLoadDao userActionWarehouseLoadDao;

	@Autowired
	BatchPickTaskDao batchPickTaskDao;
	@Autowired
	OrderProcessDao orderProcessDao;

	@Autowired
	OrderInfoDao orderInfoDao;
	@Autowired
	BatchPickDao batchPickDao;
	@Autowired
	UserActionOrderDao userActionOrderDao;
	@Autowired
	UserActionBatchPickDao userActionBatchPickDao;
	@Autowired
	LocationDao locationDao;
	@Autowired
	ProductLocationDao productLocationDao;
	@Autowired
	TaskDao taskDao;
	/**
	 * @author Jarvis
	 * @CreatedDate 2016.02.22
	 * 
	 * @Description 绑定波次单
	 * 
	 * @param customerId
	 *            an Integer: 货主ID
	 * @param warehouseId
	 *            an Integer: 仓库ID
	 * @return response a Response: 返回消息
	 * 
	 * */
	@Override
	public Response batchPick(Integer customerId, Integer warehouseId,
			String actionUser) {
		// 初始化
		Response response = ResponseFactory.createOkResponse("成功创建波次单！");
		List<Integer> orderIdList = new ArrayList<Integer>();

		// 查询符合波次条件的销售订单
		HashMap<String, Object> paramsForOrderProcessSelect = new HashMap<String, Object>();
		paramsForOrderProcessSelect.put("orderType", "SALE");
		paramsForOrderProcessSelect.put("customerId", customerId.toString());
		paramsForOrderProcessSelect.put("warehouseId", warehouseId.toString());
		paramsForOrderProcessSelect.put("status", "WMS_ACCEPT");
		paramsForOrderProcessSelect.put("reserveStatus", "Y");
		List<OrderProcess> orderProcessList = orderProcessDao
				.selectOrderProcessListForBatchPick(paramsForOrderProcessSelect);

		List<OrderGoods> orderGoodsList = orderProcessDao
				.selectOrderGoodsListForBatchPick(paramsForOrderProcessSelect);
		Map<String, String> ogMap = new HashMap<String, String>();

		// 将所有的订单的商品id和数目与订单编号进行绑定(订单号=id：商品号1|num:商品号1数目;id：商品号2|num:商品号2数目...)
		for (OrderGoods og : orderGoodsList) {
			String orderid = og.getOrder_id().toString();
			StringBuffer productIdAndNum = new StringBuffer().append("id:")
					.append(og.getProduct_id()).append("|num:")
					.append(og.getGoods_number()).append(';');
			if (ogMap.containsKey(orderid)) {
				ogMap.put(orderid, ogMap.get(orderid).toString()
						+ productIdAndNum.toString());
			} else {
				ogMap.put(orderid, productIdAndNum.toString());
			}
		}

		// 对所有的订单进行匹配，找出完全一样的订单信息

		// (id：商品号1|num:商品号1数目;id：商品号2|num:商品号2数目...=订单号1,订单号2...)
		Map<String, String> ogMapReverse = new HashMap<String, String>();

		// (订单号1,订单号2...=id：商品号1|num:商品号1数目;id：商品号2|num:商品号2数目...)
		Map<String, String> ogMap2 = new HashMap<String, String>();
		// 利用KeySet 迭代
		Iterator it = ogMap.keySet().iterator();
		while (it.hasNext()) {
			String key;
			String value;
			key = it.next().toString();
			value = (String) ogMap.get(key);
			if (!ogMapReverse.containsKey(value)) {
				ogMapReverse.put(value, key);

				ogMap2.put(key, value);
			} else {
				ogMapReverse.put(value, ogMapReverse.get(value).toString()
						+ "," + key);// 每个订单信息：订单号

				ogMap2.remove(key);
				ogMap2.put(ogMapReverse.get(value).toString() + "," + key,
						value);// 订单号：每个订单信息
			}
		}

		// 存储具有多个相同订单信息的订单号
		List<String> orderIdsList = new ArrayList<String>();

		// 筛选出多个订单一模一样的情况，将只有一个的信息去除掉
		Iterator it2 = ogMap2.keySet().iterator();
		while (it2.hasNext()) {
			String key;
			String value;
			key = it2.next().toString();
			value = (String) ogMap2.get(key);
			if (key.contains(",")) {
				orderIdsList.add(key);
			}
		}

		// 针对每一个波次
		if (!orderIdsList.isEmpty()) {

			for (String stringKey : orderIdsList) {
				orderIdList.clear();
				String[] orderIds = stringKey.split(",");

				for (String id : orderIds) {
					if (!id.isEmpty()) {
						orderIdList.add(Integer.valueOf(id));
					}
				}

			}

		} else {
			logger.error("波次失败：获取订单数据异常！");
			throw new RuntimeException("波次失败：获取订单数据异常！");
		}

		// //add by yao
		// for(OrderProcess op:orderProcessList)
		// {
		// int order_id=op.getOrder_id();
		// List<Integer>
		// list=orderInfoDao.selectOrderIdWithSameOrderGoods(order_id);
		// if(list.size()>1)
		// {
		// //进行波次
		// }
		//
		// }

		if (!orderProcessList.isEmpty()) {
			for (OrderProcess orderProcess : orderProcessList) {
				logger.info("orderId: " + orderProcess.getOrder_id().toString());
				orderIdList.add(orderProcess.getOrder_id());
			}

			// 获取并锁住订单
			List<OrderInfo> orderInfoList = orderInfoDao
					.selectOrderInfoListByIdForUpdate(orderIdList);
			if (orderInfoList.isEmpty()) {
				logger.error("波次失败：获取订单数据异常！");
				throw new RuntimeException("波次失败：获取订单数据异常！");
			}

			// 生成波次单
			Integer batchPickId = createBatchPick(actionUser);
			logger.info("生成的波次单ID: " + batchPickId.toString());

			int i = 1; // 计数
			for (OrderInfo orderInfo : orderInfoList) {
				// 检查订单状态，防止并发操作
				if (!"WMS_ACCEPT".equals(orderInfo.getOrder_status())) {
					logger.error("波次失败：订单(ID: " + orderInfo.getOrder_id()
							+ "，状态: " + orderInfo.getOrder_status()
							+ ")不是待波次状态，无法进行波次！");
					throw new RuntimeException("波次失败：订单(ID: "
							+ orderInfo.getOrder_id() + "，状态: "
							+ orderInfo.getOrder_status() + ")不是待波次状态，无法进行波次！");
				}

				// 更新OrderProcess波次单信息和状态
				HashMap<String, Object> paramsForOrderProcessUpdate = new HashMap<String, Object>();
				paramsForOrderProcessUpdate.put("orderId",
						orderInfo.getOrder_id());
				paramsForOrderProcessUpdate.put("batchPickId", batchPickId);
				paramsForOrderProcessUpdate.put("orderBatchGroup", ""); // To
																		// Confirm
				paramsForOrderProcessUpdate.put("orderBatchSequenceNumber", i);
				paramsForOrderProcessUpdate.put("status", "WMS_PICK");
				orderProcessDao
						.updateBatchPickInfoByOrderId(paramsForOrderProcessUpdate);

				// 更新OrderInfo状态
				HashMap<String, Object> paramsForOrderInfoUpdate = new HashMap<String, Object>();
				paramsForOrderInfoUpdate.put("orderStatus", "WMS_PICK");
				paramsForOrderInfoUpdate.put("lastUpdatedUser", actionUser);
				paramsForOrderInfoUpdate.put("lastUpdatedTime", new Date());
				orderInfoDao
						.updateOrderStatusByOrderId(paramsForOrderInfoUpdate);

				// 插入订单操作日志记录
				UserActionOrder userActionOrder = new UserActionOrder();
				userActionOrder.setOrder_id(orderInfo.getOrder_id());
				userActionOrder.setOrder_status("WMS_PICK");
				userActionOrder.setAction_type("BATCH_PICK");
				userActionOrder.setAction_note("订单成功绑定波次单!");
				userActionOrder.setCreated_user(actionUser);
				userActionOrder.setCreated_time(new Date());
				userActionOrderDao.insert(userActionOrder);

				i++;
			}

		} else {
			response.setMsg("未找到需要创建波次单的订单!");
		}

		return response;
	}

	/**
	 * @author Jarvis
	 * @CreatedDate 2016.02.23
	 * 
	 * @Description 生成波次单
	 * 
	 * @param actionUser
	 *            a String: 操作人
	 * @param batchPickId
	 *            an int: 波次单ID
	 * 
	 * */
	private Integer createBatchPick(String actionUser) {
		// 初始化
		Date nowTime = new Date();

		// 开始生成波次单号
		String batchPickSn = generateBatchPickSn();
		logger.info("生成的波次单号: " + batchPickSn);

		BatchPick batchPick = new BatchPick();
		batchPick.setBatch_pick_sn(batchPickSn);
		batchPick.setCreated_user(actionUser);
		batchPick.setBatch_process_type(""); // To Confirm
		batchPick.setCreated_time(nowTime);
		batchPick.setStatus("INIT"); // To Confirm
		batchPickDao.insert(batchPick);

		Integer batchPickId = batchPick.getBatch_pick_id();
		if (batchPickId <= 0) {
			logger.error("生成波次单失败");
			throw new RuntimeException("生成波次单失败！");
		}

		UserActionBatchPick userActionBatchPick = new UserActionBatchPick();
		userActionBatchPick.setBatch_pick_id(batchPickId);
		userActionBatchPick.setStatus("INIT"); // To Confirm
		userActionBatchPick.setAction_type("CREATE"); // To Confirm
		userActionBatchPick.setAction_note("生成波次单: " + batchPickSn);
		userActionBatchPick.setCreated_user(actionUser);
		userActionBatchPick.setCreated_time(nowTime);
		userActionBatchPickDao.insert(userActionBatchPick);

		return batchPickId;
	}

	/**
	 * @author Jarvis
	 * @CreatedDate 2016.02.24
	 * 
	 * @Description 生成波次单编号
	 * 
	 * @return batchPickSn a String: 波次单编号
	 * 
	 * */
	private String generateBatchPickSn() {
		String result = "";
		// 每天从1开始编号,前面为6位的年月日
		String curDateStartBatchPickSn = (new SimpleDateFormat("yyMMdd"))
				.format(WorkerUtil.getNow()) + "-0001";

		// 查询数据库，获取最大的波次单号
		String maxBatchPickSn = batchPickDao
				.selectMaxBatchPickSn(curDateStartBatchPickSn);

		if (!WorkerUtil.isNullOrEmpty(maxBatchPickSn)) {
			// 后四位最大的序号加1
			Integer nextSn = Integer.valueOf(maxBatchPickSn.substring(7)) + 1;
			result = maxBatchPickSn.substring(0, 7)
					+ String.format("%04d", nextSn);
		} else {
			result = curDateStartBatchPickSn;
		}

		return result;
	}

	@Override
	public void updatebatchPickForBindEmployee(Integer bind_user_id,
			Integer batch_pick_id) {
		batchPickDao
				.updatebatchPickForBindEmployee(bind_user_id, batch_pick_id);
	}

	public void updateOrderInfoStatus(String batch_pick_sn) {
		batchPickDao.updateOrderInfoStatus(batch_pick_sn);
	}

	@Override
	public BatchPick selectBatchPickByBatchPickSn(String batch_pick_sn) {

		return batchPickDao.selectBatchPickByBatchPickSn(batch_pick_sn);
	}

	@Override
	public HashMap<String, Object> bindBatchPickforUser(int bind_user_id,
			String username, String batch_pick_sn) {

		HashMap<String, Object> resultMap = new HashMap<String, Object>();

		BatchPick batchPick = selectBatchPickByBatchPickSn(batch_pick_sn);

		if (WorkerUtil.isNullOrEmpty(batchPick)) {

			resultMap.put("error", "波次单号不存在或未打印！" + batch_pick_sn);
			resultMap.put("error_id", 2);
			resultMap.put("success", false);

			logger.info("BatchPickBizImpl bindBatchPickforUser batch_pick_sn is not existing");
			return resultMap;
		}

		if ((!WorkerUtil.isNullOrEmpty(batchPick.getBind_user_id()))||batchPick.getStatus().equalsIgnoreCase("BINDED")) {

			resultMap.put("error", "波次单号已经绑定！");
			resultMap.put("error_id", 3);
			resultMap.put("success", false);

			logger.info("BatchPickBizImpl bindBatchPickforUser batch_pick_sn is already  binding by another");
			return resultMap;
		}

		logger.info("BatchPickController bindUser UserId" + bind_user_id
				+ " Batch_pick_id" + batchPick.getBatch_pick_id());

		updatebatchPickForBindEmployee(bind_user_id,
				batchPick.getBatch_pick_id());

//		updateOrderInfoStatus(batchPick.getBatch_pick_sn());
//		updateOrderProcessStatus(batchPick.getBatch_pick_sn());

		List<OrderInfo> orders = new ArrayList<OrderInfo>();
		orders = batchPickDao.getOrderIdByBatchPickSn(batch_pick_sn);
		String actionUser = (String) SecurityUtils.getSubject().getPrincipal();
		for (OrderInfo oi : orders) {
			UserActionOrder userActionOrder = new UserActionOrder();
			userActionOrder.setOrder_id(oi.getOrder_id());
			userActionOrder.setOrder_status(oi.getOrder_status());
			userActionOrder.setAction_type("BINDED");
			userActionOrder.setAction_note("订单所属波次单成功分配给：" + username);
			userActionOrder.setCreated_user(actionUser);
			userActionOrder.setCreated_time(new Date());
			userActionOrderDao.insert(userActionOrder);
		}

		// 记录生成日志
		UserActionBatchPick userActionBatchPick = new UserActionBatchPick();
		userActionBatchPick.setAction_note("波次单:"
				+ batchPick.getBatch_pick_id());
		userActionBatchPick.setAction_type("BINDED");
		userActionBatchPick.setCreated_time(new Date());
		userActionBatchPick.setCreated_user(actionUser);
		userActionBatchPick.setStatus("BINDED");
		userActionBatchPick.setBatch_pick_id(batchPick.getBatch_pick_id());

		userActionBatchPickDao.insert(userActionBatchPick);

		resultMap.put("success", true);

		return resultMap;
	}

	private void updateOrderProcessStatus(String batch_pick_sn) {
		batchPickDao.updateOrderProcessStatus(batch_pick_sn);
	}

	/**
	 * 获得具有相同规律的规律信息集
	 * 
	 * @param parmMap
	 *            参数集
	 * @return
	 */
	public List<SameDetails> batchPickAllSame(Map<String, Object> parmMap) {

		return orderInfoDao.selectOrderInfoListForBatchPick(parmMap);

	}

	/**
	 * 根据指定规律集 获取对应的订单号
	 * 
	 * @param parmMap
	 *            参数集
	 * @return
	 */
	public List<Integer> batchPickAllOrderListByParm(Map<String, Object> parmMap) {
		return orderInfoDao.batchPickAllOrderListByParm(parmMap);
	}

	/**
	 * 获取杂单
	 * @param parmMap
	 * @return
	 */
	public List<Integer> batchPickAllLastOrderListByParm(
			Map<String, Object> parmMap) {
		return orderInfoDao.batchPickAllLastOrderListByParm(parmMap);
	}
	/***
	 * 查出具备相同规则集的商品集合
	 * 
	 * @author dlyao
	 * @param req
	 * @param model
	 * @return
	 */
	public HashMap<String, Object> doSearchBatchPick2(HttpServletRequest req,
			HashMap<String, Object> model) {
		// 归并的可以波次的记录
		List<SameDetails> sdList1 = new ArrayList<SameDetails>(); // 完全相同
		List<SameDetails> sdList2 = new ArrayList<SameDetails>(); // 订单商品相同，数目不同
		List<SameDetails> sdList3 = new ArrayList<SameDetails>(); // 散单

		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();
		Warehouse warehouse = (Warehouse) session
				.getAttribute("currentPhysicalWarehouse");
		// 筛选条件1：货主
		int customer_id = Integer.parseInt(req.getParameter("customer_id"));

		// 筛选条件2：指定订单查询
		String specialOrderId = req.getParameter("specialOrderId");

		// 筛选条件3_1：物理仓库
		String physical_warehouse_id = warehouse.getPhysical_warehouse_id()
				+ "";

		// 筛选条件3_2：逻辑仓库
		String warehouse_id = req.getParameter("warehouse_id");
		if (warehouse_id.equals("all")) {
			warehouse_id = "";
		}

		// 筛选条件4：快递方式
		// String shipping_id = req.getParameter("shipping_id");

		// 筛选条件5：开始和结束时间

		String start = req.getParameter("start");
		String end = req.getParameter("end");
		if (start.equals("") || start.isEmpty()) {
			java.util.Date now = new java.util.Date();
			Calendar c = Calendar.getInstance();
			c.setTime(now);
			c.set(Calendar.DATE, c.get(Calendar.DATE) - 14); // 最近15天，即从之前的14天开始
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");// 可以方便地修改日期

			start = dateFormat.format(c.getTime());
		}
		start += " 00:00:00";

		if (end.equals("") || end.isEmpty()) {
			java.util.Date now = new java.util.Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");// 可以方便地修改日期
			end = dateFormat.format(now);
		}
		end += " 23:59:59";

		// 筛选条件6：收货地址
		String region_id = req.getParameter("region_id");
		// String region_name = req.getParameter("region_name");
		// if (region_name.equals("不限")) {
		// region_name = "";
		// }

		// 筛选条件7：店铺
		String shopnames = req.getParameter("shopnames");
		List<String> shop_name = new ArrayList<String>();
		if (!shopnames.isEmpty()) {
			shop_name = Tools.changeStringToList(shopnames, 1);
		}

		// //System.out.print(warehouse_id+":"+shipping_id+":"+start+":"+end+":"+region_id+":"+checkValue);

		Map<String, Object> parmMap = new HashMap<String, Object>();

		parmMap.put("customer_id", customer_id);
		parmMap.put("specialOrderId", specialOrderId);
		parmMap.put("physical_warehouse_id", physical_warehouse_id);
		parmMap.put("warehouse_id", warehouse_id);
		// parmMap.put("shipping_id", shipping_id);
		parmMap.put("start", start);
		parmMap.put("end", end);
		// parmMap.put("region_name", region_name);
		parmMap.put("region_id", region_id);
		parmMap.put("shop_name", shop_name);

		//System.out.print(parmMap.toString());
		List<SameDetails> sdList0 = batchPickAllSame(parmMap);

		SameDetails sd, sd2;
		for (int i = 0; i < sdList0.size(); i++) {
			sd = sdList0.get(i);

			// 如果此条件有>1个完全相同的订单
			if (sd.getSku_sum() > 1) {
				sdList1.add(sd);

			}
		}

		for (int i = 0; i < sdList0.size() - 1; i++) {
			sd = sdList0.get(i);

			if (sd.getSku_sum() == 1) {
				for (int j = i + 1; j < sdList0.size(); j++) {
					sd2 = sdList0.get(j);
					if (sd2.getProduct_key().equals(sd.getProduct_key())
							&& sd2.getSku_sum() == 1 && (!sd.isMark())) {

						sd.setSku_sum(sd.getSku_sum() + 1);// 修改归并后的订单数目
						sd2.setSku_sum(0); // 修改归并后的订单数目,并作为标记不再被归并如散单
						sd.setOrder_ids(sd.getOrder_ids() + ","
								+ sd2.getOrder_ids()); // 拼接好被归并的订单号
						sd.setProduct_num(calculateProductNum(sd, sd2));
						sd.setMark(true);
						sd2.setMark(true);

					}
				}
			}
		}

		for (int i = 0; i < sdList0.size(); i++) {
			sd = sdList0.get(i);

			// 如果此条件有>1个完全相同的订单
			if (sd.getSku_sum() > 1 && sd.isMark()) {
				sdList2.add(sd);
			} else if (sd.getSku_sum() == 1) {
				sdList3.add(sd);
			}
		}

		HashMap<String, Object> resMap = new HashMap<String, Object>();
		if (!(specialOrderId.isEmpty() || specialOrderId.equals(""))) {
			changeShowwords(sdList3);
			resMap.put("sdList1", sdList3);
			return resMap;
		}

		if (!sdList1.isEmpty()) {
			changeShowwords(sdList1);
		}
		resMap.put("sdList1", sdList1);
		if (!sdList2.isEmpty()) {
			changeShowwords2(sdList2);
		}
		resMap.put("sdList2", sdList2);
		if (!sdList3.isEmpty()) {
			changeShowwords(sdList3);
		}
		resMap.put("sdList3", sdList3);
		return resMap;
	}

	// 计算两个订单的商品数目和
	private String calculateProductNum(SameDetails sd, SameDetails sd2) {
		int[] sdnum = changeProductNum(sd);
		int[] sdnum2 = changeProductNum(sd2);
		StringBuffer sb = new StringBuffer();
		if (sdnum.length == sdnum2.length) {
			for (int i = 0; i < sdnum.length; i++) {
				sdnum[i] = sdnum[i] + sdnum2[i];
				sb.append(sdnum[i]).append(',');
			}
			return sb.toString().substring(0, sb.toString().length() - 1);
		} else {
			return "error num";
		}

	}

	// 将商品数量的String转换层int[]
	private int[] changeProductNum(SameDetails sd) {
		String[] sds = sd.getProduct_num().split(",");
		int[] s = new int[sds.length];
		for (int i = 0; i < sds.length; i++) {
			s[i] = Integer.parseInt(sds[i]);
		}
		return s;
	}

	private void changeShowwords2(List<SameDetails> sdList2) {
		SameDetails sd;
		for (int i = 0; i < sdList2.size(); i++) {
			sd = sdList2.get(i);
			String[] names = sd.getGoods_name().split("/");
			int[] nums = changeProductNum(sd);
			int sku = sd.getSku_sum();
			StringBuffer sb = new StringBuffer();
			for (int j = 0; j < names.length; j++) {
				sb.append(names[j]).append("  订单数:").append(sku)
						.append("  商品总数:").append(nums[j]).append("</br>");
			}

			sd.setShows_batchpick(sb.toString().substring(0,
					sb.toString().length() - 5));
		}
	}

	private void changeShowwords(List<SameDetails> sdList1) {
		SameDetails sd;
		for (int i = 0; i < sdList1.size(); i++) {
			sd = sdList1.get(i);
			String[] names = sd.getGoods_name().split("/");
			int[] nums = changeProductNum(sd);
			int sku = sd.getSku_sum();
			StringBuffer sb = new StringBuffer();
			for (int j = 0; j < names.length; j++) {
				sb.append(names[j]).append("  商品数:").append(nums[j])
						.append("  订单数:").append(sku).append("  商品总数:")
						.append(sku * nums[j]).append("</br>");
			}

			sd.setShows_batchpick(sb.toString().substring(0,
					sb.toString().length() - 5));
		}

	}

	/**
	 * @author dlyao 检查商店的发货单
	 * @param req
	 * @return
	 */
	public HashMap<String, Object> checkShopCollection(HttpServletRequest req) {

		HashMap<String, Object> resMap = new HashMap<String, Object>();
		// 筛选条件1：货主
		int customer_id = Integer.parseInt(req.getParameter("customer_id"));

		// 筛选条件7：店铺
		String shopnames = req.getParameter("shopnames");// "金佰利官方旗舰店,金佰利官方旗舰店2,";
		if (shopnames == null || shopnames.equals("")) {
			resMap.put("result", 0);
			return resMap;
		}
		List<String> shop_name = new ArrayList<String>();
		if (!shopnames.isEmpty()) {
			shop_name = Tools.changeStringToList(shopnames, 1);
		} else {
			resMap.put("result", 0);
			return resMap;
		}

		int rcount = configPrintDispatchBillDao.getCountRecord(shop_name);
		int pcount = configPrintDispatchBillDao
				.getCountPrintDispatchBill(shop_name);
		// 店铺都通用
		if (rcount == 0) {
			resMap.put("result", 0);
		}
		// 店铺都特殊但是都一样
		else if (rcount == shop_name.size() && pcount == 1) {
			resMap.put("result", 0);
		}
		// 部分店铺特殊，恰好与非特殊的一样
		else if (rcount != shop_name.size() && pcount == 1) {
			String file_name = configPrintDispatchBillDao
					.getFileName(shop_name);

			String file_name2 = configPrintDispatchBillDao
					.getCustomerFileName(customer_id);

			if (file_name.equals(file_name2)) {
				resMap.put("result", 0);

			} else {
				resMap.put("result", 1);
			}
		} else {
			resMap.put("result", 1);
		}

		return resMap;
	}

	/**
	 * @author dlyao 检查仓库负荷
	 * @param
	 * 
	 */
	public HashMap<String, Object> checkWarehouseLoad(HttpServletRequest req,
			HashMap<String, Object> model) {
		// 筛选条件1：物理仓库
		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();
		Warehouse warehouse = (Warehouse) session
				.getAttribute("currentPhysicalWarehouse");

		int physical_warehouse_id = warehouse.getPhysical_warehouse_id();

		java.util.Date now = new java.util.Date();
		Calendar c = Calendar.getInstance();
		c.setTime(now);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");// 可以方便地修改日期

		String time = dateFormat.format(c.getTime()) + " 00:00:00";

		Map<String, Object> parmMap = new HashMap<String, Object>();

		parmMap.put("physical_warehouse_id", physical_warehouse_id);
		parmMap.put("time", time);

		HashMap<String, Object> resMap = new HashMap<String, Object>();

		// 仓库每日最大订单量
		int max = warehouseLoadDao
				.selectWarehouseLoadByid(physical_warehouse_id);

		// 仓库今日已经多少订单在波次任务中
		int ordersInTask = 0;
			ordersInTask = getOrderNumInPhySicalWareHouseToday(physical_warehouse_id);
		if (ordersInTask < max * 0.8) {
			resMap.put("mark", true);
			resMap.put("mark2", true);
			resMap.put("inNum", max - ordersInTask);
		} else if (ordersInTask < max) {
			resMap.put("message", "订单总数即将达到发货上限" + max + "</br>【当前单量："
					+ ordersInTask + "】");
			resMap.put("mark", false);
			resMap.put("mark2", true);
			resMap.put("inNum", max - ordersInTask);
		} else {
			resMap.put("message", "仓库今日处理订单量已经达到最大值" + max);
			resMap.put("mark", false);
			resMap.put("mark2", false);
			resMap.put("inNum", 0);

		}

		resMap.put("max", max);
		resMap.put("ordersInTask", ordersInTask);

		return resMap;
	}

	/**
	 * 创建生成批件单任务
	 * 
	 * @author dlyao
	 */
	public HashMap<String, Object> createBatchPickTask(HttpServletRequest req,
			HashMap<String, Object> model) {

		String actionUser = (String) SecurityUtils.getSubject().getPrincipal();

		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();
		Warehouse warehouse = (Warehouse) session
				.getAttribute("currentPhysicalWarehouse");
		// 筛选条件1：货主
		int customer_id = Integer.parseInt(req.getParameter("customer_id"));

		// 筛选条件2：指定订单查询
		String specialOrderId = req.getParameter("specialOrderId");

		// 筛选条件3：仓库
		// 筛选条件3_1：物理仓库
		String physical_warehouse_id = warehouse.getPhysical_warehouse_id()
				+ "";

		// 筛选条件3_2：逻辑仓库
		String warehouse_id = req.getParameter("warehouse_id");
		if (warehouse_id.equals("all")) {
			warehouse_id = "";
		}

		// 筛选条件4：快递方式
		// String shipping_id = req.getParameter("shipping_id");

		// 筛选条件5：开始和结束时间
		String start = req.getParameter("start");
		String end = req.getParameter("end");
		if (start.equals("") || start.isEmpty()) {
			java.util.Date now = new java.util.Date();
			Calendar c = Calendar.getInstance();
			c.setTime(now);
			c.set(Calendar.DATE, c.get(Calendar.DATE) - 14); // 最近15天，即从之前的14天开始
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");// 可以方便地修改日期

			start = dateFormat.format(c.getTime());
		}
		start += " 00:00:00";

		if (end.equals("") || end.isEmpty()) {
			java.util.Date now = new java.util.Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");// 可以方便地修改日期
			end = dateFormat.format(now);
		}
		end += " 23:59:59";

		// 筛选条件6：收货地址
		String region_id = req.getParameter("region_id");
		// String region_name = req.getParameter("region_name");
		//
		// if (region_name.equals("不限")) {
		// region_name = "";
		// }

		// 筛选条件7：店铺
		String shopnames = req.getParameter("shopnames");
		List<String> shop_name = new ArrayList<String>();
		if (!shopnames.isEmpty()) {
			shop_name = Tools.changeStringToList(shopnames, 1);
		}

		// 需要波次的订单数
		int order_num = Integer.parseInt(req.getParameter("order_num"));

		// 分成的批件单数
		int batch_num = Integer.parseInt(req.getParameter("batch_num"));

		// 商品种类信息
		String product_key = req.getParameter("product_key");

		// 每种商品的件数
		String product_num = req.getParameter("product_num");

		String type = req.getParameter("type");

		Map<String, Object> parmMap = new HashMap<String, Object>();
		HashMap<String, Object> resMap = new HashMap<String, Object>();

		parmMap.put("customer_id", customer_id);
		parmMap.put("specialOrderId", specialOrderId);
		parmMap.put("physical_warehouse_id", physical_warehouse_id);
		parmMap.put("warehouse_id", warehouse_id);
		// parmMap.put("shipping_id", shipping_id);
		parmMap.put("start", start);
		parmMap.put("end", end);
		// parmMap.put("region_name", region_name);
		parmMap.put("region_id", region_id);
		parmMap.put("shop_name", shop_name);
		parmMap.put("order_num", order_num);
		parmMap.put("batch_num", batch_num);
		parmMap.put("product_key", product_key);
		parmMap.put("product_num", product_num);
		parmMap.put("type", type);// type为"list2"表示商品相同数量不同，则查询时不需要考虑product_num

		// 所有的任务列表
		List<Integer> orderIds = batchPickAllOrderListByParm(parmMap);

		// 波次锁表@dlyao start
		List<OrderProcess> orderProcessList = orderProcessDao
				.selectOrdersForLock2Task(orderIds);

		Assert.isTrue(orderProcessList != null || !orderProcessList.isEmpty()
				|| orderProcessList.size() > 0, "没找到足够的订单");

		for (OrderProcess op : orderProcessList) {
			Assert.isTrue(0 == op.getIn_batchpick_task(),
					"订单：" + op.getOrder_id() + "已经在波次任务中");
		}
		// 波次锁表@dlyao end

		// 此集合中订单数量小于波次的订单总数
		if (orderIds.size() < order_num) {
			resMap.put("mark", false);
			resMap.put("errorMessage", "没有足够的订单，目前尚有：" + orderIds.size()
					+ "个订单未处理");
			return resMap;
		}

		// 批件单的大小
		int size;
		int mod = order_num % batch_num;

		size = order_num / batch_num;
		int allIndex = 0;

		List<BatchPickTask> batchPickTaskList = new ArrayList<BatchPickTask>();

		for (int i = 0; i < batch_num; i++) {
			BatchPickTask batchPickTask = new BatchPickTask();
			int rsize = size;
			if (i < mod) {
				rsize += 1;
			}
			batchPickTask.setBatchpick_num(rsize); // 批件单中订单数目
			batchPickTask.setStatus("BEFORE"); // 批件单的状态 "BEFORE"生成前 "DO"已经生成
			batchPickTask.setCreated_user(actionUser);
			batchPickTask.setPhysical_warehouse_id(physical_warehouse_id); // 物理仓库号
			List<Integer> list0 = new ArrayList<Integer>();

			if (order_num > allIndex + rsize) {
				list0 = orderIds.subList(allIndex, allIndex + rsize);
				allIndex = allIndex + rsize;
			} else {
				list0 = orderIds.subList(allIndex, order_num);
			}

			// if (order_num > (i + 1) * size) {
			// list0 = orderIds.subList(i * size, (i + 1) * size);
			// } else if (i * size < order_num && order_num <= (i + 1) * size) {
			// list0 = orderIds.subList(i * size, order_num);
			// }

			batchPickTask.setOrder_ids(Tools.list2String(list0, ","));

			// batchPickTaskDao.insert(batchPickTask);
			batchPickTaskList.add(batchPickTask);

		}

		// 将订单加入波次任务更新orderProcess
		orderProcessDao.updateProcessByOrders2Task(orderIds);

		// 增加波次任务
		batchPickTaskDao.insertList(batchPickTaskList);

		resMap.put("mark", true);
		resMap.put("successMessage", "波次任务创建成功");

		// 执行查询
		HashMap<String, Object> listMap = doSearchBatchPick2(req, model);
		resMap.putAll(listMap);
		return resMap;
	}

	public HashMap<String, Object> configBatchPick(HttpServletRequest req,
			HashMap<String, Object> model) {
		String actionUser = (String) SecurityUtils.getSubject().getPrincipal();

		Subject subject = SecurityUtils.getSubject();
		Session session = subject.getSession();
		Warehouse warehouse = (Warehouse) session
				.getAttribute("currentPhysicalWarehouse");
		// 配置条件_1：物理仓库
		int physical_warehouse_id = warehouse.getPhysical_warehouse_id();
		// 配置条件_2：波次级别 1：紧急订单 2：相同订单 3：同商品订单 4：同通道等 5 ：散单
		int level = Integer.parseInt(req.getParameter("level"));
		ScheduleJob scheduleJob = new ScheduleJob();
		scheduleJob.setBeanClass("");
		return null;
	}

	/**
	 * 普通订单进行波次
	 * 
	 * @param orderInfoList1
	 * @param size 
	 * @param bpList 
	 * @param warehouse_id 
	 * @param physical_warehouse_id2 
	 */
	public void createLevel3BatchPick(List<Integer> orderInfoList, int physical_warehouse_id, int size, List<String> bpList, int warehouse_id) {
		//List<OrderProcess> list = new ArrayList<OrderProcess>();
		List<Integer> list = new ArrayList<Integer>();
		String locationBarcode;
		Date created_time = new Date();
		HashMap<String, Object> parmMap = new HashMap<String, Object>();
		
		List<Integer> orderInfoList1=new ArrayList<Integer>();
		
		if (!WorkerUtil.isNullOrEmpty(orderInfoList)) {

			// 波次锁表@dlyao start
			List<OrderProcess> orderProcessList = orderProcessDao
					.selectUnCancelOrdersForLock2Task(orderInfoList);
			
			for (OrderProcess op : orderProcessList) {
				Assert.isTrue(0 == op.getIn_batchpick_task(),
						"订单：" + op.getOrder_id() + "已经在波次任务中");
				orderInfoList1.add(op.getOrder_id());
			}
			// 波次锁表@dlyao end


			if (WorkerUtil.isNullOrEmpty(orderInfoList1)) {
				return ;
			}
			
			for (int i = 0; size * i < orderProcessList.size(); i++) {
				if (size * i + size <= orderProcessList.size()) {
					list = orderInfoList1.subList(size * i, size * i + size);
				} else {
					//@change by dlyao for warehouse's 20160816
					continue;
					//list = orderInfoList1.subList(size * i, orderInfoList1.size());
				}
				// 生成序列号
				locationBarcode = bpList.get(i);
				BatchPick bp = new BatchPick();
				bp.setBatch_pick_sn(locationBarcode);
				bp.setStatus("INIT");
				bp.setCreated_user("System");
				bp.setCreated_time(created_time);
				bp.setPhysical_warehouse_id(physical_warehouse_id);
                bp.setWarehouse_id(warehouse_id);				
				int batch_pick_group_id=Integer.parseInt(locationBarcode.substring(locationBarcode.length()-1));
				bp.setBatch_pick_group_id(batch_pick_group_id);
				
				// 1.生成批件单
				batchPickDao.insert(bp);
				int batch_pick_id = bp.getBatch_pick_id();
				
				parmMap.put("order_id", list); // op.status
				//parmMap.put("order_id", Integer.valueOf(orderInfoList1.get(index))); // op , oi
				parmMap.put("status", OrderInfo.ORDER_STATUS_BATCH_PICK); // op.status
																			// oi.order_status
				parmMap.put("batch_pick_id", batch_pick_id); // op
				parmMap.put("created_time", created_time); // oi
				parmMap.put("created_user", "System"); // oi
				
				orderInfoDao.updateOrderInfoListForBatchPick(parmMap);
				//orderProcessDao.updateOrderProcessListForBatchPick(parmMap);
				orderProcessDao.updateOrderProcessListForBatchPick2(batch_pick_id,list);
				userActionOrderDao.insertList(parmMap);

				// 记录生成日志
				UserActionBatchPick userActionBatchPick = new UserActionBatchPick();
				userActionBatchPick.setAction_note("生成波次单:" + batch_pick_id);
				userActionBatchPick.setAction_type("CREATE");
				userActionBatchPick.setCreated_time(created_time);
				userActionBatchPick.setCreated_user("System");
				userActionBatchPick.setStatus("INIT");
				userActionBatchPick.setBatch_pick_id(batch_pick_id);

				userActionBatchPickDao.insert(userActionBatchPick);
			}
		}
	}

	
	/**
	 * 普通订单进行波次
	 * 
	 * @param orderInfoList1
	 * @param size 
	 * @param bpList 
	 * @param warehouse_id 
	 * @param physical_warehouse_id2 
	 */
	public void createLevel3NewBatchPick(List<Integer> orderInfoList, int physical_warehouse_id, int size, List<String> bpList, int warehouse_id) {
		//List<OrderProcess> list = new ArrayList<OrderProcess>();
		List<Integer> list = new ArrayList<Integer>();
		String locationBarcode;
		Date created_time = new Date();
		HashMap<String, Object> parmMap = new HashMap<String, Object>();
		
		List<Integer> orderInfoList1=new ArrayList<Integer>();
		
		if (!WorkerUtil.isNullOrEmpty(orderInfoList)) {

			// 波次锁表@dlyao start
			List<OrderProcess> orderProcessList = orderProcessDao
					.selectUnCancelOrdersForLock2Task(orderInfoList);
			
			for (OrderProcess op : orderProcessList) {
				Assert.isTrue(0 == op.getIn_batchpick_task(),
						"订单：" + op.getOrder_id() + "已经在波次任务中");
				orderInfoList1.add(op.getOrder_id());
			}
			// 波次锁表@dlyao end


			if (WorkerUtil.isNullOrEmpty(orderInfoList1)) {
				return ;
			}
			
			for (int i = 0; size * i < orderProcessList.size(); i++) {
				if (size * i + size <= orderProcessList.size()) {
					list = orderInfoList1.subList(size * i, size * i + size);
				} else {
					list = orderInfoList1
							.subList(size * i, orderInfoList1.size());
				}
				// 生成序列号
				locationBarcode = bpList.get(i);
				BatchPick bp = new BatchPick();
				
				
				bp.setBatch_pick_sn(locationBarcode);
				bp.setStatus("INIT");
				bp.setCreated_user("System");
				bp.setCreated_time(created_time);
				bp.setPhysical_warehouse_id(physical_warehouse_id);
				bp.setWarehouse_id(warehouse_id);
				int batch_pick_group_id=Integer.parseInt(locationBarcode.substring(locationBarcode.length()-1));
				bp.setBatch_pick_group_id(batch_pick_group_id);

				// 1.生成批件单
				batchPickDao.insert(bp);
				int batch_pick_id = bp.getBatch_pick_id();
				
				parmMap.put("order_id", list); // op.status
				//parmMap.put("order_id", Integer.valueOf(orderInfoList1.get(index))); // op , oi
				parmMap.put("status", OrderInfo.ORDER_STATUS_BATCH_PICK); // op.status
																			// oi.order_status
				parmMap.put("batch_pick_id", batch_pick_id); // op
				parmMap.put("created_time", created_time); // oi
				parmMap.put("created_user", "System"); // oi
				
				orderInfoDao.updateOrderInfoListForBatchPick(parmMap);
				//orderProcessDao.updateOrderProcessListForBatchPick(parmMap);
				orderProcessDao.updateOrderProcessListForBatchPick2(batch_pick_id,list);
				userActionOrderDao.insertList(parmMap);

				// 记录生成日志
				UserActionBatchPick userActionBatchPick = new UserActionBatchPick();
				userActionBatchPick.setAction_note("生成波次单:" + batch_pick_id);
				userActionBatchPick.setAction_type("CREATE");
				userActionBatchPick.setCreated_time(created_time);
				userActionBatchPick.setCreated_user("System");
				userActionBatchPick.setStatus("INIT");
				userActionBatchPick.setBatch_pick_id(batch_pick_id);

				userActionBatchPickDao.insert(userActionBatchPick);
			}
		}
	}
	
	/**
	 * 批量单波次
	 * @param orderInfoList1
	 * @param maxSize
	 * @param physical_warehouse_id 
	 * @param bpList 
	 * @param minSize 
	 * @param warehouse_id 
	 */
	public void createBatchBatchPick(List<Integer> orderInfoList,int maxSize, int physical_warehouse_id, List<String> bpList, int minSize, int warehouse_id) {
		List<Integer> list = new ArrayList<Integer>();
		String locationBarcode;
		Date created_time = new Date();
		

		HashMap<String, Object> parmMap = new HashMap<String, Object>();
		
		List<Integer> orderInfoList1=new ArrayList<Integer>();
		
		if (!WorkerUtil.isNullOrEmpty(orderInfoList)) {

			// 波次锁表@dlyao start
			List<OrderProcess> orderProcessList = orderProcessDao
					.selectUnCancelOrdersForLock2Task(orderInfoList);
			
			for (OrderProcess op : orderProcessList) {
				Assert.isTrue(0 == op.getIn_batchpick_task(),
						"订单：" + op.getOrder_id() + "已经在波次任务中");
				orderInfoList1.add(op.getOrder_id());
			}
			// 波次锁表@dlyao end


			if (WorkerUtil.isNullOrEmpty(orderInfoList1)) {
				return ;
			}
			
			for (int i = 0; maxSize * i < orderInfoList1.size(); i++) {
				if (maxSize * (i + 1) <= orderInfoList1.size()) {
					list = orderInfoList1.subList(maxSize * i, maxSize * (i + 1));
				} else {
					//@change by dlyao for warehouse's 20160816
					
					list = orderInfoList1.subList(maxSize * i, orderInfoList1.size());
					if(list.size()<minSize){
						continue;
					}
				}
				logger.info("step 6");
				// 生成序列号
				locationBarcode = bpList.get(i);
				BatchPick bp = new BatchPick();
				bp.setBatch_pick_sn(locationBarcode);
				bp.setStatus("INIT");
				bp.setCreated_user("System");
				bp.setCreated_time(created_time);
                bp.setPhysical_warehouse_id(physical_warehouse_id);
                bp.setBatch_process_type("BATCH");
                bp.setWarehouse_id(warehouse_id);
                
                int batch_pick_group_id=Integer.parseInt(locationBarcode.substring(locationBarcode.length()-1));
				bp.setBatch_pick_group_id(batch_pick_group_id);
				
				// 1.生成批件单
				batchPickDao.insert(bp);
				int batch_pick_id = bp.getBatch_pick_id();
				//更新批量单标记
				//batchPickDao.updateBatchProcessType(batch_pick_id,"BATCH");
				
				parmMap.put("order_id", list); // op.status
				//parmMap.put("order_id", Integer.valueOf(orderInfoList1.get(index))); // op , oi
				parmMap.put("status", OrderInfo.ORDER_STATUS_BATCH_PICK); // op.status
																			// oi.order_status
				parmMap.put("batch_pick_id", batch_pick_id); // op
				parmMap.put("created_time", created_time); // oi
				parmMap.put("created_user", "System"); // oi
				
				orderInfoDao.updateOrderInfoListForBatchPick(parmMap);
				//orderProcessDao.updateOrderProcessListForBatchPick(parmMap);
				orderProcessDao.updateOrderProcessListForBatchPick2(batch_pick_id,list);
				userActionOrderDao.insertList(parmMap);

				// 记录生成日志
				UserActionBatchPick userActionBatchPick = new UserActionBatchPick();
				userActionBatchPick.setAction_note("生成波次单:" + batch_pick_id);
				userActionBatchPick.setAction_type("CREATE");
				userActionBatchPick.setCreated_time(created_time);
				userActionBatchPick.setCreated_user("System");
				userActionBatchPick.setStatus("INIT");
				userActionBatchPick.setBatch_pick_id(batch_pick_id);

				userActionBatchPickDao.insert(userActionBatchPick);
				logger.info("step 7");
			}
		}
	}

	public int getbatchPickNumInPhySicalWareHouseToday(Integer physical_warehouse_id) {
		String time = DateUtils.getDateString(0, "yyyy-MM-dd", " 00:00:00");
		return batchPickDao.getbatchPickNumInPhySicalWareHouseToday(physical_warehouse_id,time);
	}

	public int getOrderNumInPhySicalWareHouseToday(Integer physical_warehouse_id) {
		String time = DateUtils.getDateString(0, "yyyy-MM-dd", " 00:00:00");
		return batchPickDao.getorderNumInPhySicalWareHouseToday(physical_warehouse_id,time);
	}

	public List<OrderInfo> getSingleOrderByShopByPage(
			Map<String, Object> parmMap) {
		return orderInfoDao.getSingleOrderByShopByPage(parmMap);
	}

	/**
	 * 创建散单批件单
	 * @param order_id_list
	 * @param userName 
	 * @param warehouse_id 
	 */
	public void createOddBatchPick(List<String> order_id_list,int physical_warehouse_id, String userName, int warehouse_id) {
		List<Integer> orderInfoList=Tools.changeStringListToIntegerList(order_id_list);
		String locationBarcode;
		Date created_time = new Date();
		

		HashMap<String, Object> parmMap = new HashMap<String, Object>();
        List<Integer> orderInfoList1=new ArrayList<Integer>();
		
		if (!WorkerUtil.isNullOrEmpty(orderInfoList)) {

			// 波次锁表@dlyao start
			List<OrderProcess> orderProcessList = orderProcessDao
					.selectUnCancelOrdersForLock2Task(orderInfoList);
			
			for (OrderProcess op : orderProcessList) {
				Assert.isTrue(0 == op.getIn_batchpick_task(),
						"订单：" + op.getOrder_id() + "已经在波次任务中");
				orderInfoList1.add(op.getOrder_id());
			}
			// 波次锁表@dlyao end
			
			if (WorkerUtil.isNullOrEmpty(orderInfoList1)) {
				return ;
			}
			// 生成序列号
			locationBarcode = WorkerUtil.generatorSequence(
					SequenceUtil.KEY_NAME_BPCODE, "", true);
			BatchPick bp = new BatchPick();
			bp.setBatch_pick_sn(locationBarcode);
			bp.setStatus("INIT");
			bp.setCreated_user(userName);
			bp.setCreated_time(created_time);
            bp.setPhysical_warehouse_id(physical_warehouse_id);
            bp.setWarehouse_id(warehouse_id);
            int batch_pick_group_id=Integer.parseInt(locationBarcode.substring(locationBarcode.length()-1));
			bp.setBatch_pick_group_id(batch_pick_group_id);
            
			// 1.生成批件单
			batchPickDao.insert(bp);
			int batch_pick_id = bp.getBatch_pick_id();
			parmMap.put("order_id", orderInfoList1); // op.status
			//parmMap.put("order_id", Integer.valueOf(orderInfoList1.get(index))); // op , oi
			parmMap.put("status", OrderInfo.ORDER_STATUS_BATCH_PICK); // op.status
																		// oi.order_status
			parmMap.put("batch_pick_id", batch_pick_id); // op
			parmMap.put("created_time", created_time); // oi
			parmMap.put("created_user", userName); // oi
			//parmMap.put("order_batch_sequence_number", index + 1); // op
			
			orderInfoDao.updateOrderInfoListForBatchPick(parmMap);
			//orderProcessDao.updateOrderProcessListForBatchPick(parmMap);
			orderProcessDao.updateOrderProcessListForBatchPick2(batch_pick_id,orderInfoList1);
			userActionOrderDao.insertList(parmMap);

			// 记录生成日志
			UserActionBatchPick userActionBatchPick = new UserActionBatchPick();
			userActionBatchPick.setAction_note("生成波次单:" + batch_pick_id);
			userActionBatchPick.setAction_type("CREATE");
			userActionBatchPick.setCreated_time(created_time);
			userActionBatchPick.setCreated_user("System");
			userActionBatchPick.setStatus("INIT");
			userActionBatchPick.setBatch_pick_id(batch_pick_id);

			userActionBatchPickDao.insert(userActionBatchPick);
		
		}
	}

	public int getOrdersInPhysicalWareHouseTodayForBp(
			Integer physical_warehouse_id) {
		String time = DateUtils.getDateString(0, "yyyy-MM-dd", " 00:00:00");
		return batchPickDao.getOrdersInPhysicalWareHouseTodayForBp(physical_warehouse_id,time);
	}


	/**
	 * 获取需要波次的波此单
	 * @param string 
	 * @param customer_id 
	 * @param physical_warehouse_id 
	 * @param isToB 
	 * @param warehouse_id 
	 * @return
	 */
	public List<Integer> getNeedAllotSaleBatchPick(Integer physical_warehouse_id, Integer customer_id, String status, int isToB, int warehouse_id) {
		return batchPickDao.getNeedAllotSaleBatchPick(physical_warehouse_id,customer_id,status,isToB,warehouse_id);
	}

	/**
	 * 根据波此单id获取波此单商品
	 * @param batchPickList
	 * @return
	 */
	public List<Map> selectPickOrderGoodsList(List<Integer> batchPickList) {
		
		return orderInfoDao.selectPickOrderGoodsListByList(batchPickList);
	}

	public List<Map> selectPickOrderGoodsListSum(List<Integer> batchPickList) {
		return orderInfoDao.selectPickOrderGoodsListSum(batchPickList);
	}

	public void createToBBatchPick(List<Integer> orderInfoList,
			int physical_warehouse_id, int size, List<String> bpList, String type, int warehouse_id) {
		List<Integer> list = new ArrayList<Integer>();
		String locationBarcode;
		Date created_time = new Date();
		HashMap<String, Object> parmMap = new HashMap<String, Object>();
		
        List<Integer> orderInfoList1=new ArrayList<Integer>();
		
		if (!WorkerUtil.isNullOrEmpty(orderInfoList)) {

			// 波次锁表@dlyao start
			List<OrderProcess> orderProcessList = orderProcessDao
					.selectUnCancelOrdersForLock2Task(orderInfoList);
			
			for (OrderProcess op : orderProcessList) {
				Assert.isTrue(0 == op.getIn_batchpick_task(),
						"订单：" + op.getOrder_id() + "已经在波次任务中");
				orderInfoList1.add(op.getOrder_id());
			}
			// 波次锁表@dlyao end
			

			if (WorkerUtil.isNullOrEmpty(orderInfoList1)) {
				return ;
			}
			
			for (int i = 0; size * i < orderProcessList.size(); i++) {
				if (size * i + size < orderProcessList.size()) {
					list = orderInfoList1.subList(size * i, size * i + size);
				} else {
					list = orderInfoList1
							.subList(size * i, orderInfoList1.size());
				}
				// 生成序列号
				locationBarcode = bpList.get(i);
				BatchPick bp = new BatchPick();
				bp.setBatch_pick_sn(locationBarcode);
				bp.setStatus("INIT");
				bp.setCreated_user("System");
				bp.setCreated_time(created_time);
				bp.setPhysical_warehouse_id(physical_warehouse_id);
                bp.setBatch_process_type(type);
                bp.setWarehouse_id(warehouse_id);
                int batch_pick_group_id=Integer.parseInt(locationBarcode.substring(locationBarcode.length()-1));
				bp.setBatch_pick_group_id(batch_pick_group_id);
                
				// 1.生成批件单
				batchPickDao.insert(bp);
				int batch_pick_id = bp.getBatch_pick_id();
				
				//更新toB标记
				//batchPickDao.updateBatchProcessType(batch_pick_id,type);
				
				parmMap.put("order_id", list); // op.status
				//parmMap.put("order_id", Integer.valueOf(orderInfoList1.get(index))); // op , oi
				parmMap.put("status", OrderInfo.ORDER_STATUS_BATCH_PICK); // op.status
																			// oi.order_status
				parmMap.put("batch_pick_id", batch_pick_id); // op
				parmMap.put("created_time", created_time); // oi
				parmMap.put("created_user", "System"); // oi
				
				orderInfoDao.updateOrderInfoListForBatchPick(parmMap);
				//orderProcessDao.updateOrderProcessListForBatchPick(parmMap);
				orderProcessDao.updateOrderProcessListForBatchPick2(batch_pick_id,list);
				userActionOrderDao.insertList(parmMap);

				// 记录生成日志
				UserActionBatchPick userActionBatchPick = new UserActionBatchPick();
				userActionBatchPick.setAction_note("生成波次单:" + batch_pick_id);
				userActionBatchPick.setAction_type("CREATE");
				userActionBatchPick.setCreated_time(created_time);
				userActionBatchPick.setCreated_user("System");
				userActionBatchPick.setStatus("INIT");
				userActionBatchPick.setBatch_pick_id(batch_pick_id);

				userActionBatchPickDao.insert(userActionBatchPick);
			}
			
		}
	}

	public List<Integer> batchPickAllOrderListByParmV3(
			Map<String, Object> parmMap) {
		return orderInfoDao.batchPickAllOrderListByParmV3(parmMap);
	}

	public List<SameDetails> batchPickAllSameV3(Map<String, Object> parmMap) {
		return orderInfoDao.selectOrderInfoListForBatchPickV3(parmMap);
	}

	public List<Integer> getNeedAllotSaleBatchPickV2(
			Integer physical_warehouse_id, Integer customer_id, String status,
			int isToB, int warehouse_id) {
		return batchPickDao.getNeedAllotSaleBatchPickV2(physical_warehouse_id,customer_id,status,isToB,warehouse_id);
	}

	public List<OrderInfo> getSingleOrderByShopByPageV2(
			Map<String, Object> parmMap, int pick) {
		List<OrderInfo> orderInfoList = orderInfoDao.getSingleOrderByShopV2ByPage(parmMap);
		
		int physical_warehouse_id = (Integer)parmMap.get("physical_warehouse_id");
		int warehouse_id = (Integer)parmMap.get("warehouse_id");
		
		//需要区分是否有货的商品
		if(1==pick){
			if(!WorkerUtil.isNullOrEmpty(orderInfoList)){
				
				//订单id集合
				List<Integer> orderIdList =new ArrayList<Integer>();
				for(OrderInfo oi:orderInfoList){
					orderIdList.add(oi.getOrder_id());
				}
				
				//订单商品集合
				List<OrderGoods> ogList = orderInfoDao.getOrderGoodsList(orderIdList);
				
				List<Integer> productIdList =new ArrayList<Integer>();
				for(OrderGoods og:ogList){
					productIdList.add(og.getProduct_id());
				}
				
				
				String  location_type ="('PIECE_PICK_LOCATION','BOX_PICK_LOCATION','RETURN_LOCATION','STOCK_LOCATION')";
				//库存集合
				List<ProductLocation> productlocationList = productLocationDao.selectProductlocationListV3(physical_warehouse_id,warehouse_id, productIdList, location_type, "NORMAL");
				
				Map<String, List<ProductLocation>> productlocationListMap = new HashMap<String, List<ProductLocation>>();

				// 求出每种商品的退货区可用数量
				Map<String, Integer> availableMap = new HashMap<String, Integer>();

				cutProductLocationByProductId(productlocationListMap,
						productIdList, productlocationList);

				calculateProductAvailable(productIdList, productlocationListMap,
						availableMap);
				
				//不够分的订单id集合
				List<Integer> errorOrderIdList=new ArrayList<Integer>();
				
				for(OrderGoods og:ogList){
					Integer order_id =og.getOrder_id();
					
					if(errorOrderIdList.contains(order_id)){
						continue;
					}
					
					int p_id=og.getProduct_id();
					int need=og.getGoods_number();
					int available= null==availableMap.get(p_id+"")?0:Integer.parseInt(availableMap.get(p_id+"").toString());
					
					if (need>available){
						errorOrderIdList.add(order_id);
					}
					else{
						availableMap.put(p_id+"", available-need);
					}
				}
				
				for(OrderInfo oi:orderInfoList){
					if(errorOrderIdList.contains(oi.getOrder_id())){
						oi.setCanAllot(2);
					}else{
						oi.setCanAllot(1);
					}
				}
				
			}
			
		}
		
		
		return orderInfoList;
	}
	
	/**
	 * 
	 * @param goodsIdList
	 *            商品id集合
	 * @param productlocationListMap
	 *            某区域 按商品的productlocation记录 集合
	 * @param availableMap
	 *            某区域每个商品可用数目
	 */
	private void calculateProductAvailable(List<Integer> goodsIdList,
			Map<String, List<ProductLocation>> productlocationListMap,
			Map<String, Integer> availableMap) {

		List<ProductLocation> pllist = new ArrayList<ProductLocation>();
		for (int gid : goodsIdList) {
			pllist = productlocationListMap.get(gid + "");
			int sum = 0;
			for (ProductLocation l : pllist) {
				sum += l.getQty_available();
			}
			availableMap.put(gid + "", sum);
		}

	}

	/**
	 * 
	 * @param productlocationListMap
	 *            某区域 按商品的productlocation记录 集合
	 * @param goodsIdList
	 *            商品id集合
	 * @param productlocationList
	 *            某区域所有的productlocation记录
	 */
	private void cutProductLocationByProductId(
			Map<String, List<ProductLocation>> productlocationListMap,
			List<Integer> goodsIdList, List<ProductLocation> productlocationList) {

		for (Integer pro_id : goodsIdList) {
			List<ProductLocation> productlocationListTemp = new ArrayList<ProductLocation>();
			for (ProductLocation pl : productlocationList) {
				if (pl.getProduct_id() - pro_id == 0) {
					productlocationListTemp.add(pl);
				}
			}

			productlocationListMap.put(pro_id + "", productlocationListTemp);
		}
	}

	@Override
	public List<Integer> getToApplyFirstShipmentBatchPickIdList(Integer physicalWarehouseId,Integer batchPickGroupId,
			Date startDateTime, Date endDateTime) {
		Map<String, Object> paramsMap = new HashMap<String, Object>();
		paramsMap.put("physicalWarehouseId", physicalWarehouseId);
		paramsMap.put("batchPickGroupId", batchPickGroupId);
		paramsMap.put("startDateTime", startDateTime);
		paramsMap.put("endDateTime", endDateTime);
		return batchPickDao.selectToApplyFirstShipmentBatchPickIdList(paramsMap);
	}

	@Override
	public void updateFlowStatus(Integer batchPickId, String toFlowStatus) {
		batchPickDao.updateFlowStatus(batchPickId,toFlowStatus);
		
	}

}