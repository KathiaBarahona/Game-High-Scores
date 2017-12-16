package practice.exercise.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import practice.exercise.dao.CategoryDAO;
import practice.exercise.entity.Category;

@Service
public class CategoryService {
	@Autowired
	private CategoryDAO categoryDAO;	
	public boolean createCategory(long playerId,Category category) {
		return categoryDAO.createCategory(playerId, category);
	}
	public boolean updateCategory(long playerId,Category category) {
		System.out.println("Service");
		System.out.println(category);
		return categoryDAO.updateCategory(playerId, category);
	}
	public Category getCategory(long playerId,String name) {
		return categoryDAO.getCategory(playerId, name);
	}
	public boolean deleteCategory(long playerId,String name) {
		return categoryDAO.deleteCategory(playerId, name);
	}
	public Collection<Category> getAllCategories(long playerId) {
		return categoryDAO.getAllCategories(playerId);
	}

}
