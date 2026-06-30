package br.com.postechfiap.toloni.restauranthub.infrastructure.web.rest.menuitem;

import br.com.postechfiap.toloni.restauranthub.domain.shared.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/// API interface for [MenuItem] operations.
///
/// Defines the contract for the REST layer, separating Swagger
/// documentation from the controller implementation.
@Tag(name = "Menu Items", description = "Operations related to menu items")
@RequestMapping("/api/v1/menu-items")
public interface MenuItemApi {

    @Operation(summary = "Create a menu item", description = "Creates a new menu item for a restaurant.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Menu item created successfully"),
            @ApiResponse(responseCode = "403", description = "Requester is not the owner of the restaurant"),
            @ApiResponse(responseCode = "404", description = "Restaurant not found"),
            @ApiResponse(responseCode = "409", description = "Menu item with the given name already exists in the restaurant")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    MenuItemResponse create(
            @Parameter(description = "Authenticated user identifier", required = true) @RequestHeader("X-User-Id") String userId,
            @RequestBody MenuItemRequest request);

    @Operation(summary = "Update a menu item", description = "Updates an existing menu item by its identifier.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Menu item updated successfully"),
            @ApiResponse(responseCode = "403", description = "Requester is not the owner of the restaurant"),
            @ApiResponse(responseCode = "404", description = "Menu item or restaurant not found"),
            @ApiResponse(responseCode = "409", description = "Menu item with the given name already exists in the restaurant")
    })
    @PatchMapping("/{id}")
    MenuItemResponse update(
            @Parameter(description = "Menu item identifier", required = true) @PathVariable String id,
            @Parameter(description = "Authenticated user identifier", required = true) @RequestHeader("X-User-Id") String userId,
            @RequestBody MenuItemRequest request);

    @Operation(summary = "Delete a menu item", description = "Deletes a menu item by its identifier.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Menu item deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Requester is not the owner of the restaurant"),
            @ApiResponse(responseCode = "404", description = "Menu item or restaurant not found")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(
            @Parameter(description = "Menu item identifier", required = true) @PathVariable String id,
            @Parameter(description = "Authenticated user identifier", required = true) @RequestHeader("X-User-Id") String userId);

    @Operation(summary = "Find a menu item by id", description = "Retrieves a menu item by its identifier.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Menu item found"),
            @ApiResponse(responseCode = "404", description = "Menu item not found")
    })
    @GetMapping("/{id}")
    MenuItemResponse findById(
            @Parameter(description = "Menu item identifier", required = true) @PathVariable String id);

    @Operation(summary = "Find all menu items", description = "Retrieves a paginated list of menu items, optionally filtered by restaurant.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Menu items retrieved successfully")
    })
    @GetMapping
    Page<MenuItemResponse> findAll(
            @Parameter(description = "Restaurant identifier to filter by") @RequestParam(required = false) String restaurantId,
            @Parameter(description = "Page number (zero-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "10") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Field to sort by", example = "name") @RequestParam(required = false) String sort,
            @Parameter(description = "Sort direction (ASC or DESC)", example = "ASC") @RequestParam(required = false) String direction,
            @Parameter(description = "Field to filter by", example = "name") @RequestParam(required = false) String filter,
            @Parameter(description = "Filter value", example = "pizza") @RequestParam(required = false) String filterValue);
}
