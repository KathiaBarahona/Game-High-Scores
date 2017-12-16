package practice.exercise;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

import practice.exercise.controller.PlayerController;
import practice.exercise.dao.PlayerDAO;
import practice.exercise.entity.Category;
import practice.exercise.entity.Player;
import practice.exercise.service.PlayerService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = HighScoreBoardApiApplication.class)
@WebAppConfiguration
public class PlayerControllerTest { 
	private MediaType contentType = MediaType.APPLICATION_JSON_UTF8;

	private MockMvc mockMvc;
	private String playerName = "kats";
	private Player player;
	private String baseURL = "/players";

	@Mock
	private PlayerService playerService;
	@InjectMocks
	private PlayerController playerController;
	
	@Autowired
	private PlayerDAO playerDAO;

	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		this.mockMvc  = MockMvcBuilders.standaloneSetup(playerController).build();
	
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
		mockMvc.perform(post(baseURL)
				.contentType(contentType)
				.content(this.json("{}")))
		        .andExpect(status().isBadRequest());
	}
	@Test	
	public void createPlayerWithNoName() throws Exception{
		 mockMvc.perform(post(baseURL)
				 	.contentType(contentType)
	                .content(this.json(new Player())))
	                .andExpect(status().isBadRequest());
	}
	
	@Test 
	public void createPlayerWithNoCategories() throws Exception{
		mockMvc.perform(post(baseURL)
			    .contentType(contentType)
			    .content(this.json(new Player("Kats2"))))
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
		mockMvc.perform(post(baseURL)
				.contentType(contentType)
				.content(this.json(playerCreate)))
    			.andExpect(status().isCreated());
	}
	@Test
	public void updateNonExistentPlayer() throws Exception{
		String url = baseURL+"/{id}";
		mockMvc.perform(put(url,100)
				.contentType(contentType)
				.content(this.json(this.player)))
				.andExpect(status().isNotFound());
	}
	@Test
	public void updatePlayerWithEmptyName() throws Exception{
		String url = baseURL+"/{id}";
		mockMvc.perform(put(url,this.player.getId())
				.content(this.json(new Player("")))
				.contentType(contentType))
				.andExpect(status().isBadRequest());
	}
	@Test
	public void updatePlayer() throws Exception{
		this.player.setName("Kats Udpdate");
		when(playerService.getPlayerById(this.player.getId())).thenReturn(this.player);
		doNothing().when(playerService).updatePlayer(this.player, this.player.getId());
		String url = baseURL+"/{id}";
		mockMvc.perform(put(url,this.player.getId())
				.contentType(contentType)
				.content(this.json(this.player)))
				.andExpect(status().isOk());
	}
	@Test
	public void deleteNonExistentPlayer() throws Exception{
		String url = baseURL+"/{id}";
		mockMvc.perform(delete(url,100)
				.contentType(contentType))
				.andExpect(status().isNotFound());
	}
	@Test
	public void deletePlayer() throws Exception{
		String url = baseURL+"/{id}";
		mockMvc.perform(delete(url,this.player.getId())
				.contentType(contentType))
				.andExpect(status().isOk());
	}
	
	@Test
	public void getPlayerById() throws Exception{
		String url = baseURL+"/{id}";
		Player player = new Player("Tom");
		player.setId(1);
		when(playerService.getPlayerById(player.getId())).thenReturn(player);
		mockMvc.perform(get(url,this.player.getId()))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(jsonPath("$.id",is(1)))
				.andExpect(jsonPath("$.name",is("Tom")));
		verify(playerService,times(1)).getPlayerById(player.getId());
		verifyNoMoreInteractions(playerService); 
	}
	@Test
	public void getTopPlayersOverallWithNoParameters() throws Exception{
		mockMvc.perform(get("/players/ranking")
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
		 ObjectMapper mapper = new ObjectMapper();
		    mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		    String requestJson=ow.writeValueAsString(o);
        return requestJson;
    }

}
