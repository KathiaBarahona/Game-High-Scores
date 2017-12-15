package practice.exercise.exceptionHandler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IncorrectPlayerException extends RuntimeException {
	public IncorrectPlayerException() {
		super("The informed player is missing properties.");
	}

}
