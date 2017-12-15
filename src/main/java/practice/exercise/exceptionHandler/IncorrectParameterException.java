package practice.exercise.exceptionHandler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IncorrectParameterException extends RuntimeException  {
	public IncorrectParameterException(String param) {
		super("Please include the "+param+" parameter.");
	}
}
