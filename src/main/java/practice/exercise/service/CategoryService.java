package practice.exercise.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import practice.exercise.dao.CategoryDAO;
import practice.exercise.dao.PlayerDAO;
import practice.exercise.entity.Category;

@Service
public class CategoryService {
	@Autowired
	private CategoryDAO categoryDAO;	

}
