package practice.exercise;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

import practice.exercise.controller.PlayerController;
import practice.exercise.entity.Category;
import practice.exercise.entity.Player;
import practice.exercise.service.PlayerService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = HighScoreBoardApiApplication.class)
@WebAppConfiguration
public class PlayerControllerTest { 
	private MediaType contentType = MediaType.APPLICATION_JSON_UTF8;

	private MockMvc mockMvc;
	private String baseURL = "/players";

	@Mock
	private PlayerService playerService;
	
	@InjectMocks
	private PlayerController playerController;

	
	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		this.mockMvc  = MockMvcBuilders.standaloneSetup(playerController).build();	
	}
	//********************************** CREATE TEST ***********************************************************
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
	                .content(this.json(new Player(""))))
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
		playerCreate.setId(1);
		when(playerService.createPlayer(playerCreate)).thenReturn((long)1);
		mockMvc.perform(post(baseURL)
				.contentType(contentType)
				.content(this.json(playerCreate)))
    			.andExpect(status().isCreated())
    			.andExpect(header().string("location",containsString("http://localhost/players/")));
		verify(playerService,times(1)).createPlayer(playerCreate);
		verifyNoMoreInteractions(playerService);
	}
	//********************************** UPDATE TEST ******************************************************
	@Test
	public void updateNonExistentPlayer() throws Exception{
		String url = baseURL+"/{id}";
		Player player = new Player("Kats");
		player.setId(100);
		when(playerService.updatePlayer(player, 100)).thenReturn(false);
		mockMvc.perform(put(url,100)
				.contentType(contentType)
				.content(this.json(player)))
				.andExpect(status().isNotFound());
		verify(playerService,times(1)).updatePlayer(player, 100);
		verifyNoMoreInteractions(playerService);
	}
	@Test
	public void updatePlayerWithEmptyName() throws Exception{
		Player player = new Player("");
		player.setId(1);
		String url = baseURL+"/{id}";
		mockMvc.perform(put(url,1)
				.content(this.json(player))
				.contentType(contentType))
				.andExpect(status().isBadRequest());
	}
	@Test
	public void updatePlayer() throws Exception{
		Player player = new Player("Kats Update");
		player.setId(1);
		when(playerService.updatePlayer(player, 1)).thenReturn(true);
		when(playerService.getPlayerById(1)).thenReturn(player);
		String url = baseURL+"/{id}";
		mockMvc.perform(put(url,1)
				.contentType(MediaType.APPLICATION_JSON)
				.content(this.json(player)))
				.andExpect(status().isOk());
		verify(playerService,times(1)).updatePlayer(player, 1);
		verify(playerService,times(1)).getPlayerById(1);
		verifyNoMoreInteractions(playerService);
	}
	/******************************** DELETE TEST ********************************/
	@Test
	public void deleteNonExistentPlayer() throws Exception{
		String url = baseURL+"/{id}";
		when(playerService.deletePlayer(100)).thenReturn(false);
		mockMvc.perform(delete(url,100)
				.contentType(contentType))
				.andExpect(status().isNotFound());
		verify(playerService,times(1)).deletePlayer(100);
		verifyNoMoreInteractions(playerService);
	} 
	@Test
	public void deletePlayer() throws Exception{
		String url = baseURL+"/{id}";
		when(playerService.deletePlayer(1)).thenReturn(true);
		mockMvc.perform(delete(url,1))
				.andExpect(status().isOk());
		verify(playerService,times(1)).deletePlayer(1);
		verifyNoMoreInteractions(playerService);
	}
	
	//******************************** LIST TEST ***********************************************//
	@Test
	public void getPlayerById() throws Exception{
		String url = baseURL+"/{id}";
		Player player = new Player("Tom");
		player.setId(1);
		when(playerService.getPlayerById(player.getId())).thenReturn(player);
		mockMvc.perform(get(url,player.getId()))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$.id",is(1)))
				.andExpect(jsonPath("$.name",is("Tom")));
		verify(playerService,times(1)).getPlayerById(player.getId());
		verifyNoMoreInteractions(playerService); 
	}
	@Test 
	public void getNonExistentPlayerById() throws Exception{
		String url = baseURL+"/{id}";
		when(playerService.getPlayerById(1)).thenReturn(null);
		mockMvc.perform(get(url,1))
		       .andExpect(status().isNotFound());
		verify(playerService,times(1)).getPlayerById(1); 
		verifyNoMoreInteractions(playerService);
	}
	@Test
	public void getPlayerByName() throws Exception{
		String url = baseURL+"?name=Tom";
		Player player = new Player("Tom");
		player.setId(1);
		when(playerService.getPlayersByName("Tom")).thenReturn(Arrays.asList(player));
		mockMvc.perform(get(url))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$",hasSize(1)))
				.andExpect(jsonPath("$[0].id",is(1)))
				.andExpect(jsonPath("$[0].name",is("Tom")));
		verify(playerService,times(1)).getPlayersByName("Tom");
		verifyNoMoreInteractions(playerService); 
	}
	@Test 
	public void getNonExistentPlayerByName() throws Exception{
		String url = baseURL+"?name=Tom";
		when(playerService.getPlayersByName("Tom")).thenReturn(null);
		mockMvc.perform(get(url))
		       .andExpect(status().isNoContent());
		verify(playerService,times(1)).getPlayersByName("Tom");
		verifyNoMoreInteractions(playerService);
	}
	@Test
	public void getTopPlayersWithNoParameters() throws Exception{
		mockMvc.perform(get("/players/ranking")
				.contentType(contentType))
				.andExpect(status().isBadRequest());
	}
	@Test
	public void getTopPlayersByCategory () throws Exception{
		Collection<Player> players = Arrays.asList(
					new Player("Kats"),
					new Player("Tom")
				);
		
		players.forEach(player -> {
			player.setCategories(Arrays.asList(new Category("Attack",0,0)));
			player.setId(player.getName().equals("Kats")?1:2);
		});
		when(playerService.getTopPlayersByCategory("Attack", 1)).thenReturn(players);
		mockMvc.perform(get("/players/ranking?category=Attack&page=1"))
		               .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
					   .andExpect(status().isOk())
					   .andExpect(jsonPath("$",hasSize(2)))
					   .andExpect(jsonPath("$[0].id",is(1)))
					   .andExpect(jsonPath("$[0].name",is("Kats")))
					   .andExpect(jsonPath("$[1].id",is(2)))
					   .andExpect(jsonPath("$[1].name",is("Tom")));
		verify(playerService,times(1)).getTopPlayersByCategory("Attack", 1); 
		verifyNoMoreInteractions(playerService);
							 
	}
	@Test
	public void getTopPlayersByEmptyCategory () throws Exception{
		mockMvc.perform(get("/players/ranking?category=Overall&page=1")
				.contentType(contentType))
				.andExpect(status().isNoContent());
	}
	protected String json(Object o) throws IOException {
		 ObjectMapper mapper = new ObjectMapper();
		    mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		    String requestJson=ow.writeValueAsString(o);
        return requestJson;
    }

}
