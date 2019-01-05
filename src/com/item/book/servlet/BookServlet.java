package com.item.book.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.item.book.service.BookService;

import cn.itcast.servlet.BaseServlet;

/**
 * Servlet implementation class BookServlet
 */
@WebServlet("/BookServlet")
public class BookServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
    private BookService bookService=new BookService();
	
    public String load(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
    	/*
		 * 1. 得到参数bid
		 * 2. 查询得到Book
		 * 3. 保存，转发到desc.jsp
		 */
    	String bid=request.getParameter("bid");
		request.setAttribute("book", bookService.load(request.getParameter("bid")));
		return "f:/jsps/book/desc.jsp";
    }
    
    public String findAll(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		/*
		 * 查询所有图书
		 */
			request.setAttribute("bookList", bookService.findAll());
			return "f:/jsps/book/list.jsp";
	}

    public String findById(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
    	String cid=request.getParameter("cid");
    	request.setAttribute("bookList",bookService.findById(cid));
    	return "f:/jsps/book/list.jsp";
    }
}
