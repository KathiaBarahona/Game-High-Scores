package practice.exercise.dao;

import java.util.Collection;


import practice.exercise.entity.Category;


public interface ICategoryDAO {
	boolean createCategory(long playerId,Category category);
	boolean updateCategory(long playerId,Category category);
	Category getCategory(long playerId,String name);
	boolean deleteCategory(long playerId,String name);
	Collection<Category> getAllCategories(long playerId);
}
