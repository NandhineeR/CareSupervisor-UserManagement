package com.qdm.cs.usermanagement.service;

import java.util.List;

import com.qdm.cs.usermanagement.entity.Category;

public interface CategoryService {

	Category addCategoryList(Category category);

	List<Category> getCategoryList();

}
