package practice.exercise.controller;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import practice.exercise.entity.Player;
import practice.exercise.exceptionHandler.IncorrectParameterException;
import practice.exercise.exceptionHandler.IncorrectPlayerException;
import practice.exercise.exceptionHandler.ValidatePlayer;
import practice.exercise.exceptionHandler.ValidatePlayerBuilder;
import practice.exercise.service.PlayerService;

@RestController
@RequestMapping("/players")
public class PlayerController {

	@Autowired
	private PlayerService playerService;

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public BodyBuilder createPlayer(@RequestBody Optional<Player> player) throws URISyntaxException {
		long playerId = -1;
		if (player.isPresent()) {
			playerId = playerService.createPlayer(player.get());
		}
		player.orElseThrow(() -> new IncorrectPlayerException());
		URI location = new URI("/players/" + playerId);
		return ResponseEntity.created(location);
	}

	@RequestMapping(method = RequestMethod.PUT)
	public void updatePlayer(@RequestBody Player player) {
		playerService.updatePlayer(player);
	}

	@RequestMapping(method = RequestMethod.DELETE)
	public void deletePlayer(@PathVariable("id") long playerId) {
		playerService.deletePlayer(playerId);
	}

	@RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public Collection<Player> getPlayersByName(@RequestParam("name") Optional<String> name) {
		if (name.isPresent()) {
			return playerService.getPlayersByName(name.get());
		}
		name.orElseThrow(() -> new IncorrectParameterException("name"));
		return null;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
	public Player getPlayerById(@PathVariable("id") long id) {
		return playerService.getPlayerById(id);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/overall")
	public Collection<Player> getTopPlayersOverallByPage(@RequestParam("page") Optional<Integer> page) {
		if (page.isPresent()) {
			return playerService.getTopPlayersOverallByPage(page.get());
		}
		page.orElseThrow(() -> new IncorrectParameterException("page"));
		return null;
	}

	@ExceptionHandler
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public ValidatePlayer handleException(MethodArgumentNotValidException exception) {
		return createValidationError(exception);
	}

	private ValidatePlayer createValidationError(MethodArgumentNotValidException exception) {
		return ValidatePlayerBuilder.fromBindingErrors(exception.getBindingResult());
	}
}
