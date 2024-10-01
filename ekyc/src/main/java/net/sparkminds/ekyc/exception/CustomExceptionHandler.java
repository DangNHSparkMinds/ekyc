package net.sparkminds.ekyc.exception;

import net.sparkminds.ekyc.service.dto.response.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public ErrorResponseDto handlerNotFoundException(Exception ex, WebRequest req) {
        return new ErrorResponseDto(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handlerBadRequestException(Exception ex, WebRequest req) {
        return new ErrorResponseDto(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseStatus(code = HttpStatus.CONFLICT)
    public ErrorResponseDto handlerDuplicateKeyException(Exception ex, WebRequest req) {
        return new ErrorResponseDto(HttpStatus.CONFLICT, ex.getMessage());
    }
}
