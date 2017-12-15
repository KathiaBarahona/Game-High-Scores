package practice.exercise.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Ranking {
	@Id
	private long playerId;
	private String playerName;
	private long overallLevel;
	private long overallExperience;
	public Ranking() {
		super();
	}
	public Ranking(long playerId, String playerName, long overallLevel, long overallExperience) {
		super();
		this.playerId = playerId;
		this.playerName = playerName;
		this.overallLevel = overallLevel;
		this.overallExperience = overallExperience;
	}
	public long getPlayerId() {
		return playerId;
	}
	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}
	public String getPlayerName() {
		return playerName;
	}
	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
	public long getOverallLevel() {
		return overallLevel;
	}
	public void setOverallLevel(Integer overallLevel) {
		this.overallLevel = overallLevel;
	}
	public long getOverallExperience() {
		return overallExperience;
	}
	public void setOverallExperience(Integer overallExperience) {
		this.overallExperience = overallExperience;
	}
	
}
