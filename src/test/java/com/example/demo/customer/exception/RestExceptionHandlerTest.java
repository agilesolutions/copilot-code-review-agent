package com.example.demo.customer.exception;

import com.example.demo.customer.service.CustomerNotFoundException;
import com.example.demo.customer.service.DuplicateCustomerException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RestExceptionHandlerTest {

    @Test
void returnsConflictProblemDetailForDuplicateCustomerException() {
    RestExceptionHandler handler = new RestExceptionHandler();
    DuplicateCustomerException ex = new DuplicateCustomerException("Customer with email already exists: duplicate@example.com");

    ResponseEntity<ProblemDetail> response = handler.handleDuplicate(ex);

    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    ProblemDetail pd = response.getBody();
    assertNotNull(pd);
    assertEquals("Duplicate customer", pd.getTitle());
    assertEquals(ex.getMessage(), pd.getDetail());
}

@Test
void returnsNotFoundProblemDetailForCustomerNotFoundException() {
    RestExceptionHandler handler = new RestExceptionHandler();
    CustomerNotFoundException ex = new CustomerNotFoundException("Customer not found: 123");

    ResponseEntity<ProblemDetail> response = handler.handleNotFound(ex);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    ProblemDetail pd = response.getBody();
    assertNotNull(pd);
    assertEquals("Customer not found", pd.getTitle());
    assertEquals(ex.getMessage(), pd.getDetail());
}

@Test
void returnsBadRequestProblemDetailForMethodArgumentNotValidException() {
    RestExceptionHandler handler = new RestExceptionHandler();

    BindingResult bindingResult = mock(BindingResult.class);
    FieldError f1 = new FieldError("customer", "email", "must be a well-formed email address");
    FieldError f2 = new FieldError("customer", "firstName", "must not be blank");
    when(bindingResult.getFieldErrors()).thenReturn(List.of(f1, f2));

    MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
    when(ex.getBindingResult()).thenReturn(bindingResult);

    ResponseEntity<ProblemDetail> response = handler.handleValidation(ex);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    ProblemDetail pd = response.getBody();
    assertNotNull(pd);
    assertEquals("Validation failed", pd.getTitle());
    String detail = pd.getDetail();
    assertTrue(detail.contains("email: must be a well-formed email address"));
    assertTrue(detail.contains("firstName: must not be blank"));
}

@Test
void returnsBadRequestProblemDetailForConstraintViolationException() {
    RestExceptionHandler handler = new RestExceptionHandler();

    jakarta.validation.Path path = mock(jakarta.validation.Path.class);
    when(path.toString()).thenReturn("email");
    ConstraintViolation<?> cv = mock(ConstraintViolation.class);
    when(cv.getPropertyPath()).thenReturn(path);
    when(cv.getMessage()).thenReturn("must be a valid email");

    Set<ConstraintViolation<?>> violations = Set.of((ConstraintViolation<?>) cv);
    ConstraintViolationException ex = new ConstraintViolationException(violations);

    ResponseEntity<ProblemDetail> response = handler.handleConstraintViolation(ex);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    ProblemDetail pd = response.getBody();
    assertNotNull(pd);
    assertEquals("Validation failed", pd.getTitle());
    String detail = pd.getDetail();
    assertTrue(detail.contains("email: must be a valid email"));
}

}