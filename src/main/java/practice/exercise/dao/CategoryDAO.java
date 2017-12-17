package practice.exercise.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import practice.exercise.entity.Category;
import practice.exercise.entity.CompositeCategoryId;
import practice.exercise.entity.Player;



@Transactional
@Repository
public class CategoryDAO implements ICategoryDAO{
	@PersistenceContext	
	private EntityManager entityManager;
	
	@Autowired
	private PlayerDAO playerDAO;
	/*
	 * createCategory method adds a new category to the specified player
	 * @method createCategory
	 * @param {long} playerId,
	 * @param {Category} category
	 * @return {boolean}
	 * */
	@Override
	public boolean createCategory(long playerId, Category category) {
		Player p = playerDAO.getPlayerById(playerId);
		if(!Objects.isNull(p)) {
			if(Objects.isNull(this.getCategory(playerId, category.getName()))) {
				category.setPlayer(p,false);
				entityManager.persist(category);
				entityManager.flush();
				return true;
			}
		}
		return false;
		
	}
	/*
	 * updateCategory is in charge of updating the experience and level values of a category 
	 * @method updateCategory
	 * @param {long} playerId,
	 * @param {Category} category
	 * @return {boolean}
	 * */
	@Override
	public boolean updateCategory(long playerId, Category category) {
		Category oldCategory = this.getCategory(playerId, category.getName());
		if(!Objects.isNull(oldCategory)) {
			if(!Objects.isNull(category.getExperience())) {
				oldCategory.setExperience(category.getExperience());
			}
			if(!Objects.isNull(category.getLevel())) {
				oldCategory.setLevel(category.getLevel());
			}
			entityManager.flush();
			return true;
		}
		return false;
	}
	/*
	 * getCategory is in charge of getting a category 
	 * @method getCategory
	 * @param {long} playerId,
	 * @param {String} name
	 * @return {Category}
	 * */
	@Override
	public Category getCategory(long playerId, String name) {
		Player p = playerDAO.getPlayerById(playerId);
		if(!Objects.isNull(p)) {
			return entityManager.find(Category.class, new CompositeCategoryId(name,p));
		}
		return null;
	}
	/*
	 * deleteCategory is in charge of deleting category 
	 * @method deleteCategory
	 * @param {long} playerId,
	 * @param {String} name
	 * @return {boolean}
	 * */
	@Override
	public boolean deleteCategory(long playerId,String name) {
		Player p = playerDAO.getPlayerById(playerId);
		if(!Objects.isNull(p)) {
			List<Category> categories = p.getCategories();
			for(int c = 0; c < categories.size(); c++) {
				if(categories.get(c).getName().equals(name)) {
					entityManager.remove(categories.get(c));
					p.getCategories().remove(c);
					entityManager.flush();
					return true;
				}
			}
			
		}
		return false;
	}
	@Override
	public Collection<Category> getAllCategories(long playerId) {
		Player p = playerDAO.getPlayerById(playerId);
		if(Objects.isNull(p)) {
			return new ArrayList<>();
		}
		return p.getCategories();
	}
}
