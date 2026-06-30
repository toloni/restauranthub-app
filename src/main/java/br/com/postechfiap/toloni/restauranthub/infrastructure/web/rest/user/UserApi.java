package br.com.postechfiap.toloni.restauranthub.infrastructure.web.rest.user;

import br.com.postechfiap.toloni.restauranthub.domain.shared.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/// API interface for [User] operations.
///
/// Defines the contract for the REST layer, separating Swagger
/// documentation from the controller implementation.
@Tag(name = "Users", description = "Operations related to users")
@RequestMapping("/api/v1/users")
public interface UserApi {

    @Operation(summary = "Create a user", description = "Creates a new user in the system.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "409", description = "User with the given email already exists")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    UserResponse create(@RequestBody UserRequest input);

    @Operation(summary = "Update a user", description = "Updates an existing user by its identifier.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "409", description = "User with the given email already exists")
    })
    @PatchMapping("/{id}")
    UserResponse update(
            @Parameter(description = "User identifier", required = true) @PathVariable String id,
            @RequestBody UserRequest input);

    @Operation(summary = "Delete a user", description = "Deletes a user by its identifier. This operation is idempotent.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted successfully")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(
            @Parameter(description = "User identifier", required = true) @PathVariable String id);

    @Operation(summary = "Find a user by id", description = "Retrieves a user by its identifier.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    UserResponse findById(
            @Parameter(description = "User identifier", required = true) @PathVariable String id);

    @Operation(summary = "Find all users", description = "Retrieves a paginated list of users.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    })
    @GetMapping
    Page<UserResponse> findAll(
            @Parameter(description = "Page number (zero-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Field to sort by", example = "name") @RequestParam(required = false) String sort,
            @Parameter(description = "Sort direction (ASC or DESC)", example = "ASC") @RequestParam(required = false) String direction,
            @Parameter(description = "Field to filter by", example = "name") @RequestParam(required = false) String filter,
            @Parameter(description = "Filter value", example = "john") @RequestParam(required = false) String filterValue);
}
