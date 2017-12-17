package practice.exercise.controller;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import practice.exercise.entity.Category;
import practice.exercise.exceptionHandler.IncorrectPlayerException;
import practice.exercise.exceptionHandler.ValidateError;
import practice.exercise.exceptionHandler.ValidateErrorBuilder;
import practice.exercise.service.CategoryService;

@RestController
@RequestMapping("/players/{id}/categories")
public class CategoryController {
	@Autowired 
	CategoryService categoryService;
	
	private final Logger LOG = LoggerFactory.getLogger(CategoryController.class);
	//***********************************GET REQUESTS****************************************//
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<Collection<Category>> getCategories(@PathVariable("id") long playerId) {
		LOG.info("Getting all categories for player with id {} ",playerId);
		Collection<Category> categories = categoryService.getAllCategories(playerId);
		if(categories == null || categories.isEmpty()) {
			LOG.info("No categories found for player with id {} ",playerId);
			return new ResponseEntity<Collection<Category>>(categories,HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<Collection<Category>>(categories,HttpStatus.OK);
		
	}
	
	@RequestMapping(method = RequestMethod.GET, value="{name}")
	public ResponseEntity<Category> getCategory(@PathVariable("id") long playerId, @PathVariable("name") String name) {
		Category category = categoryService.getCategory(playerId,name);
		LOG.info("Getting category {} for player with id {} ",name,playerId);
		if(!Objects.isNull(category)){
			return new ResponseEntity<Category>(category,HttpStatus.OK);
		}
		LOG.info("Category {} for player with id {} not found.",name,playerId);
		return new ResponseEntity<Category>(HttpStatus.NOT_FOUND);
		
	}
	//************************************POST REQUESTS ***********************************************//
	
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<ValidateError> createCategory(@PathVariable("id") long playerId, @Valid @RequestBody Optional<Category> category, Errors errors, UriComponentsBuilder ucBuilder)  {
		boolean created = false;
		if (errors.hasErrors()) {
			LOG.info("The following errors were found: {}",errors);
            return new ResponseEntity<ValidateError>(ValidateErrorBuilder.fromBindingErrors(errors),HttpStatus.BAD_REQUEST);
        }
		if(category.isPresent()) {
			LOG.info("Creating category {}  for player with id {}",category.get().getName(),playerId);
			created = categoryService.createCategory(playerId, category.get());
		}
		category.orElseThrow(() -> new IncorrectPlayerException());
		if(created) {
			HttpHeaders headers = new HttpHeaders();
			headers.setLocation(ucBuilder.path("players/{id}/categories/{name}").buildAndExpand(playerId,category.get().getName()).toUri());
			return new ResponseEntity<ValidateError>(headers,HttpStatus.CREATED);
		}
		return new ResponseEntity<ValidateError>(HttpStatus.BAD_REQUEST);
		
	}
	
	//******************************************PUT REQUESTS *************************************************//
	@RequestMapping(method = RequestMethod.PUT,value="{name}")
	public ResponseEntity<ValidateError> updateCategory(@PathVariable("id") long playerId, @PathVariable("name")String name, @Valid @RequestBody Optional<Category> category, Errors errors) {
	
		boolean updated = false;
		if (errors.hasErrors()) {
			LOG.info("The following errors were found: {}",errors);
            return new ResponseEntity<ValidateError>(ValidateErrorBuilder.fromBindingErrors(errors),HttpStatus.BAD_REQUEST);
        }
		if(category.isPresent()) {
			LOG.info("Updating category {}  for player with id {}",category.get().getName(),playerId);
			Category category2= category.get();
			updated = categoryService.updateCategory(playerId, category2);
		}
		category.orElseThrow(() -> new IncorrectPlayerException()); 
		if(updated) {
			return new ResponseEntity<ValidateError>(HttpStatus.OK);
		}else {
			LOG.info("Category {}  for player with id {} not found.",category.get().getName(),playerId);
		}
		return new ResponseEntity<ValidateError>(HttpStatus.NOT_FOUND);
	}
	
	//************************************DELETE REQUESTS **************************************************
	@RequestMapping(method = RequestMethod.DELETE,value="{name}")
	public ResponseEntity<Void> deleteCategory(@PathVariable("id") long playerId, @PathVariable("name") String name) {
		boolean deleted = categoryService.deleteCategory(playerId, name);
		LOG.info("Deleting category {}  for player with id {}",name,playerId);
		if(deleted) {
			return new ResponseEntity<Void>(HttpStatus.OK);
		}
		LOG.info("Couldn't category {}  for player with id {}",name,playerId);
		return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
	}
	
	
	@ExceptionHandler
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public ValidateError handleException(MethodArgumentNotValidException exception) {
		return createValidationError(exception);
	}

	private ValidateError createValidationError(MethodArgumentNotValidException exception) {
		return ValidateErrorBuilder.fromBindingErrors(exception.getBindingResult());
	}
}
