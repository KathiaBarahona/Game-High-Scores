package practice.exercise.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import practice.exercise.entity.Category;



@Transactional
@Repository
public class CategoryDAO implements ICategoryDAO{
	@PersistenceContext	
	private EntityManager entityManager;
	
	@Override
	public void createCategory(Category category) {
		entityManager.persist(category);
		entityManager.flush();
	}
}
