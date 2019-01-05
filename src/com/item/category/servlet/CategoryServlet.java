package com.item.category.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.item.category.service.CategoryService;

import cn.itcast.servlet.BaseServlet;

/**
 * Servlet implementation class CategoryServlet
 */
@WebServlet("/CategoryServlet")
public class CategoryServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	private CategoryService categoryService=new CategoryService();
		
	
	public String findAll(HttpServletRequest arg0, HttpServletResponse arg1)
			throws ServletException, IOException {
			arg0.setAttribute("categoryList", categoryService.findAll());
			return "f:/jsps/left.jsp";
	}
}
