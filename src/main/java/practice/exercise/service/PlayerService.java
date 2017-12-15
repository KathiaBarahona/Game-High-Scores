package practice.exercise.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import practice.exercise.dao.PlayerDAO;
import practice.exercise.entity.Category;
import practice.exercise.entity.Player;

@Service
public class PlayerService {
	@Autowired
	private PlayerDAO playerDAO;
	
	public Long createPlayer(Player player) {
		return playerDAO.createPlayer(player);
	}
	public void updatePlayer(Player player) { 
		playerDAO.updatePlayer(player);
	}
	public void deletePlayer(long id) {
		playerDAO.deletePlayer(id);
	}
	public Player getPlayerById(long id) {
		return playerDAO.getPlayerById(id);
	}
	public Collection<Player> getPlayersByName(String name){
		return playerDAO.getPlayersByName(name);
	}
	public Collection<Player> getTopPlayersOverallByPage(int page){
		return playerDAO.getTopPlayersOverallByPage(page);
	}	
}
