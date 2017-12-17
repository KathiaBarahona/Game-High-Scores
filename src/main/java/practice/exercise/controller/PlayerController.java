package practice.exercise.controller;

import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import practice.exercise.entity.Player;
import practice.exercise.exceptionHandler.IncorrectParameterException;
import practice.exercise.exceptionHandler.IncorrectPlayerException;
import practice.exercise.exceptionHandler.ValidateError;
import practice.exercise.exceptionHandler.ValidateErrorBuilder;
import practice.exercise.service.PlayerService;

@RestController
@RequestMapping("/players")
public class PlayerController {


    private final Logger LOG = LoggerFactory.getLogger(PlayerController.class);

	@Autowired
	private PlayerService playerService;
	
	
//********************************POST REQUEST**************************************************
	
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<ValidateError> createPlayer(@Valid @RequestBody Optional<Player> player, Errors errors, UriComponentsBuilder ucBuilder) throws URISyntaxException {
		LOG.info("Creating a new player...");
		if (errors.hasErrors()) {
			LOG.info("The following errors were found: {}",errors);
			return new ResponseEntity<ValidateError>(ValidateErrorBuilder.fromBindingErrors(errors),HttpStatus.BAD_REQUEST);
        }
		long playerId = -1;
		if (player.isPresent()) {
			Player p = player.get();
			p.getCategories().forEach(category-> category.setPlayer(p,false));
			playerId = playerService.createPlayer(p);
		}
		player.orElseThrow(() -> new IncorrectPlayerException());
		if(playerId != -1) {
			LOG.info("The player {} was created",player.get());
			HttpHeaders headers = new HttpHeaders();
			headers.setLocation(ucBuilder.path("players/{id}").buildAndExpand(playerId).toUri());
			return new ResponseEntity<ValidateError>(headers,HttpStatus.CREATED);
		}
		LOG.info("The player wasn't created");
		return new ResponseEntity<ValidateError>(HttpStatus.BAD_REQUEST);
	}
//*******************************************UPDATE REQUEST***************************************************
	@RequestMapping(method = RequestMethod.PUT, value="{id}")
	public ResponseEntity<Object> updatePlayer(@RequestBody Optional<Player> player,@PathVariable("id") long id, Errors errors) {
		LOG.info("Player with id {} is to be updated ",id);
		if (errors.hasErrors()) {
			LOG.info("The following errors where found: "+errors);
			return new ResponseEntity<Object>(ValidateErrorBuilder.fromBindingErrors(errors),HttpStatus.BAD_REQUEST);
        }
		if (player.isPresent()) {
			Player p = player.get();
			if(Objects.isNull(p.getName()) || p.getName().equals("")) {
				LOG.info("The player's name property is empty");
				throw new IncorrectParameterException("name");
			}
			boolean updated = playerService.updatePlayer(player.get(),id); 
			if(!updated) {
				LOG.info("Player with id {} not found.", id);
				return new ResponseEntity<Object>(HttpStatus.NOT_FOUND);
			}
			Player updatedPlayer = playerService.getPlayerById(id);
			return new ResponseEntity<Object>(updatedPlayer,HttpStatus.OK);
		}
		LOG.info("The informed player isn't valid");
		player.orElseThrow(() -> new IncorrectPlayerException());
		return new ResponseEntity<Object>(HttpStatus.BAD_REQUEST);
	}
//**************************************DELETE REQUESTS****************************************************
	@RequestMapping(method = RequestMethod.DELETE, value="{id}")
	public ResponseEntity<Void> deletePlayer(@PathVariable("id") long playerId) {
		LOG.info("Player with id {} is to be deleted",playerId);
		boolean deleted = playerService.deletePlayer(playerId);
		if(deleted) {
			LOG.info("Player with id {} was deleted",playerId);
			return new ResponseEntity<Void>(HttpStatus.OK);
		}
		LOG.info("Player with id {} not found",playerId);
		return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
	}
//****************************************GET REQUESTS****************************************************
	
	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Collection<Player>> getPlayersByName(@RequestParam("name") Optional<String> name) {
		LOG.info("Getting players by name {}");
		if (name.isPresent()) {
			Collection<Player> player = playerService.getPlayersByName(name.get());
			if(player == null || player.size() == 0) {
				LOG.info("No player where found for name {} ",name.get());
				return new ResponseEntity<Collection<Player>>(HttpStatus.NO_CONTENT);
			}
			return new ResponseEntity<Collection<Player>>(player,HttpStatus.OK);
		}
		LOG.info("The request is missing the name parameter.");
		name.orElseThrow(() -> new IncorrectParameterException("name"));
		return new ResponseEntity<Collection<Player>>(HttpStatus.BAD_REQUEST);
	}

	@RequestMapping(value = "{id}",method = RequestMethod.GET)
	public ResponseEntity<Player> getPlayerById(@PathVariable("id") long id) {
		LOG.info("Getting a player with id: " +id);
		Player player = playerService.getPlayerById(id); 
		if(player == null) {
			LOG.info("Player with id {} not found",id);
			return new ResponseEntity<Player>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Player>(player,HttpStatus.OK);
	}

	
	@RequestMapping(method = RequestMethod.GET, value = "ranking")
	public ResponseEntity<Collection<Player>> getTopPlayersByCategory(@RequestParam("category") Optional<String> category,
			@RequestParam("page") Optional<Integer> page) {
	
		if (page.isPresent() && category.isPresent()) {
			LOG.info("Getting all top players for {} category, {} page.", category.get(),page.get());
			Collection<Player> players = playerService.getTopPlayersByCategory(category.get(), page.get());
			if(players.size() == 0) {
				LOG.info("No players found.");
				return new ResponseEntity<Collection<Player>>(HttpStatus.NO_CONTENT);
			}
			return new ResponseEntity<Collection<Player>>(players,HttpStatus.OK);
		}
		LOG.info("The request is missing some parameters");
		category.orElseThrow(() -> new IncorrectParameterException("category"));
		page.orElseThrow(() -> new IncorrectParameterException("page"));
		return new ResponseEntity<Collection<Player>>(HttpStatus.BAD_REQUEST);
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
