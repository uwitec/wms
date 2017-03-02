package com.leqee.wms.entity;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.leqee.wms.util.SpringUtil;

/** 
* @author qyyao 
* 
* Sequence主键生成器载体 
*/ 
public class KeyInfo { 
    private long maxKey;        //当前Sequence载体的最大值 
    private long minKey;        //当前Sequence载体的最小值 
    private long nextKey;       //下一个Sequence值 
    private int poolSize;       //Sequence值缓存大小 
    private String keyName;     //Sequence的名称 
    private String date;        //有效日期
    
    private Logger logger = Logger.getLogger(KeyInfo.class);
    
    public KeyInfo(String keyName, int poolSize) throws SQLException { 
        this.poolSize = poolSize; 
        this.keyName = keyName; 
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		this.date = sdf.format((new Date()));
        retrieveFromDB(true); 
    } 

    public String getKeyName() { 
        return keyName; 
    } 

    public long getMaxKey() { 
        return maxKey; 
    } 

    public long getMinKey() { 
        return minKey; 
    } 

    public int getPoolSize() { 
        return poolSize; 
    } 

    
    public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	/** 
     * 获取下一个Sequence值 
     * 
     * @return 下一个Sequence值 
     * @throws SQLException 
     */ 
    public synchronized long getNextKey(Boolean flag) throws SQLException { 
    	
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String today = sdf.format((new Date()));
    	
        if ((nextKey > maxKey) || (!today.equalsIgnoreCase(this.date))) { 
            retrieveFromDB(flag);
        	
        } 
        return nextKey++; 
    } 

    
    
    /** 
     * 执行Sequence表信息初始化和更新工作 
     * 
     * @throws SQLException 
     */ 
    private void retrieveFromDB(Boolean flag) throws SQLException { 
    	
    	// 参数初始化
    	if(flag == null) {
    		flag = true;
    	}
    	
    	DataSource dataSource = (DataSource) SpringUtil.getBean("dataSourceMaster");
		Connection conn = null;
		
		PreparedStatement pstmt_query = null;
		ResultSet rs = null;
		PreparedStatement pstmt_insert = null;
		PreparedStatement pstmt_update = null;
		
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String today = sdf.format((new Date()));
		
		// 设置今天日期
		this.date = today;
		
    	try {
    		conn = dataSource.getConnection();
        	conn.setAutoCommit(false);
        	
        	String sql_query = "select key_value from wms.key_info where key_name='"+ keyName +"' ";
        	if(flag){
        		sql_query += " and key_date >= '" + today + "'";
        	}
    		
        	pstmt_query = conn.prepareStatement(sql_query); 
        	rs = pstmt_query.executeQuery(); 
        	Long keyValue = null;
            if (rs.next()) { 
            	keyValue = rs.getLong("key_value");
            } else { 
                String sql_init = "insert into wms.key_info(key_name,key_value,key_date) values( '" + keyName + "',0,now() )"; 
                pstmt_insert = conn.prepareStatement(sql_init); 
                int insertEffectRows = pstmt_insert.executeUpdate(); 
                if(insertEffectRows < 1){
                	throw new RuntimeException("retrieveFromDB insert keyInfo failed!");
                }
                
                keyValue = 0L;
            } 
            
            //System.out.println("更新Sequence最大值！"); 
            String sql_update = "update wms.key_info set key_value= key_value + ? where key_name= ? and key_date >= ?"; 
            pstmt_update = conn.prepareStatement(sql_update); 
            pstmt_update.setLong(1, poolSize); 
            pstmt_update.setString(2, keyName); 
            pstmt_update.setString(3, today);
            int updateEffectRows = pstmt_update.executeUpdate(); 
            if(updateEffectRows < 1){
            	throw new RuntimeException("retrieveFromDB update keyValue failed!");
            }
            
            conn.commit(); 
            
            maxKey = keyValue + poolSize; 
            minKey = maxKey - poolSize + 1; 
            nextKey = minKey; 
        	
    	} finally { // 关闭该关闭的资源
    		if(rs != null) {
    			close(rs);
    		}
    		if(pstmt_query != null) {
    			close(pstmt_query);
    		}
    		if(pstmt_insert != null) {
    			close(pstmt_insert);
    		}
    		if(pstmt_update != null) {
    			close(pstmt_update);
    		}
    		if(conn != null) {
    			close(conn);
    		}
    	}
    } 
    
    private void close(ResultSet resultSet) {
		try {
			resultSet.close();
		} catch (SQLException e) {
			logger.error("resultSet close Exception: ",e);
		}
    }
    
    private void close(PreparedStatement preparedStatement) {
    	try {
			preparedStatement.close();
		} catch (SQLException e) {
			logger.error("preparedStatement close Exception: ",e);
		}
    }
    
    private void close(Connection connection) {
    	try {
			connection.close();
		} catch (SQLException e) {
			logger.error("connection close Exception: ",e);
		}
    }
    
    /** 
     * 执行Sequence表信息初始化和更新工作 
     * 
     * @throws SQLException 
     */ 
//    private void retrieveFromDB1(Boolean flag) throws SQLException { 
//    	//1、获取该keyName的keyValue（也就是该主键的最大值）
//    	Map<String,Object> map = new HashMap<String,Object>();
//    	map.put("keyName", keyName);
//    	
//    	Long keyValue = null;
//    	if(flag == Boolean.TRUE){
//    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//    		String date = sdf.format((new Date()));
//    		map.put("keyDate", date);
//    		keyValue = keyInfoBiz.selectKeyValueByKeyName(map);
//    	}else{
//    		keyValue = keyInfoBiz.selectKeyValueByKeyName(map);
//    	}
//    	
//    	if(WorkerUtil.isNullOrEmpty(keyValue)){  //keyValue为空时将异常往外抛
//    		logger.error("keyName:" + keyName + " has not keyValue in key_info ");
//    		keyInfoBiz.insertKeyName(keyName);
//    		keyValue = 0L;
//    		//throw new SQLException("keyName:" + keyName + " has not keyValue in key_info ");
//    	}
//    	
//    	
//    	//2、更新keyInfo表中该主键的最大值
//    	keyInfoBiz.updateKeyValueByKeyName(keyName , poolSize );  
//    	
//    	//3、重新赋值maxkey、minKey、nextKey
//    	maxKey = keyValue + poolSize; 
//		minKey = maxKey - poolSize + 1; 
//		nextKey = minKey; 
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//		this.date = sdf.format((new Date()));
//
//    } 
}
