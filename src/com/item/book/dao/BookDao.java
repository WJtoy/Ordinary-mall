package com.item.book.dao;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import com.item.book.domain.Book;

import cn.itcast.jdbc.TxQueryRunner;

public class BookDao {
	private QueryRunner qr=new TxQueryRunner();
	/**
	 * 查询所有图书
	 * @return
	 */
	public List<Book> findAll(){
		String sql="select * from book";
		try {
			return qr.query(sql, new BeanListHandler<Book>(Book.class));
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * 按分类查询
	 * @param cid
	 * @return
	 */
	public List<Book> findById(String cid) {
		String sql="select * from book where cid=?";
		try {
			return qr.query(sql, new BeanListHandler<Book>(Book.class),cid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	public Book load(String parameter) {
		try {
			String sql = "select * from book where bid=?";
			return qr.query(sql, new BeanHandler<Book>(Book.class), parameter);
		} catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
}
