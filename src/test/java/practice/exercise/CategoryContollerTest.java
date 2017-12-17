package practice.exercise;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.hamcrest.Matchers.hasSize;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

import practice.exercise.controller.CategoryController;
import practice.exercise.entity.Category;
import practice.exercise.entity.Player;
import practice.exercise.service.CategoryService;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = HighScoreBoardApiApplication.class)
@WebAppConfiguration
public class CategoryContollerTest {
	private MediaType contentType = MediaType.APPLICATION_JSON_UTF8;
	private String baseURL = "/players/{id}/categories";
	
	private MockMvc mockMvc;

	@Mock
	private CategoryService categoryService;
	
	@InjectMocks
	private CategoryController categoryController;

	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		this.mockMvc  = MockMvcBuilders.standaloneSetup(categoryController).build();	
		 
	}
	//************************** GET TESTS*******************************************//
	@Test 
	public void getCategoriesFromNonExistentPlayer() throws Exception{
		when(categoryService.getAllCategories((long)100)).thenReturn(null);
		mockMvc.perform(get(baseURL,100))
				.andExpect(status().isNoContent());
		verify(categoryService,times(1)).getAllCategories((long)100); 
		verifyNoMoreInteractions(categoryService);
	}
	
	@Test
	public void getCategoriesFromPlayer() throws Exception{
		Player player = new Player("Kats Create Test");
		List<Category> categories = new ArrayList<Category>(
				Arrays.asList(new Category(player, "Attack", (int) (Math.random() * 10), (int) (Math.random() * 10)),
						new Category(player, "Defense", (int) (Math.random() * 10), (int) (Math.random() * 10))));
		player.setCategories(categories);
		player.setId(1);
		when(categoryService.getAllCategories(1)).thenReturn(player.getCategories());
		mockMvc.perform(get(baseURL,1))
	            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(jsonPath("$",hasSize(2)))
				.andExpect(jsonPath("$[0].name",is("Attack")))
				.andExpect(jsonPath("$[1].name",is("Defense")))
				.andExpect(status().isOk());
		verify(categoryService,times(1)).getAllCategories(1); 
		verifyNoMoreInteractions(categoryService);
	}
	
	@Test
	public void getNonExistentCategory() throws Exception{
		when(categoryService.getCategory(1, "Sleep")).thenReturn(null);
		mockMvc.perform(get(baseURL+"/{name}",1,"Sleep"))
				.andExpect(status().isNotFound());
		verify(categoryService,times(1)).getCategory(1, "Sleep");
		verifyNoMoreInteractions(categoryService);
	}
	
	@Test
	public void getCategory() throws Exception{
		Player player = new Player("Kats Create Test");
		List<Category> categories = new ArrayList<Category>(
				Arrays.asList(new Category(player, "Attack", (int) (Math.random() * 10), (int) (Math.random() * 10)),
						new Category(player, "Defense", (int) (Math.random() * 10), (int) (Math.random() * 10))));
		player.setCategories(categories);
		player.setId(1);
		when(categoryService.getCategory(1, "Attack")).thenReturn(player.getCategories().get(0));
		mockMvc.perform(get(baseURL+"/{name}",1,"Attack"))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
		    	.andExpect(status().isOk())
		    	.andExpect(jsonPath("$.name",is("Attack")));
		verify(categoryService,times(1)).getCategory(1, "Attack");
		verifyNoMoreInteractions(categoryService);
	}
	
	//************************************** POST REQUESTS *********************************************
	@Test
	public void createCategory() throws Exception{
		Category category = new Category("Sleep",0,0);
		when(categoryService.createCategory(1, category)).thenReturn(true);
		mockMvc.perform(post(baseURL,1)
				.contentType(contentType)
				.content(this.json(category)))
				.andExpect(status().isCreated());
		verify(categoryService,times(1)).createCategory(1, category);
		verifyNoMoreInteractions(categoryService);
	} 
	
	//************************************ UPDATE REQUESTS **********************************************
	@Test
	public void updateNonExistentCategory() throws Exception{
		Category category = new Category("Sleep",0,0);
		Category initial = new Category("Sleep",0,0);
		when(categoryService.updateCategory(1, category)).thenReturn(false);
		mockMvc.perform(put(baseURL+"/{name}", 1,"Sleep")
				.content(this.json(category))
				.contentType(contentType))
				.andExpect(status().isNotFound());
		verify(categoryService,times(1)).updateCategory(1, category);
		verifyNoMoreInteractions(categoryService);
	}
	@Test
	public void updateCategory() throws Exception{
		Category category = new Category("Attack",0,0);
		doReturn(true).when(categoryService).updateCategory(1, category);
		//when(categoryService.updateCategory(1, category)).thenReturn(true);
		this.mockMvc.perform(put(baseURL+"/{name}",1,"Attack")
				.content(this.json(category))
				.contentType(contentType))
				.andExpect(status().isOk()); 
		verify(categoryService,times(1)).updateCategory(1, category);
		verifyNoMoreInteractions(categoryService);
	}
	//********************************DELETE REQUESTS *****************************************
	@Test
	public void deleteNonExistentCategory() throws Exception{
		when(categoryService.deleteCategory(1, "Sleep")).thenReturn(false);
		mockMvc.perform(delete(baseURL+"/{name}",1,"Sleep")
				.contentType(contentType))
				.andExpect(status().isNotFound());
		verify(categoryService,times(1)).deleteCategory(1, "Sleep");
		verifyNoMoreInteractions(categoryService);
	}
	@Test
	public void deleteCategory() throws Exception{
		when(categoryService.deleteCategory(1, "Defense")).thenReturn(true);
		mockMvc.perform(delete(baseURL+"/{name}",1,"Defense")
				.contentType(contentType))
				.andExpect(status().isOk());
		verify(categoryService,times(1)).deleteCategory(1, "Defense");
		verifyNoMoreInteractions(categoryService);
	}
	
	
	protected String json(Object o) throws IOException {
		 ObjectMapper mapper = new ObjectMapper();
		    mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		    ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		    String requestJson=ow.writeValueAsString(o);
       return requestJson;
   }
}
