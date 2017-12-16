package practice.exercise.dao;

import java.util.List;

import practice.exercise.entity.Category;
import practice.exercise.entity.Player;

public interface ICategoryDAO {
	boolean createCategory(long playerId,Category category);
	boolean updateCategory(long playerId,Category category);
	Category getCategory(long playerId,String name);
	boolean deleteCategory(long playerId,String name);
}
