package practice.exercise.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import practice.exercise.dao.PlayerDAO;
import practice.exercise.entity.Player;

@Service
public class PlayerService {
	@Autowired
	private PlayerDAO playerDAO;
	
	public Long createPlayer(Player player) {
		return playerDAO.createPlayer(player);
	}
	public boolean updatePlayer(Player player,long id) { 
		return playerDAO.updatePlayer(player,id);
	}
	public boolean deletePlayer(long id) {
		return playerDAO.deletePlayer(id);
	}

	public Player getPlayerById(long id) {
		return playerDAO.getPlayerById(id);
	}
	public Collection<Player> getPlayersByName(String name){
		return playerDAO.getPlayersByName(name);
	}
	public Collection<Player> getTopPlayersByCategory(String category,int page){
		return playerDAO.getTopPlayersByCategory(category,page);
	}	
}
