package practice.exercise.exceptionHandler;

import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;

public class ValidateErrorBuilder {

    public static ValidateError fromBindingErrors(Errors errors) {
        ValidateError error = new ValidateError("Validation failed. " + errors.getErrorCount() + " error(s)");
        for (ObjectError objectError : errors.getAllErrors()) {
            error.addValidationError(objectError.getDefaultMessage());
        }
        return error;
    }
}