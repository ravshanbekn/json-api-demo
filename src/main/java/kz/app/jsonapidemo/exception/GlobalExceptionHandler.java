package kz.app.jsonapidemo.exception;

import kz.app.jsonapidemo.model.jsonapi.ErrorObject;
import kz.app.jsonapidemo.model.jsonapi.JsonApiDocument;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ObjectNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public JsonApiDocument<ErrorObject> handleRuntimeException(ObjectNotFoundException exception) {
        ErrorObject errorObject = getErrorObject(HttpStatus.NOT_FOUND, exception);
        return new JsonApiDocument<>(List.of(errorObject));
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public JsonApiDocument<ErrorObject> handleRuntimeException(RuntimeException exception) {
        ErrorObject errorObject = getErrorObject(HttpStatus.BAD_REQUEST, exception);
        return new JsonApiDocument<>(List.of(errorObject));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public JsonApiDocument<ErrorObject> handleException(Exception exception) {
        ErrorObject errorObject = getErrorObject(HttpStatus.INTERNAL_SERVER_ERROR, null);
        return new JsonApiDocument<>(List.of(errorObject));
    }

    private static ErrorObject getErrorObject(HttpStatus status, Exception exception) {
        ErrorObject errorObject = new ErrorObject();
        errorObject.setStatus(String.valueOf(status.value()));
        errorObject.setTitle(status.name());

        if (exception != null) {
            errorObject.setDetail(exception.getMessage());
        }

        return errorObject;
    }
}
