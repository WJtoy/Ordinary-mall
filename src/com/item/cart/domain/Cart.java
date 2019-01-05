package com.item.cart.domain;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class Cart {
	private Map<String,CartItems> map=new LinkedHashMap<String,CartItems>();
	
	/*
	 * 计算合计
	 */
	public double getTotal(){
		//合计=所有条目的小计之和
		double total=0;
		for(CartItems cartItem:map.values()){
			total+=cartItem.getSubtotal();
		}
		return total;
	}
	
	/*
	 * 添加条目到车内
	 */
	public void add(CartItems cartItem){
		if(map.containsKey(cartItem.getBook().getBid())){	//判断原来车中是否存在该条目
			CartItems _cartItem=map.get(cartItem.getBook().getBid());//返回原条目
			_cartItem.setCount(_cartItem.getCount()+cartItem.getCount());//设置老条目的数量和新条目的数量
			map.put(cartItem.getBook().getBid(), _cartItem);
		}else{
			map.put(cartItem.getBook().getBid(), cartItem);
		}
	}
	/*
	 * 删除一条目
	 */
	public void delete(String bid){
		map.remove(bid);
	}
	/*
	 * 清空购物车
	 */
	public void clear(){
		map.clear();
	}
	/*
	 * 获取所有条目
	 */
	public Collection<CartItems> getCartItems(){
		return map.values();
	}
}
