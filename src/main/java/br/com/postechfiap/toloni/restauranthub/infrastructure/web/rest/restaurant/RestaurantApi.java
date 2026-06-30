package br.com.postechfiap.toloni.restauranthub.infrastructure.web.rest.restaurant;

import br.com.postechfiap.toloni.restauranthub.domain.shared.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/// API interface for [Restaurant] operations.
///
/// Defines the contract for the REST layer, separating Swagger
/// documentation from the controller implementation.
@Tag(name = "Restaurants", description = "Operations related to restaurants")
@RequestMapping("/api/v1/restaurants")
public interface RestaurantApi {

    @Operation(summary = "Create a restaurant", description = "Creates a new restaurant in the system.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Restaurant created successfully"),
            @ApiResponse(responseCode = "404", description = "Owner user not found"),
            @ApiResponse(responseCode = "409", description = "Restaurant with the given name already exists")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    RestaurantResponse create(
            @Parameter(description = "Authenticated user identifier", required = true) @RequestHeader("X-User-Id") String userId,
            @RequestBody RestaurantRequest request);

    @Operation(summary = "Update a restaurant", description = "Updates an existing restaurant by its identifier.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Restaurant updated successfully"),
            @ApiResponse(responseCode = "404", description = "Restaurant or owner user not found"),
            @ApiResponse(responseCode = "409", description = "Restaurant with the given name already exists")
    })
    @PatchMapping("/{id}")
    RestaurantResponse update(
            @Parameter(description = "Restaurant identifier", required = true) @PathVariable String id,
            @Parameter(description = "Authenticated user identifier", required = true) @RequestHeader("X-User-Id") String userId,
            @RequestBody RestaurantRequest request);

    @PatchMapping("/{id}/transfer-ownership")
    RestaurantTransferOwnershipResponse transferOwnership(
            @Parameter(description = "Restaurant identifier", required = true) @PathVariable String id,
            @Parameter(description = "Authenticated user identifier", required = true) @RequestHeader("X-User-Id") String userId,
            @RequestBody RestaurantTransferOwnershipRequest request);

    @Operation(summary = "Delete a restaurant", description = "Deletes a restaurant by its identifier. This operation is idempotent.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Restaurant deleted successfully"),
            @ApiResponse(responseCode = "409", description = "Restaurant is in use and cannot be deleted")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(
            @Parameter(description = "Restaurant identifier", required = true) @PathVariable String id,
            @Parameter(description = "Authenticated user identifier", required = true) @RequestHeader("X-User-Id") String userId);

    @Operation(summary = "Find a restaurant by id", description = "Retrieves a restaurant by its identifier.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Restaurant found"),
            @ApiResponse(responseCode = "404", description = "Restaurant not found")
    })
    @GetMapping("/{id}")
    RestaurantResponse findById(
            @Parameter(description = "Restaurant identifier", required = true) @PathVariable String id);

    @Operation(summary = "Find all restaurants", description = "Retrieves a paginated list of restaurants.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Restaurants retrieved successfully")
    })
    @GetMapping
    Page<RestaurantResponse> findAll(
            @Parameter(description = "Page number (zero-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Field to sort by", example = "name") @RequestParam(required = false) String sort,
            @Parameter(description = "Sort direction (ASC or DESC)", example = "ASC") @RequestParam(required = false) String direction,
            @Parameter(description = "Field to filter by", example = "name") @RequestParam(required = false) String filter,
            @Parameter(description = "Filter value", example = "burger") @RequestParam(required = false) String filterValue);
}
