package practice.exercise.dao;

import java.util.Collection;

import practice.exercise.entity.Player;

public interface IPlayerDAO {
	Long createPlayer(Player player); 
	boolean updatePlayer(Player player,long id);
	boolean deletePlayer(long playerId);
	Player getPlayerById(long id);
	Collection<Player> getPlayersByName(String name);
	Collection<Player> getTopPlayersByCategory(String category,int page); 
}
