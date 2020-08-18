package com.qdm.cs.usermanagement.serviceimpl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qdm.cs.usermanagement.entity.Category;
import com.qdm.cs.usermanagement.repository.CategoryRepository;
import com.qdm.cs.usermanagement.service.CategoryService;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

	@Autowired
	CategoryRepository categoryRepository;

	@Override
	public Category addCategoryList(Category category) {
		return categoryRepository.save(category);
	}

	@Override
	public List<Category> getCategoryList() {
		return categoryRepository.findAll();
	}
}
