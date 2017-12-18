package practice.exercise.entity;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="Category")
@IdClass(CompositeCategoryId.class)
public class Category implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name="name")
	@NotBlank(message = "The category's name must not be blank!")
	private String name;
	@NotNull(message="The category must have a level")
	private Integer level;
	@NotNull(message="The category must have an experience value")
	private Integer experience;
	
	@Id	
	@ManyToOne
	@JsonIgnore
	@JoinColumn(name = "playerId", referencedColumnName = "id")
	private Player player;
		
	public Category() {
		
	}
	public Category(Player player,String name ,Integer level, Integer experience) {
		this.player = player;
		this.name = name;
		this.level = level;
		this.experience = experience;
	}
	public Category(String name ,Integer level, Integer experience) {
		this.name = name;
		this.level = level;
		this.experience = experience;
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
		setPlayer(player,true);
	}
	public void setPlayer(Player player,boolean add) {
		this.player = player;
		if(player != null && add) {
			player.addCategory(this, false);
		}
	}
	
	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}
	public Integer getExperience() {
		return experience;
	}
	public void setExperience(Integer experience) {
		this.experience = experience;
	}
	@Override
	public String toString() {
		if(!Objects.isNull(this.player)) {
			return "Category [name=" + name + ", level=" + level + ", experience=" + experience + ", player=" + player.getName() + "]";
		}
		return "Category [name=" + name + ", level=" + level + ", experience=" + experience +"]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((experience == null) ? 0 : experience.hashCode());
		result = prime * result + ((level == null) ? 0 : level.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((player == null) ? 0 : player.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Category other = (Category) obj;
		if (experience == null) {
			if (other.experience != null)
				return false;
		} else if (!experience.equals(other.experience))
			return false;
		if (level == null) {
			if (other.level != null)
				return false;
		} else if (!level.equals(other.level))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (player == null) {
			if (other.player != null)
				return false;
		} else if (!player.equals(other.player))
			return false;
		return true;
	}
	
	
}
