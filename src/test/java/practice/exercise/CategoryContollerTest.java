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
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import practice.exercise.dao.PlayerDAO;
import practice.exercise.entity.Category;
import practice.exercise.entity.Player;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = HighScoreBoardApiApplication.class)
@WebAppConfiguration
public class CategoryContollerTest {
	private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));
	private String baseURL = "/players/{id}/categories";
	private MockMvc mockMvc;
	private String playerName = "kats";
	private Player player;
	private Category category;
	@Autowired
	private WebApplicationContext webApplicationContext;
	private HttpMessageConverter mappingJackson2HttpMessageConverter;
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
		this.player = new Player(this.playerName);
		List<Category> categories = new ArrayList<Category>(
				Arrays.asList(new Category(player, "Attack", (int) (Math.random() * 10), (int) (Math.random() * 10)),
						new Category(player, "Defense", (int) (Math.random() * 10), (int) (Math.random() * 10)),
						new Category(player, "Magic", (int) (Math.random() * 10), (int) (Math.random() * 10)),
						new Category(player, "Cooking", (int) (Math.random() * 10), (int) (Math.random() * 10))));
		category = new Category(player, "Crafting", (int) (Math.random() * 10), (int) (Math.random() * 10));
		categories.add(category);
		this.player.setCategories(categories);
		playerDAO.createPlayer(this.player); 
	}
	@Test 
	public void getCategoriesFromNonExistentPlayer() throws Exception{
		mockMvc.perform(get(baseURL,100)
				.contentType(contentType))
				.andExpect(status().isOk());
	}
	@Test
	public void getCategoriesFromPlayer() throws Exception{
		mockMvc.perform(get(baseURL,this.player.getId())
				.contentType(contentType))
				.andExpect(status().isOk());
	}
	@Test
	public void getNonExistentCategory() throws Exception{
		mockMvc.perform(get(baseURL+"/{name}",this.player.getId(),"Sleep")
				.contentType(contentType))
				.andExpect(status().isNotFound());
	}
	@Test
	public void getCategory() throws Exception{
		mockMvc.perform(get(baseURL+"/{name}",this.player.getId(),"Attack")
				.contentType(contentType))
				.andExpect(status().isOk());
	}
	@Test
	public void createDuplicatedCategory() throws Exception{
		mockMvc.perform(post(baseURL,this.player.getId())
				.content(this.json(new Category("Attack",0,0)))
				.contentType(contentType))
				.andExpect(status().isBadRequest());
	}
	@Test
	public void createCategory() throws Exception{
		mockMvc.perform(post(baseURL,this.player.getId())
				.content(this.json(new Category("Sleep",0,0)))
				.contentType(contentType))
				.andExpect(status().isCreated());
	} 
	@Test
	public void updateNonExistentCategory() throws Exception{
		Category categoryUpdate = new Category("Sleep",0,0);
		mockMvc.perform(put(baseURL+"/{name}", this.player.getId(),"Sleep")
				.content(this.json(categoryUpdate))
				.contentType(contentType))
				.andExpect(status().isNotFound());
	}
	@Test
	public void updateCategory() throws Exception{
		this.mockMvc.perform(put(baseURL+"/{name}",this.category.getPlayer().getId(),"Attack")
				.content(this.json(new Category("Attack",0,0)))
				.contentType(contentType))
				.andExpect(status().isOk());
	}
	@Test
	public void deleteNonExistentCategory() throws Exception{
		mockMvc.perform(delete(baseURL+"/{name}",this.player.getId(),"Sleep")
				.contentType(contentType))
				.andExpect(status().isNotFound());
	}
	@Test
	public void deleteCategory() throws Exception{
		mockMvc.perform(delete(baseURL+"/{name}",this.player.getId(),"Defense")
				.contentType(contentType))
				.andExpect(status().isOk());
	}
	protected String json(Object o) throws IOException {
		 try {
		        return new ObjectMapper().writeValueAsString(o);
		    } catch (Exception e) {
		        throw new RuntimeException(e);
		    }
    }
}
