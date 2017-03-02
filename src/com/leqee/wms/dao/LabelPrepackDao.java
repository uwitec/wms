package com.leqee.wms.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.leqee.wms.entity.BatchPick;
import com.leqee.wms.entity.LabelPrepack;
import com.leqee.wms.entity.OrderInfo;
import com.leqee.wms.entity.ProductLocationDetail;

/**
 * @author Jarvis
 * @CreatedDate 2016.02.02
 *
 */
public interface LabelPrepackDao {
	
	public int insert(LabelPrepack labelPrepack);

	public void updatePackBoxNeedOut(@Param("label_prepack_id")int label_prepack_id,@Param("packbox_need_out") int packbox_need_out);

}
