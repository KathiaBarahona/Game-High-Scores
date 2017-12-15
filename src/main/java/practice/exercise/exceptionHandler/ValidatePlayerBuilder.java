package practice.exercise.exceptionHandler;

import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;

public class ValidatePlayerBuilder {

    public static ValidatePlayer fromBindingErrors(Errors errors) {
        ValidatePlayer error = new ValidatePlayer("Validation failed. " + errors.getErrorCount() + " error(s)");
        for (ObjectError objectError : errors.getAllErrors()) {
            error.addValidationError(objectError.getDefaultMessage());
        }
        return error;
    }
}