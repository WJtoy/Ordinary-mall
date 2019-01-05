package com.item.order.service;

import java.sql.SQLException;
import java.util.List;

import com.item.order.dao.OrderDao;
import com.item.order.domain.Order;

import cn.itcast.jdbc.JdbcUtils;

public class OrderService {
	private OrderDao orderDao=new OrderDao();
	
	public void zhiFu(String oid){
		/*
		 * 获取订单状态
		 */
		
		int state=orderDao.getStateByOid(oid);
		if(state==1){
			//修改订单状态为2
			orderDao.updateState(oid, 2);
		}
			
	}
	
	public void add(Order order){
		try{
			//开启事务
			JdbcUtils.beginTransaction();
			orderDao.addOrder(order);	//插入订单
			orderDao.addOrderItemList(order.getOrderItemList());	//插入订单 中的所有条目
			//提交事务
			JdbcUtils.commitTransaction();
		}catch(Exception e){
			//回滚事务
			try{
				JdbcUtils.commitTransaction();
			}catch(SQLException e1){
				
			}
			throw new RuntimeException(e);
		}
	}
	
	/*
	 * 我的订单
	 */
	public List<Order> myOrders(String uid) {
		return orderDao.findByUid(uid);
	}

	public Order load(String oid) {
		// TODO Auto-generated method stub
		return orderDao.load(oid);
	}
	
	public void confirm(String oid) throws OrderException{
		int state=orderDao.getStateByOid(oid);	//获取订单状态
		if(state!=3)throw new OrderException("订单确认失败，你的订单有错误！");
		//修改订单状态为4 ，表示交易成功
		orderDao.updateState(oid, 4); 
		
	}
}
