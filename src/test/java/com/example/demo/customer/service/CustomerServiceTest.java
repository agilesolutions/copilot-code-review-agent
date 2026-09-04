package com.example.demo.customer.service;

import com.example.demo.customer.entity.Customer;
import com.example.demo.customer.dto.CustomerRequest;
import com.example.demo.customer.dto.CustomerResponse;
import com.example.demo.customer.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerServiceTest {

    @Mock
    private CustomerRepository repository;

    @InjectMocks
    private CustomerService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createCustomer_success() {
        CustomerRequest req = new CustomerRequest("John", "Doe", "john@example.com");
        when(repository.existsByEmail(req.getEmail())).thenReturn(false);
        Customer toSave = new Customer(req.getFirstName(), req.getLastName(), req.getEmail());
        // simulate generated id and createdAt
        Customer saved = new Customer(req.getFirstName(), req.getLastName(), req.getEmail());
        // set id via reflection isn't necessary — mock repository to return saved with id
        when(repository.save(any(Customer.class))).thenAnswer(inv -> {
            Customer c = inv.getArgument(0);
            // emulate DB assigning id
            java.lang.reflect.Field idField = Customer.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(c, 42L);
            java.lang.reflect.Field createdField = Customer.class.getDeclaredField("createdAt");
            createdField.setAccessible(true);
            createdField.set(c, OffsetDateTime.now());
            return c;
        });

        CustomerResponse resp = service.createCustomer(req);

        assertNotNull(resp.getId());
        assertEquals(req.getEmail(), resp.getEmail());
        verify(repository).existsByEmail(req.getEmail());
        verify(repository).save(any(Customer.class));
    }

    @Test
    void createCustomer_duplicate_throws() {
        CustomerRequest req = new CustomerRequest("Jane", "Doe", "jane@example.com");
        when(repository.existsByEmail(req.getEmail())).thenReturn(true);

        DuplicateCustomerException ex = assertThrows(DuplicateCustomerException.class, () -> service.createCustomer(req));
        assertTrue(ex.getMessage().contains(req.getEmail()));
        verify(repository).existsByEmail(req.getEmail());
        verify(repository, never()).save(any());
    }

    @Test
void returnsAllCustomersAsResponses() throws Exception {
    Customer c1 = new Customer("A", "One", "a1@example.com");
    Customer c2 = new Customer("B", "Two", "b2@example.com");

    // set ids and createdAt
    java.lang.reflect.Field idField = Customer.class.getDeclaredField("id");
    idField.setAccessible(true);
    idField.set(c1, 1L);
    idField.set(c2, 2L);
    java.lang.reflect.Field createdField = Customer.class.getDeclaredField("createdAt");
    createdField.setAccessible(true);
    createdField.set(c1, java.time.OffsetDateTime.now());
    createdField.set(c2, java.time.OffsetDateTime.now());

    when(repository.findAll()).thenReturn(List.of(c1, c2));

    List<CustomerResponse> results = service.getAllCustomers();

    assertEquals(2, results.size());
    assertEquals(1L, results.get(0).getId());
    assertEquals("a1@example.com", results.get(0).getEmail());
    assertEquals(2L, results.get(1).getId());
    assertEquals("b2@example.com", results.get(1).getEmail());
    verify(repository).findAll();
}

@Test
void getCustomerById_foundReturnsResponse() throws Exception {
    Long id = 100L;
    Customer c = new Customer("Found", "User", "found@example.com");
    java.lang.reflect.Field idField = Customer.class.getDeclaredField("id");
    idField.setAccessible(true);
    idField.set(c, id);
    java.lang.reflect.Field createdField = Customer.class.getDeclaredField("createdAt");
    createdField.setAccessible(true);
    createdField.set(c, java.time.OffsetDateTime.now());

    when(repository.findById(id)).thenReturn(Optional.of(c));

    CustomerResponse resp = service.getCustomerById(id);

    assertNotNull(resp);
    assertEquals(id, resp.getId());
    assertEquals("found@example.com", resp.getEmail());
    verify(repository).findById(id);
}

@Test
void getCustomerById_notFoundThrowsCustomerNotFoundException() {
    Long id = 999L;
    when(repository.findById(id)).thenReturn(Optional.empty());

    CustomerNotFoundException ex = assertThrows(CustomerNotFoundException.class, () -> service.getCustomerById(id));
    assertTrue(ex.getMessage().contains(String.valueOf(id)));
    verify(repository).findById(id);
}

@Test
void updateCustomer_successfullyUpdatesCustomer() {
    Long id = 7L;
    Customer existing = new Customer("Old", "Name", "old@example.com");
    try {
        java.lang.reflect.Field idField = Customer.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(existing, id);
        java.lang.reflect.Field createdField = Customer.class.getDeclaredField("createdAt");
        createdField.setAccessible(true);
        createdField.set(existing, java.time.OffsetDateTime.now());
    } catch (Exception ignored) {}

    CustomerRequest updateReq = new CustomerRequest("NewFirst", "NewLast", "new@example.com");

    when(repository.findById(id)).thenReturn(Optional.of(existing));
    when(repository.existsByEmailAndIdNot(updateReq.getEmail(), id)).thenReturn(false);
    when(repository.save(any(Customer.class))).thenAnswer(inv -> inv.getArgument(0));

    CustomerResponse resp = service.updateCustomer(id, updateReq);

    assertNotNull(resp);
    assertEquals(id, resp.getId());
    assertEquals("NewFirst", resp.getFirstName());
    assertEquals("new@example.com", resp.getEmail());
    verify(repository).findById(id);
    verify(repository).existsByEmailAndIdNot(updateReq.getEmail(), id);
    verify(repository).save(any(Customer.class));
}

@Test
void updateCustomer_duplicateEmailThrowsDuplicateCustomerException() {
    Long id = 8L;
    Customer existing = new Customer("X", "Y", "x@y.com");
    try {
        java.lang.reflect.Field idField = Customer.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(existing, id);
    } catch (Exception ignored) {}

    CustomerRequest updateReq = new CustomerRequest("X", "Y", "dup@example.com");

    when(repository.findById(id)).thenReturn(Optional.of(existing));
    when(repository.existsByEmailAndIdNot(updateReq.getEmail(), id)).thenReturn(true);

    DuplicateCustomerException ex = assertThrows(DuplicateCustomerException.class, () -> service.updateCustomer(id, updateReq));
    assertTrue(ex.getMessage().contains(updateReq.getEmail()));
    verify(repository).findById(id);
    verify(repository).existsByEmailAndIdNot(updateReq.getEmail(), id);
    verify(repository, never()).save(any());
}

@Test
void deleteCustomer_successDeletesRecord() {
    Long id = 55L;
    when(repository.existsById(id)).thenReturn(true);

    service.deleteCustomer(id);

    verify(repository).existsById(id);
    verify(repository).deleteById(id);
}

@Test
void deleteCustomer_notFoundThrowsCustomerNotFoundException() {
    Long id = 56L;
    when(repository.existsById(id)).thenReturn(false);

    CustomerNotFoundException ex = assertThrows(CustomerNotFoundException.class, () -> service.deleteCustomer(id));
    assertTrue(ex.getMessage().contains(String.valueOf(id)));
    verify(repository).existsById(id);
    verify(repository, never()).deleteById(any());
}
}
