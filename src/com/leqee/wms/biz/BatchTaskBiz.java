package com.leqee.wms.biz;

import java.util.Map;


public interface BatchTaskBiz {

	Map<String, Object> bindBatchTaskForWeb(Integer id, String username,String batch_task_sn);

}
