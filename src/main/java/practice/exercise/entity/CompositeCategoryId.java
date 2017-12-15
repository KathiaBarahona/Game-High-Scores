package practice.exercise.entity;

import java.io.Serializable;
import java.util.Objects;


public class CompositeCategoryId implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private Player player;
	
	public CompositeCategoryId() {
		
	}
	public CompositeCategoryId(String name, Player playerId) {
		this.name = name;
		this.player = playerId;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Player getPlayer() {
		return player;
	}
	public void setPlayer(Player player) {
		this.player = player;
	}
	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CompositeCategoryId)) return false;
        CompositeCategoryId that = (CompositeCategoryId) o;
        return Objects.equals(getName(), that.getName()) && 
        		Objects.equals(getPlayer(), that.getPlayer());
    }
 
    @Override
    public int hashCode() {
        return Objects.hash(getName(),getPlayer());
    }
}
