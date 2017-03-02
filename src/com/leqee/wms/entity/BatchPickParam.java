package com.leqee.wms.entity;

import java.io.Serializable;

public class BatchPickParam  implements Serializable {

	private static final long serialVersionUID = 1L;
	
	
	private String size;
	
	private String minSize;
	
	private String maxSize;
	
	private String minWeight;
	
	
	private String maxWeight;
	
	private  String level;
	
	private String time;
	
	private String runTimeStart="";
	private String runTimeEnd="";
	private String createTime="";

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getMinSize() {
		return minSize;
	}


	public void setMinSize(String minSize) {
		this.minSize = minSize;
	}

	public String getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(String maxSize) {
		this.maxSize = maxSize;
	}

	public String getMinWeight() {
		return minWeight;
	}

	public void setMinWeight(String minWeight) {
		this.minWeight = minWeight;
	}

	public String getMaxWeight() {
		return maxWeight;
	}

	public void setMaxWeight(String maxWeight) {
		this.maxWeight = maxWeight;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getRunTimeStart() {
		return runTimeStart;
	}

	public void setRunTimeStart(String runTimeStart) {
		this.runTimeStart = runTimeStart;
	}

	public String getRunTimeEnd() {
		return runTimeEnd;
	}

	public void setRunTimeEnd(String runTimeEnd) {
		this.runTimeEnd = runTimeEnd;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	
}
