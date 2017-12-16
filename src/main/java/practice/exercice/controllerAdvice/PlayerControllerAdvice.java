package practice.exercice.controllerAdvice;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import practice.exercise.entity.Player;
import practice.exercise.exceptionHandler.ValidateError;
import practice.exercise.exceptionHandler.ValidateErrorBuilder;

@ControllerAdvice(assignableTypes = Player.class)
public class PlayerControllerAdvice  extends ResponseEntityExceptionHandler{
	@Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ValidateError error = ValidateErrorBuilder.fromBindingErrors(exception.getBindingResult());
        return super.handleExceptionInternal(exception, error, headers, status, request);
    }
}
