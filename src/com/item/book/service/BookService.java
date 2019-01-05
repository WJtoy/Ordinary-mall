package com.item.book.service;

import java.util.List;

import com.item.book.dao.BookDao;
import com.item.book.domain.Book;

public class BookService {
	private BookDao bookDao=new BookDao();
	
	public List<Book> findAll(){
		return bookDao.findAll();
	}

	public List<Book> findById(String cid) {
		return bookDao.findById(cid);
	}

	public Book load(String parameter) {
		return bookDao.load(parameter);
	}
}
