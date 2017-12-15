package practice.exercise;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import practice.exercise.dao.CategoryDAO;
import practice.exercise.dao.PlayerDAO;
import practice.exercise.entity.Category;
import practice.exercise.entity.Player;

@SpringBootApplication
public class HighScoreBoardApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(HighScoreBoardApiApplication.class, args);
	}

	@Bean
	CommandLineRunner init(PlayerDAO playerDAO, CategoryDAO categoryDAO) {
		return (evt) -> Arrays.asList("jhoeller,dsyer,pwebb,ogierke,rwinch,mfisher,mpollack,jlong,kats,kbara,alazo,mpoller,rick,mike".split(","))
				.forEach(a -> {
					Player player = new Player(a);
					List<Category> categories = new ArrayList<Category>(Arrays.asList(
						new Category(player, "Attack", (int)(Math.random()*10), (int)(Math.random()*10)),
						new Category(player, "Defense", (int)(Math.random()*10), (int)(Math.random()*10)),
						new Category(player, "Magic", (int)(Math.random()*10), (int)(Math.random()*10)),
						new Category(player, "Cooking", (int)(Math.random()*10), (int)(Math.random()*10)),
						new Category(player, "Crafting", (int)(Math.random()*10), (int)(Math.random()*10))
					));
					player.setCategories(categories);
					playerDAO.createPlayer(player);
					
				});
	}

}
