package practice.exercise.dao;

import java.util.Collection;

import practice.exercise.entity.Player;

public interface IPlayerDAO {
	Long createPlayer(Player player);
	void updatePlayer(Player player);
	void deletePlayer(long playerId);
	Player getPlayerById(long id);
	Collection<Player> getPlayersByName(String name);
	Collection<Player> getTopPlayersOverallByPage(int page);
}
