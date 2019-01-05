package com.item.user.dao;

import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

import com.item.user.domain.User;

import cn.itcast.jdbc.TxQueryRunner;

public class UserDao {
	private QueryRunner qr=new TxQueryRunner();
	
	public User findByUserName(String username){
		try {
			String sql="select *from b_user where username=?";
			return qr.query(sql, new BeanHandler<User>(User.class),username);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public User findByEamil(String email){
		try {
			String sql="select *from b_user where email=?";
			return qr.query(sql, new BeanHandler<User>(User.class),email);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	public void add(User user){
		try {
			String sql="insert into b_user values(?,?,?,?,?,?)";
			Object[] params={user.getUid(),user.getUsername(),
					user.getPassword(),user.getEmail(),user.getCode(),user.isState()};
			qr.update(sql,params);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * 按激活码查询
	 * @param code
	 * @return
	 */
	public User findByCode(String code){
		
		try {
			String sql="select * from b_user where code=?";
			return qr.query(sql, new BeanHandler<User>(User.class),code);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * 修改指定用户的指定状态
	 * @param uid
	 * @param state
	 */
	public  void updateCode(String uid,boolean state){
		
		try {
			String sql="update b_user set state=? where uid=? ";
			qr.update(sql, state, uid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
}


