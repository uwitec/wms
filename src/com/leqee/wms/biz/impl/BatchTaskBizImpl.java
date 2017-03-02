package com.leqee.wms.biz.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.leqee.wms.biz.BatchTaskBiz;
import com.leqee.wms.dao.BatchTaskDao;
import com.leqee.wms.dao.TaskDao;
import com.leqee.wms.dao.UserActionTaskDao;
import com.leqee.wms.entity.Task;
import com.leqee.wms.entity.UserActionTask;
import com.leqee.wms.util.WorkerUtil;

@Service
public class BatchTaskBizImpl implements BatchTaskBiz {
	private Logger logger = Logger.getLogger(BatchTaskBizImpl.class);

	@Autowired
	BatchTaskDao batchTaskDao;
	@Autowired
	TaskDao taskDao;
	@Autowired
	UserActionTaskDao userActionTaskDao;
	
	@Override
	public Map<String, Object> bindBatchTaskForWeb(Integer userId, String username, String batchTaskSn) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map<String,Object> batchTaskInfo = batchTaskDao.selectBatchTaskByBatchTaskSn(batchTaskSn);
		if (WorkerUtil.isNullOrEmpty(batchTaskInfo)) {
			resultMap.put("note", "补货批次号“" + batchTaskSn+"”不存在或已经全部取消");
			resultMap.put("note_id", 2);
			resultMap.put("success", false);
			return resultMap;
		}else if(!WorkerUtil.isNullOrEmpty(batchTaskInfo.get("bind_user_group"))){
			resultMap.put("note", "补货批次号已经与“"+batchTaskInfo.get("bind_user_group")+"”绑定！");
			resultMap.put("note_id", 3);
			resultMap.put("success", false);
			return resultMap;
		}else if(String.valueOf(batchTaskInfo.get("task_platform_group")).contains("RF")){
			resultMap.put("note", "补货批次号中存在RF操作任务！");
			resultMap.put("note_id", 3);
			resultMap.put("success", false);
			return resultMap;
		}

		Integer batchTaskId = Integer.parseInt(String.valueOf(batchTaskInfo.get("batch_task_id")));
		Integer col = taskDao.updateTaskForWEBBind(userId,batchTaskId);
		if(col>0){
			List<UserActionTask> userActionTaskList = new ArrayList<UserActionTask>();
			List<Task> taskList = taskDao.getTaskIdByBatchTaskId(batchTaskId);
			String actionUser = (String) SecurityUtils.getSubject().getPrincipal();
			for (Task task : taskList) {
				UserActionTask userActionTask = new UserActionTask();
				userActionTask.setTask_id(task.getTask_id());
				userActionTask.setAction_note("分配补货批次任务");
				userActionTask.setAction_type("BIND");
				userActionTask.setTask_status(task.getTask_status());
				userActionTask.setCreated_user(actionUser);
				userActionTask.setCreated_time(new Date());
				userActionTaskList.add(userActionTask);
			}
			col = userActionTaskDao.batchInsert(userActionTaskList);
			if(col >0){
				resultMap.put("success", true);
			}else{
				resultMap.put("success", false);
				resultMap.put("note", "插入绑定记录失败，请重试！");
				resultMap.put("note_id", 3);
			}
		}else{
			resultMap.put("success", false);
			resultMap.put("note", "更新绑定记录失败，请重试！");
			resultMap.put("note_id", 3);
		}
		return resultMap;
	}


	

}