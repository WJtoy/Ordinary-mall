package com.item.cart.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.item.book.domain.Book;
import com.item.book.service.BookService;
import com.item.cart.domain.Cart;
import com.item.cart.domain.CartItems;

import cn.itcast.servlet.BaseServlet;

/**
 * Servlet implementation class CartServlet
 */
@WebServlet("/CartServlet")
public class CartServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	
	public String add(HttpServletRequest arg0, HttpServletResponse arg1) 
			throws ServletException, IOException {
		/*
		 * 添加车，得到条目和数量
		 */
		Cart cart=(Cart) arg0.getSession().getAttribute("cart");
		String bid=arg0.getParameter("bid");
		Book book=new BookService().load(bid);
		int count=Integer.parseInt(arg0.getParameter("count"));
		CartItems cartItems= new CartItems();
		cartItems.setBook(book);
		cartItems.setCount(count);
		/*
		 * 把条目放入购物车内
		 */
		cart.add(cartItems);
		return "f:/jsps/cart/list.jsp"; 
	}
	
	public String clear(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		/*
		 * 清空购物车，
		 *	1.的到车
		 */
		Cart cart=(Cart) request.getSession().getAttribute("cart");
		cart.clear();
		return "f:/jsps/cart/list.jsp";
	}
	
	
	public String delete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Cart cart=(Cart) request.getSession().getAttribute("cart");
		String bid=request.getParameter("bid");
		cart.delete(bid);
		return "f:/jsps/cart/list.jsp";
	}
}
