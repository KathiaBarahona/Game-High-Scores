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
	public boolean equals(Object object) {
        if (object == this)
            return true;
        if ((object == null) || !(object instanceof Category))
            return false;
 
        final Category category = (Category)object;
 
        if (player != null && category.getPlayer() != null ) {
            return player.equals(category.getPlayer()) && name == category.getName();
        }else {
        	return name == category.getName();
        }
    }
	public int hashCode() {
		if(Objects.isNull(player)) {
			return name.hashCode();
		}
		return name.hashCode() + player.hashCode();
	}
	
	
}
