package practice.exercise;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import practice.exercise.dao.PlayerDAO;
import practice.exercise.entity.Category;
import practice.exercise.entity.Player;
import practice.exercise.service.PlayerService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = HighScoreBoardApiApplication.class)
@WebAppConfiguration
public class PlayerControllerTest { 
	private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

	private MockMvc mockMvc;
	private String playerName = "kats";
	private Player player;
	@Autowired
	private WebApplicationContext webApplicationContext;
	private HttpMessageConverter mappingJackson2HttpMessageConverter;
	@Autowired
	private PlayerService playerService;
	@Autowired
	private PlayerDAO playerDAO;

	@Autowired
	void setConverters(HttpMessageConverter<?>[] converters) {

		this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream()
				.filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter).findAny().orElse(null);

		assertNotNull("the JSON message converter must not be null", this.mappingJackson2HttpMessageConverter);
	}

	@Before
	public void setup() throws Exception {
		this.mockMvc = webAppContextSetup(webApplicationContext).build();
		//this.playerService.deleteAllPlayers();
		player = new Player(this.playerName);
		List<Category> categories = new ArrayList<Category>(
				Arrays.asList(new Category(player, "Attack", (int) (Math.random() * 10), (int) (Math.random() * 10)),
						new Category(player, "Defense", (int) (Math.random() * 10), (int) (Math.random() * 10)),
						new Category(player, "Magic", (int) (Math.random() * 10), (int) (Math.random() * 10)),
						new Category(player, "Cooking", (int) (Math.random() * 10), (int) (Math.random() * 10)),
						new Category(player, "Crafting", (int) (Math.random() * 10), (int) (Math.random() * 10))));
		player.setCategories(categories);
		playerDAO.createPlayer(player);
		
	}
	@Test
	public void createEmptyPlayer() throws Exception{
		mockMvc.perform(post("/players")
				.content(this.json("{}"))
				.contentType(contentType))
		        .andExpect(status().isBadRequest());
	}
	@Test	
	public void createPlayerWithNoName() throws Exception{
		 mockMvc.perform(post("/players")
	                .content(this.json(new Player()))
	                .contentType(contentType))
	                .andExpect(status().isBadRequest());
	}
	
	@Test 
	public void createPlayerWithNoCategories() throws Exception{
		mockMvc.perform(post("/players")
			    .content(this.json(new Player("Kats2")))
			    .contentType(contentType))
			    .andExpect(status().isBadRequest());
	}
	@Test
	public void createPlayer() throws Exception{
		Player playerCreate = new Player("Kats Create Test");
		List<Category> categories = new ArrayList<Category>(
				Arrays.asList(new Category(playerCreate, "Attack", (int) (Math.random() * 10), (int) (Math.random() * 10)),
						new Category(playerCreate, "Defense", (int) (Math.random() * 10), (int) (Math.random() * 10)),
						new Category(playerCreate, "Magic", (int) (Math.random() * 10), (int) (Math.random() * 10)),
						new Category(playerCreate, "Cooking", (int) (Math.random() * 10), (int) (Math.random() * 10)),
						new Category(playerCreate, "Crafting", (int) (Math.random() * 10), (int) (Math.random() * 10))));
		playerCreate.setCategories(categories);
		mockMvc.perform(post("/players")
				.content(this.json(playerCreate))
				.contentType(contentType))
    			.andExpect(status().isCreated());
	}
	@Test
	public void updateNonExistentPlayer() throws Exception{
		mockMvc.perform(put("/players/100")
				.content(this.json(this.player))
				.contentType(contentType))
				.andExpect(status().isNotFound());
	}
	@Test
	public void updatePlayerWithEmptyName() throws Exception{
		mockMvc.perform(put("/players/1")
				.content(this.json(new Player("")))
				.contentType(contentType))
				.andExpect(status().isBadRequest());
	}
	@Test
	public void updatePlayer() throws Exception{
		mockMvc.perform(put("/players/1")
				.content(this.json(new Player("Kats Update")))
				.contentType(contentType))
				.andExpect(status().isOk());
	}
	@Test
	public void deleteNonExistentPlayer() throws Exception{
		mockMvc.perform(delete("/players/100")
				.contentType(contentType))
				.andExpect(status().isNotFound());
	}
	@Test
	public void deletePlayer() throws Exception{
		mockMvc.perform(delete("/players/1")
				.contentType(contentType))
				.andExpect(status().isOk());
	}
	
	@Test
	public void getPlayerById() throws Exception{
		mockMvc.perform(get("/players/1")
				.contentType(contentType))
				.andExpect(status().isOk());
	}
	@Test
	public void getTopPlayersOverallWithNoParameters() throws Exception{
		mockMvc.perform(get("/players/overall")
				.contentType(contentType))
				.andExpect(status().isBadRequest());
	}
	@Test
	public void getTopPlayersOverall() throws Exception{
		mockMvc.perform(get("/players/ranking?category=Overall&page=1")
				.contentType(contentType))
				.andExpect(status().isOk());
	}
	@Test
	public void getTopPlayersAttack() throws Exception{
		mockMvc.perform(get("/players/ranking?category=Attack&page=1")
				.contentType(contentType))
				.andExpect(status().isOk());
	}
	@Test
	public void getTopPlayersDefense() throws Exception{
		mockMvc.perform(get("/players/ranking?category=Defense&page=1")
				.contentType(contentType))
				.andExpect(status().isOk());
	}
	@Test
	public void getTopPlayersMagic() throws Exception{
		mockMvc.perform(get("/players/ranking?category=Magic&page=1")
				.contentType(contentType))
				.andExpect(status().isOk());
	}
	@Test
	public void getTopPlayersCooking() throws Exception{
		mockMvc.perform(get("/players/ranking?category=Cooking&page=1")
				.contentType(contentType))
				.andExpect(status().isOk());
	}
	@Test
	public void getTopPlayersCrafting() throws Exception{
		mockMvc.perform(get("/players/ranking?category=Crafting&page=1")
				.contentType(contentType))
				.andExpect(status().isOk());
	}
	protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

}
