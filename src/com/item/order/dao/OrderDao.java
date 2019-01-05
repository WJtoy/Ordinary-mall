package com.item.order.dao;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import com.item.book.domain.Book;
import com.item.order.domain.Order;
import com.item.order.domain.OrderItem;

import cn.itcast.commons.CommonUtils;
import cn.itcast.jdbc.TxQueryRunner;

public class OrderDao {
	private QueryRunner qr=new TxQueryRunner();
	
	/*
	 * 添加订单
	 */
	public void addOrder(Order order){
		try{
			String sql="insert into orders value(?,?,?,?,?,?)";
			Timestamp timeStamp=new Timestamp(order.getOrderTime().getTime());
			Object[] params={order.getOid(),timeStamp,order.getTotal(),
					order.getState(),order.getOwner().getUid(),order.getAddress()};
			qr.update(sql, params);
		}catch(SQLException e){
			throw new RuntimeException(e);
		}
	}
	
	public void addOrderItemList(List<OrderItem> orderItemList){
		/**
		 * QueryRunner类的batch（String sql,Object[][] params）
		 * params是多个一维数组！
		 */
		try{
			String sql="insert into orderitem values(?,?,?,?,?)";
			Object[][] params=new Object[orderItemList.size()][];
			//遍历循环orderItemList，使用orderItem对象为params中的每个一维数组赋值
			for(int i=0;i<orderItemList.size(); i++){
				OrderItem item=orderItemList.get(i);
				params[i]=new Object[]{	item.getIid(),item.getCount(),
						item.getSubtotal(),item.getOrder().getOid(),item.getBook().getBid()
				};
			}
			qr.batch(sql, params);	//执行批处理
		}catch(SQLException e){
			throw new RuntimeException(e);
		}
		
	}

	public List<Order> findByUid(String uid) {
		/*
		 *1. 通过uid查询当前用户的所有List<Order>
		 *2.遍历循环每个Order，为其加载他的所有Order Item
		 */
		try{
			String sql="select * from orders where  uid=?";
			List<Order>orderList=qr.query(sql, new BeanListHandler<Order>(Order.class),uid);
			
			for(Order order:orderList){
				loadOrderItems(order);	//为order 对象添加它的所有订单条目
			}
			
			//返回订单列表
			return orderList;
		}catch(SQLException e){
			throw new RuntimeException(e); 
		}
	}

	private void loadOrderItems(Order order) throws SQLException {
		/*
		 * 查询两张表：orderItem,book
		 */
		String sql="select * from orderitem i,book b where i.bid=b.bid and oid=?";
		/*
		 * 因为查询出的结果集对应的不再是一个JavaBean，所以不能再使用BeanListHandler，而是MapListHandler
		 */
		List<Map<String , Object>>mapList=qr.query(sql, new MapListHandler(),order.getOid());
		/*
		 * mapList是多个map,每个map对应一行结果集
		 */
		//order.setOrderItemList(orderItemList);
		List<OrderItem> orderItemList=toOrderItemList(mapList);
		order.setOrderItemList(orderItemList);
	}
	/**
	 * 把MapList中每个Map转换成两个对象，并建立关系
	 * @param mapList
	 * @return
	 */
	private List<OrderItem> toOrderItemList(List<Map<String, Object>> mapList) {
		List<OrderItem>orderItemList=new ArrayList<OrderItem>();
		for(Map<String,Object>map:mapList){
			OrderItem item=toOrderItem(map);
			orderItemList.add(item);
		}
		return orderItemList;
	}

	private OrderItem toOrderItem(Map<String, Object> map) {
		OrderItem orderItem=CommonUtils.toBean(map, OrderItem.class);
		Book book=CommonUtils.toBean(map, Book.class);
		orderItem.setBook(book);
		return orderItem;
	}

	public Order load(String oid) {
		try{
			String sql="select * from orders where  oid=?";
			Order order=qr.query(sql, new BeanHandler<Order>(Order.class),oid);
			
			//为order加载它的所有结构
			loadOrderItems(order);	//为order 对象添加它的所有订单条目
			
			//返回订单列表
			return order;
		}catch(SQLException e){
			throw new RuntimeException(e); 
		}
	}
	
	public int getStateByOid(String oid){
		try{
			String sql="select state from orders where oid=?";
			return (int) qr.query(sql, new ScalarHandler(),oid);
		}catch(SQLException e){
			throw new RuntimeException(e);
		}
	}
	public void updateState(String oid,int state){
		try{
			String sql="update orders set  state =? where oid=?";
			qr.update(sql,state,oid);
			
		}catch(SQLException e){
			throw new RuntimeException(e);
		}
	}
}
