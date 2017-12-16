package practice.exercise.dao;

import java.util.Collection;

import practice.exercise.entity.Player;

public interface IPlayerDAO {
	Long createPlayer(Player player); 
	boolean updatePlayer(Player player,long id);
	boolean deletePlayer(long playerId);
	void deleteAllPlayers();
	Player getPlayerById(long id);
	Collection<Player> getPlayersByName(String name);
	Collection<Player> getTopPlayersOverallByPage(int page); 
}
