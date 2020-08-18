package com.qdm.cs.usermanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.qdm.cs.usermanagement.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

	Category findByCategoryId(int categoryData);

}
