package practice.exercise.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
	@RequestMapping(method = RequestMethod.GET, value="/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
	public Category getCategory(@PathVariable("id") long playerId, @PathVariable("name") String name) {
		return categoryService.getCategory(playerId,name);
		
	}
	
	@RequestMapping(method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity createCategory(@PathVariable("id") long playerId, @Valid @RequestBody Optional<Category> category, Errors errors) throws URISyntaxException {
		boolean created = false;
		if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(ValidateErrorBuilder.fromBindingErrors(errors));
        }
		if(category.isPresent()) {
			created = categoryService.createCategory(playerId, category.get());
		}
		category.orElseThrow(() -> new IncorrectPlayerException());
		if(created) {
			URI location = new URI("/players/" + playerId+"/categories/"+category.get().getName()); 
			return ResponseEntity.created(location).body("Category was added");
		}
		return ResponseEntity.badRequest().body("Not added.");
		
	}
	@RequestMapping(method = RequestMethod.PUT,value="/{name}",consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity updateCategory(@PathVariable("id") long playerId, @PathVariable("name")String name, @RequestBody Optional<Category> category, Errors errors) {
		boolean updated = false;
		if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(ValidateErrorBuilder.fromBindingErrors(errors));
        }
		if(category.isPresent()) {
			Category category2= category.get();
			category2.setName(name);
			updated = categoryService.updateCategory(playerId, category2);
		}
		category.orElseThrow(() -> new IncorrectPlayerException());
		if(updated) {
			return ResponseEntity.ok().body("Category was updated.");
		}
		return ResponseEntity.badRequest().body("Category wasn't updated");
	}
	@RequestMapping(method = RequestMethod.DELETE,value="/{name}",consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity deleteCategory(@PathVariable("id") long playerId, @PathVariable("name") String name) {
		boolean deleted = categoryService.deleteCategory(playerId, name);
		if(deleted) {
			return ResponseEntity.ok().body("Category was deleted");
		}
		return ResponseEntity.badRequest().body("Category wasn't deleted");
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
