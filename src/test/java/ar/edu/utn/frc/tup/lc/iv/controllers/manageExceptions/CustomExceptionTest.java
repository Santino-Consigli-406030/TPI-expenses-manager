package ar.edu.utn.frc.tup.lc.iv.controllers.manageExceptions;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

class CustomExceptionTest {

    @Test
    void testConstructorWithMessageAndStatus() {
        String message = "Test message";
        HttpStatus status = HttpStatus.BAD_REQUEST;

        CustomException exception = new CustomException(message, status);

        assertEquals(message, exception.getMessage());
        assertEquals(status, exception.getStatus());
    }

    @Test
    void testConstructorWithMessageStatusAndCause() {
        String message = "Test message";
        HttpStatus status = HttpStatus.NOT_FOUND;
        Throwable cause = new IllegalArgumentException("Cause of exception");

        CustomException exception = new CustomException(message, status, cause);

        assertEquals(message, exception.getMessage());
        assertEquals(status, exception.getStatus());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testGetStatus() {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        CustomException exception = new CustomException("Test message", status);

        assertEquals(status, exception.getStatus());
    }
}
