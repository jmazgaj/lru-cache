package lru.cache.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartException;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MultipartException.class)
    final public ResponseEntity<String> handleFileException(HttpServletRequest request, Throwable ex) {
        return new ResponseEntity<>("Maximum file size is 1MB.", HttpStatus.BAD_REQUEST);
    }
}
