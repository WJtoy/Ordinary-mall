package com.item.category.service;

import java.util.List;

import com.item.category.dao.CategoryDao;
import com.item.category.domain.Category;

public class CategoryService {
	private CategoryDao categoryDao=new CategoryDao(); 
	
	/*
	 * 查询所有分类
	 */
	public List<Category> findAll(){
		return categoryDao.findAll();
	}
}
