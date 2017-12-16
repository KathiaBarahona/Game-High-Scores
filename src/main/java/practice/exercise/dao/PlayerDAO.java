package practice.exercise.dao;

import java.util.Collection;
import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import practice.exercise.entity.Player;

@Transactional
@Repository
public class PlayerDAO implements IPlayerDAO {
	@PersistenceContext	
	private EntityManager entityManager;
	/*
	 * createPlayer method is in charge of adding a new player to the db
	 * @method createPlayer
	 * @param {Player} player
	 * @return {}
	 * */
	@Override
	public Long createPlayer(Player player) {
		entityManager.persist(player);
		entityManager.flush();
		return player.getId();
	}
	/*
	 * updatePlayer method is in charge of updating an existing player from the db
	 * @method updatePlayer
	 * @param {Player} player
	 * @return {}
	 * */
	@Override
	public boolean updatePlayer(Player player,long id) {
		Player oldPlayer = this.getPlayerById(id);
		if(!Objects.isNull(oldPlayer)) {
			oldPlayer.setName(player.getName()); 
			entityManager.flush();
			return true;
		}
		return false;
	}
	/*
	 * deletePlayer method is in charge of delete an existing player from the db
	 * @method deletePlayer
	 * @param {long} id
	 * */
	@Override 
	public boolean deletePlayer(long id) {
		Player player = getPlayerById(id);
		if(!Objects.isNull(player)) {
			entityManager.remove(player);
			return true;
		}
		return false;
	}
	/*TODO
	 * deleteAllPlayers method is in charge of delete all existing player from the db
	 * @method deleteAllPlayers
	 * 
	 * */
	@Override 
	public void deleteAllPlayers() {
		entityManager.remove(Player.class);
	}
	/*
	 * getPlayerById method retrieves an specific player from the db
	 * @method getPlayerById
	 * @param {long} id
	 * @return {Player}
	 * */
	@Override
	public Player getPlayerById(long id) {
		return entityManager.find(Player.class, id);
	}
	/*
	 * getPlayersByName method that searches users by name
	 * @method getPlayersByName
	 * @param {String} name
	 * @return {List}
	 * */
	@Override
	@SuppressWarnings("unchecked")
	public Collection<Player> getPlayersByName(String name){
		String hql = "FROM Player as player WHERE player.name = '"+name+"'";
		return entityManager.createQuery(hql).getResultList();
	}
	/*
	 * getTopPlayersOverallByPage method gets the players with the highest scores or experience
	 * @method getTopPlayersOverallByPage
	 * @param {Integer} page
	 * @return {Collection<Player>}
	 * */
	@Override
	@SuppressWarnings("unchecked")
	public Collection<Player> getTopPlayersByCategory(String category,int page){		
		int limit = 10;
		String hql = "SELECT new Ranking(player.id AS playerId, player.name AS playerName, SUM(category.level) AS overallLevel, "+
		"SUM(category.experience) AS overallExperience)"+
		" FROM Player AS player INNER JOIN player.categories AS category ";
		if(!category.equals("Overall")) {
			hql += " WHERE category.name = :category";
		}
		hql+=" GROUP BY player.id,player.name ORDER BY overallExperience DESC";
		Query query = entityManager.createQuery(hql);
		if(!category.equals("Overall")) {
			query.setParameter("category", category);
		}
		return query.setFirstResult((page-1)*limit).setMaxResults(limit).getResultList();
	}
}
