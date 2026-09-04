package com.example.demo.customer.controller;

import com.example.demo.customer.dto.CustomerRequest;
import com.example.demo.customer.dto.CustomerResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class CustomerControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");
        registry.add("spring.flyway.enabled", () -> "true");
        registry.add("spring.flyway.locations", () -> "classpath:db/migration");
    }

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void createCustomer_success_returns201() {
        CustomerRequest req = new CustomerRequest("Alice", "Brown", "alice@example.com");
        HttpHeaders headers = jsonHeaders();

        ResponseEntity<CustomerResponse> response = restTemplate.postForEntity(baseUrl(), new HttpEntity<>(req, headers), CustomerResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getLocation()).isNotNull();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getEmail()).isEqualTo("alice@example.com");
    }

    @Test
    void getCustomerById_returnsCustomer() {
        CustomerRequest req = new CustomerRequest("Bob", "Clark", "bob@example.com");
        Long id = createCustomer(req).getBody().getId();

        ResponseEntity<CustomerResponse> response = restTemplate.getForEntity(baseUrl() + "/{id}", CustomerResponse.class, id);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getFirstName()).isEqualTo("Bob");
    }

    @Test
    void listCustomers_returnsAllCustomers() {
        createCustomer(new CustomerRequest("Carol", "Davis", "carol@example.com"));
        createCustomer(new CustomerRequest("Dan", "Evans", "dan@example.com"));

        ResponseEntity<CustomerResponse[]> response = restTemplate.getForEntity(baseUrl(), CustomerResponse[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void updateCustomer_success_updatesRecord() {
        Long id = createCustomer(new CustomerRequest("Eve", "Frank", "eve@example.com")).getBody().getId();
        CustomerRequest update = new CustomerRequest("Evelyn", "Frank", "eve.updated@example.com");

        ResponseEntity<CustomerResponse> response = restTemplate.exchange(
                baseUrl() + "/{id}",
                HttpMethod.PUT,
                new HttpEntity<>(update, jsonHeaders()),
                CustomerResponse.class,
                id);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getFirstName()).isEqualTo("Evelyn");
        assertThat(response.getBody().getEmail()).isEqualTo("eve.updated@example.com");
    }

    @Test
    void deleteCustomer_success_removesCustomer() {
        Long id = createCustomer(new CustomerRequest("Frank", "Green", "frank@example.com")).getBody().getId();

        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                baseUrl() + "/{id}",
                HttpMethod.DELETE,
                null,
                Void.class,
                id);

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        ResponseEntity<CustomerResponse> fetch = restTemplate.getForEntity(baseUrl() + "/{id}", CustomerResponse.class, id);
        assertThat(fetch.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void createCustomer_duplicate_returns409() {
        CustomerRequest req = new CustomerRequest("Grace", "Hall", "duplicate@example.com");
        createCustomer(req);

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl(), new HttpEntity<>(req, jsonHeaders()), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).contains("Duplicate customer");
    }

    @Test
    void createCustomer_validationFailure_returns400() {
        CustomerRequest invalid = new CustomerRequest("", "", "not-an-email");

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl(), new HttpEntity<>(invalid, jsonHeaders()), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Validation failed");
    }

    private ResponseEntity<CustomerResponse> createCustomer(CustomerRequest request) {
        return restTemplate.postForEntity(baseUrl(), new HttpEntity<>(request, jsonHeaders()), CustomerResponse.class);
    }

    private HttpHeaders jsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private String baseUrl() {
        return "http://localhost:" + port + "/api/customers";
    }
}
