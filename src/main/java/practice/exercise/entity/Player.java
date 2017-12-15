package practice.exercise.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.Valid;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name="Player")
public class Player implements Serializable { 
	/**
	 *  
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	@NotBlank(message = "The player's name must not be blank!")
	private String name;
	
	@Valid
	@NotEmpty(message = "The player must have at least one category")
	@OneToMany(cascade={CascadeType.ALL}, mappedBy = "player")//1:N relationship
    private List<Category> categories = new ArrayList<>();
	
	public Player() {}
	
	public Player(String name) {
		this.name = name;
	}

	public long  getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Category> getCategories() {
		return categories;
	}
	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}
	public void addCategory(Category category) {
	    addCategory(category);
	}
	void addCategory(Category category,boolean set) {
		if (category != null) {
            getCategories().add(category);
            if (set) {
                category.setPlayer(this, false);
            }
        }
	}
	public void removeCategory(Category category) {
		getCategories().remove(category);
	    category.setPlayer(null);
	  }

	@Override
	public String toString() {
		return "Player [id=" + id + ", name=" + name + ", categories=" + categories + "]";
	}
	public boolean equals(Object object) {
        if (object == this)
            return true;
        if ((object == null) || !(object instanceof Player))
            return false;
 
        final Player player = (Player)object;
 
        if (id != 0 && player.getId() != 0) {
            return id == player.getId();
        }
        return false;
    }
	
}
