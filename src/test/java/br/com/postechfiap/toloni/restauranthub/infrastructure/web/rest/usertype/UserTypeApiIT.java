package br.com.postechfiap.toloni.restauranthub.infrastructure.web.rest.usertype;

import br.com.postechfiap.toloni.restauranthub.domain.usertype.UserRole;
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

import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("integration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserTypeApiIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserTypeJpaRepository userTypeJpaRepository;

    @BeforeEach
    void setUp() {
        userTypeJpaRepository.deleteAll();
    }

    // -------------------------------------------------------------------------
    // POST /api/v1/user-types
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should create UserType successfully")
    void shouldCreateUserTypeSuccessfully() throws Exception {
        var request = new UserTypeRequest("Restaurant Owner", "Owns and manages a restaurant", UserRole.RESTAURANT_OWNER);

        mockMvc.perform(post("/api/v1/user-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value("Restaurant Owner"))
                .andExpect(jsonPath("$.description").value("Owns and manages a restaurant"))
                .andExpect(jsonPath("$.role").value("RESTAURANT_OWNER"));
    }

    @Test
    @DisplayName("Should return 409 when role already exists")
    void shouldReturn409WhenRoleAlreadyExists() throws Exception {
        var request = new UserTypeRequest("Restaurant Owner", "Owns and manages a restaurant", UserRole.RESTAURANT_OWNER);

        mockMvc.perform(post("/api/v1/user-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/user-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.detail").value(containsString("RESTAURANT_OWNER")));
    }

    @Test
    @DisplayName("Should return 422 when role is null")
    void shouldReturn422WhenRoleIsNull() throws Exception {
        var request = new UserTypeRequest("Restaurant Owner", "Owns and manages a restaurant", null);

        mockMvc.perform(post("/api/v1/user-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.status").value(422));
    }

    @Test
    @DisplayName("Should return 400 when role is invalid")
    void shouldReturn400WhenRoleIsInvalid() throws Exception {
        mockMvc.perform(post("/api/v1/user-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Restaurant Owner",
                                    "description": "Owns and manages a restaurant",
                                    "role": "INVALID_ROLE"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    // -------------------------------------------------------------------------
    // PUT /api/v1/user-types/{id}
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should update UserType successfully")
    void shouldUpdateUserTypeSuccessfully() throws Exception {
        var created = createUserType("Restaurant Owner", "Owns and manages a restaurant", UserRole.RESTAURANT_OWNER);
        var request = new UserTypeRequest("Updated Name", "Updated description", UserRole.RESTAURANT_OWNER);

        mockMvc.perform(patch("/api/v1/user-types/{id}", created.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.description").value("Updated description"));
    }

    @Test
    @DisplayName("Should return 404 when updating non-existing UserType")
    void shouldReturn404WhenUpdatingNonExistingUserType() throws Exception {
        var request = new UserTypeRequest("Updated Name", "Updated description", UserRole.RESTAURANT_OWNER);

        mockMvc.perform(patch("/api/v1/user-types/{id}", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("Should return 409 when updating to an already existing role")
    void shouldReturn409WhenUpdatingToAlreadyExistingRole() throws Exception {
        createUserType("Restaurant Owner", "Owns and manages a restaurant", UserRole.RESTAURANT_OWNER);
        var customer = createUserType("Customer", "Browses and orders food", UserRole.CUSTOMER);
        var request = new UserTypeRequest("Customer", "Browses and orders food", UserRole.RESTAURANT_OWNER);

        mockMvc.perform(patch("/api/v1/user-types/{id}", customer.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    @DisplayName("Should return 400 when updating with invalid UUID")
    void shouldReturn400WhenUpdatingWithInvalidUUID() throws Exception {
        var request = new UserTypeRequest("Updated Name", "Updated description", UserRole.RESTAURANT_OWNER);

        mockMvc.perform(patch("/api/v1/user-types/{id}", "invalid-uuid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    // -------------------------------------------------------------------------
    // DELETE /api/v1/user-types/{id}
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should delete UserType successfully")
    void shouldDeleteUserTypeSuccessfully() throws Exception {
        var created = createUserType("Restaurant Owner", "Owns and manages a restaurant", UserRole.RESTAURANT_OWNER);

        mockMvc.perform(delete("/api/v1/user-types/{id}", created.id()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should return 204 when deleting non-existing UserType")
    void shouldReturn204WhenDeletingNonExistingUserType() throws Exception {
        mockMvc.perform(delete("/api/v1/user-types/{id}", UUID.randomUUID()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should return 400 when deleting with invalid UUID")
    void shouldReturn400WhenDeletingWithInvalidUUID() throws Exception {
        mockMvc.perform(delete("/api/v1/user-types/{id}", "invalid-uuid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    // -------------------------------------------------------------------------
    // GET /api/v1/user-types/{id}
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should find UserType by id successfully")
    void shouldFindUserTypeByIdSuccessfully() throws Exception {
        var created = createUserType("Restaurant Owner", "Owns and manages a restaurant", UserRole.RESTAURANT_OWNER);

        mockMvc.perform(get("/api/v1/user-types/{id}", created.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(created.id().toString()))
                .andExpect(jsonPath("$.name").value("Restaurant Owner"))
                .andExpect(jsonPath("$.description").value("Owns and manages a restaurant"))
                .andExpect(jsonPath("$.role").value("RESTAURANT_OWNER"));
    }

    @Test
    @DisplayName("Should return 404 when UserType is not found")
    void shouldReturn404WhenUserTypeIsNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/user-types/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("Should return 400 when finding with invalid UUID")
    void shouldReturn400WhenFindingWithInvalidUUID() throws Exception {
        mockMvc.perform(get("/api/v1/user-types/{id}", "invalid-uuid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    // -------------------------------------------------------------------------
    // GET /api/v1/user-types
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should return paginated list of UserTypes")
    void shouldReturnPaginatedListOfUserTypes() throws Exception {
        createUserType("Restaurant Owner", "Owns and manages a restaurant", UserRole.RESTAURANT_OWNER);
        createUserType("Customer", "Browses and orders food", UserRole.CUSTOMER);

        mockMvc.perform(get("/api/v1/user-types")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.pageNumber").value(0))
                .andExpect(jsonPath("$.pageSize").value(10));
    }

    @Test
    @DisplayName("Should return empty page when no UserTypes exist")
    void shouldReturnEmptyPageWhenNoUserTypesExist() throws Exception {
        mockMvc.perform(get("/api/v1/user-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    @DisplayName("Should return filtered UserTypes by name")
    void shouldReturnFilteredUserTypesByName() throws Exception {
        createUserType("Restaurant Owner", "Owns and manages a restaurant", UserRole.RESTAURANT_OWNER);
        createUserType("Customer", "Browses and orders food", UserRole.CUSTOMER);

        mockMvc.perform(get("/api/v1/user-types")
                        .param("filter", "name")
                        .param("filterValue", "Restaurant"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name").value("Restaurant Owner"));
    }

    @Test
    @DisplayName("Should return sorted UserTypes by name ASC")
    void shouldReturnSortedUserTypesByNameAsc() throws Exception {
        createUserType("Restaurant Owner", "Owns and manages a restaurant", UserRole.RESTAURANT_OWNER);
        createUserType("Customer", "Browses and orders food", UserRole.CUSTOMER);

        mockMvc.perform(get("/api/v1/user-types")
                        .param("sort", "name")
                        .param("direction", "ASC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Customer"))
                .andExpect(jsonPath("$.content[1].name").value("Restaurant Owner"));
    }

    @Test
    @DisplayName("Should return sorted UserTypes by name DESC")
    void shouldReturnSortedUserTypesByNameDesc() throws Exception {
        createUserType("Restaurant Owner", "Owns and manages a restaurant", UserRole.RESTAURANT_OWNER);
        createUserType("Customer", "Browses and orders food", UserRole.CUSTOMER);

        mockMvc.perform(get("/api/v1/user-types")
                        .param("sort", "name")
                        .param("direction", "DESC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Restaurant Owner"))
                .andExpect(jsonPath("$.content[1].name").value("Customer"));
    }

    @Test
    @DisplayName("Should return 400 when sorting by invalid field")
    void shouldReturn400WhenSortingByInvalidField() throws Exception {
        mockMvc.perform(get("/api/v1/user-types")
                        .param("sort", "invalidField")
                        .param("direction", "ASC"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    // -------------------------------------------------------------------------
    // Helper
    // -------------------------------------------------------------------------

    private UserTypeResponse createUserType(String name, String description, UserRole role) throws Exception {
        var request = new UserTypeRequest(name, description, role);

        var result = mockMvc.perform(post("/api/v1/user-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readValue(result.getResponse().getContentAsString(), UserTypeResponse.class);
    }
}
