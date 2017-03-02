package com.leqee.wms.util;

import java.util.List;

import org.mybatis.spring.support.SqlSessionDaoSupport;

public class DaoUtil  extends SqlSessionDaoSupport{
	
	public List<Integer> testStringSql(String sql){
//		List<Integer> list=new ArrayList<Integer>();
//		Connection con=this.getSqlSession().getConnection();
//		PreparedStatement ps=null;
//		ResultSet rs=null;
//		try {
//			ps = con.prepareStatement(sql);
//			rs=ps.executeQuery();
//			while (rs.next()) {
//				list.add(rs.getInt("id"));
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if(rs!=null){
//				try {
//					rs.close();
//				} catch (Exception e) {
//					//System.out.println("关闭结果集ResultSet异常！"+e.getMessage());
//				}
//			}
//			if(ps!=null){
//				try {
//					ps.close();
//				} catch (Exception e) {
//					//System.out.println("关闭结果集ResultSet异常！"+e.getMessage());
//				}
//			}
//			if(con!=null){
//				try {
//					con.close();
//				} catch (Exception e) {
//					//System.out.println("关闭结果集ResultSet异常！"+e.getMessage());
//				}
//			}
//		}
		return null;
	}
}
