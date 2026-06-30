package br.com.postechfiap.toloni.restauranthub.infrastructure.web.rest.user;

import br.com.postechfiap.toloni.restauranthub.domain.usertype.UserRole;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.UserType;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.valueobject.UserTypeDescription;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.valueobject.UserTypeId;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.valueobject.UserTypeName;
import br.com.postechfiap.toloni.restauranthub.infrastructure.persistence.entities.UserTypeJpaEntity;
import br.com.postechfiap.toloni.restauranthub.infrastructure.persistence.repositories.UserJpaRepository;
import br.com.postechfiap.toloni.restauranthub.infrastructure.persistence.repositories.UserTypeJpaRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("integration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class UserApiIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private UserTypeJpaRepository userTypeJpaRepository;

    private UserType userTypeCustomer;
    private UserType userTypeRestaurantOwner;

    @BeforeEach
    void setUp() {
        userJpaRepository.deleteAll();
        userTypeJpaRepository.deleteAll();
        userTypeCustomer = new UserType(
                UserTypeId.generate(),
                UserTypeName.of("Customer"),
                UserTypeDescription.of("User who can browse restaurants and place orders."),
                UserRole.CUSTOMER);
        userTypeRestaurantOwner = new UserType(
                UserTypeId.generate(),
                UserTypeName.of("Restaurant Owner"),
                UserTypeDescription.of("User responsible for managing a restaurant."),
                UserRole.RESTAURANT_OWNER);
        userTypeJpaRepository.save(UserTypeJpaEntity.fromDomain(userTypeCustomer));
        userTypeJpaRepository.save(UserTypeJpaEntity.fromDomain(userTypeRestaurantOwner));
    }

    // -------------------------------------------------------------------------
    // POST /api/v1/users
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should create User successfully")
    void shouldCreateUserSuccessfully() throws Exception {

        var request = new UserRequest(
                "John Doe",
                "john@example.com",
                "secret123",
                userTypeCustomer.getId().getValue().toString());

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.userTypeId").value(userTypeCustomer.getId().getValue().toString()));
    }
}
