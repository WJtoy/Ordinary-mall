package com.item.cart.domain;

import java.math.BigDecimal;

import com.item.book.domain.Book;

public class CartItems {
	private Book book;	//商品
	private int count;	//数量
	
	
	public double getSubtotal(){	//小计方法，但它没有对应的成员！
		BigDecimal d1=new BigDecimal(book.getPrice()+"");	//该商品价格
		BigDecimal d2=new BigDecimal(count+"");	//该商品数量
		return d1.multiply(d2).doubleValue();
	}
	
	public Book getBook() {
		return book;
	}
	public void setBook(Book book) {
		this.book = book;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
}
