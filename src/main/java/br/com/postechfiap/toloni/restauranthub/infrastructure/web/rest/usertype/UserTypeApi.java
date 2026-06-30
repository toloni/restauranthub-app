package br.com.postechfiap.toloni.restauranthub.infrastructure.web.rest.usertype;

import br.com.postechfiap.toloni.restauranthub.domain.shared.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/// API interface for [UserType] operations.
///
/// Defines the contract for the REST layer, separating Swagger
/// documentation from the controller implementation.
@Tag(name = "User Types", description = "Operations related to user types")
@RequestMapping("/api/v1/user-types")
public interface UserTypeApi {

    @Operation(summary = "Create a user type", description = "Creates a new user type in the system.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User type created successfully"),
            @ApiResponse(responseCode = "409", description = "User type with the given role already exists")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    UserTypeResponse create(@RequestBody UserTypeRequest request);

    @Operation(summary = "Update a user type", description = "Updates an existing user type by its identifier.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User type updated successfully"),
            @ApiResponse(responseCode = "404", description = "User type not found"),
            @ApiResponse(responseCode = "409", description = "User type with the given role already exists")
    })
    @PatchMapping("/{id}")
    UserTypeResponse update(
            @Parameter(description = "User type identifier", required = true) @PathVariable String id,
            @RequestBody UserTypeRequest request);

    @Operation(summary = "Delete a user type", description = "Deletes a user type by its identifier. This operation is idempotent.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User type deleted successfully")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(
            @Parameter(description = "User type identifier", required = true) @PathVariable String id);

    @Operation(summary = "Find a user type by id", description = "Retrieves a user type by its identifier.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User type found"),
            @ApiResponse(responseCode = "404", description = "User type not found")
    })
    @GetMapping("/{id}")
    UserTypeResponse findById(
            @Parameter(description = "User type identifier", required = true) @PathVariable String id);

    @Operation(summary = "Find all user types", description = "Retrieves a paginated list of user types.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User types retrieved successfully")
    })
    @GetMapping
    Page<UserTypeResponse> findAll(
            @Parameter(description = "Page number (zero-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Field to sort by", example = "name") @RequestParam(required = false) String sort,
            @Parameter(description = "Sort direction (ASC or DESC)", example = "ASC") @RequestParam(required = false) String direction,
            @Parameter(description = "Field to filter by", example = "name") @RequestParam(required = false) String filter,
            @Parameter(description = "Filter value", example = "pizza") @RequestParam(required = false) String filterValue);
}
