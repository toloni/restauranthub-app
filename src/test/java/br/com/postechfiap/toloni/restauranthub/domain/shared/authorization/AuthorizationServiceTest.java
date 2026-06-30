package br.com.postechfiap.toloni.restauranthub.domain.shared.authorization;

import br.com.postechfiap.toloni.restauranthub.domain.restaurant.Restaurant;
import br.com.postechfiap.toloni.restauranthub.domain.restaurant.valueobject.*;
import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.NotFoundException;
import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.UnauthorizedException;
import br.com.postechfiap.toloni.restauranthub.domain.user.User;
import br.com.postechfiap.toloni.restauranthub.domain.user.UserGateway;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserEmail;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserId;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserName;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserPassword;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.UserRole;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.UserType;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.UserTypeGateway;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.valueobject.UserTypeDescription;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.valueobject.UserTypeId;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.valueobject.UserTypeName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class AuthorizationServiceTest {

    @Mock
    private UserGateway userGateway;

    @Mock
    private UserTypeGateway userTypeGateway;

    @InjectMocks
    private AuthorizationService authorizationService;

    private UserId requesterId;
    private UserTypeId userTypeId;
    private User restaurantOwnerUser;
    private User customerUser;
    private User adminUser;
    private UserType restaurantOwnerType;
    private UserType customerType;
    private UserType adminType;
    private Restaurant restaurant;

    @BeforeEach
    void setUp() {
        requesterId = UserId.generate();
        userTypeId = UserTypeId.generate();

        restaurantOwnerType = new UserType(
                userTypeId,
                UserTypeName.of("Restaurant Owner"),
                UserTypeDescription.of("Owns and manages a restaurant"),
                UserRole.RESTAURANT_OWNER
        );
        customerType = new UserType(
                UserTypeId.generate(),
                UserTypeName.of("Customer"),
                UserTypeDescription.of("Browses and orders food"),
                UserRole.CUSTOMER
        );
        adminType = new UserType(
                UserTypeId.generate(),
                UserTypeName.of("Admin"),
                UserTypeDescription.of("System administrator"),
                UserRole.ADMIN
        );

        restaurantOwnerUser = new User(
                requesterId,
                UserName.of("John Doe"),
                UserEmail.of("john@example.com"),
                UserPassword.of("password123"),
                restaurantOwnerType.getId()
        );
        customerUser = new User(
                requesterId,
                UserName.of("Jane Doe"),
                UserEmail.of("jane@example.com"),
                UserPassword.of("password123"),
                customerType.getId()
        );
        adminUser = new User(
                requesterId,
                UserName.of("Admin User"),
                UserEmail.of("admin@example.com"),
                UserPassword.of("password123"),
                adminType.getId()
        );

        restaurant = new Restaurant(
                RestaurantId.generate(),
                RestaurantName.of("The Great Burger"),
                RestaurantAddress.of("123 Main St, Springfield"),
                RestaurantCuisineType.of("American"),
                RestaurantOpeningHours.of("Mon-Fri 9am-10pm"),
                requesterId
        );
    }

    // -------------------------------------------------------------------------
    // validateRestaurantOwnership
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should pass validation when requester is the owner")
    void shouldPassValidationWhenRequesterIsTheOwner() {
        when(userGateway.findById(requesterId)).thenReturn(Optional.of(restaurantOwnerUser));
        when(userTypeGateway.findById(restaurantOwnerType.getId())).thenReturn(Optional.of(restaurantOwnerType));

        assertThatNoException().isThrownBy(() ->
                authorizationService.validateRestaurantOwnership(restaurant, requesterId));
    }

    @Test
    @DisplayName("Should pass validation when requester is admin")
    void shouldPassValidationWhenRequesterIsAdmin() {
        when(userGateway.findById(requesterId)).thenReturn(Optional.of(adminUser));
        when(userTypeGateway.findById(adminType.getId())).thenReturn(Optional.of(adminType));

        assertThatNoException().isThrownBy(() ->
                authorizationService.validateRestaurantOwnership(restaurant, requesterId));
    }

    @Test
    @DisplayName("Should throw UnauthorizedException when requester is not the owner")
    void shouldThrowUnauthorizedExceptionWhenRequesterIsNotTheOwner() {
        var differentOwnerId = UserId.generate();
        var differentOwner = new User(
                differentOwnerId,
                UserName.of("Other User"),
                UserEmail.of("other@example.com"),
                UserPassword.of("password123"),
                restaurantOwnerType.getId()
        );

        when(userGateway.findById(differentOwnerId)).thenReturn(Optional.of(differentOwner));
        when(userTypeGateway.findById(restaurantOwnerType.getId())).thenReturn(Optional.of(restaurantOwnerType));

        assertThatThrownBy(() ->
                authorizationService.validateRestaurantOwnership(restaurant, differentOwnerId))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Only the owner can manage this restaurant.");
    }

    @Test
    @DisplayName("Should throw UnauthorizedException when requester is customer")
    void shouldThrowUnauthorizedExceptionWhenRequesterIsCustomer() {
        var customerId = UserId.generate();
        var customer = new User(
                customerId,
                UserName.of("Customer User"),
                UserEmail.of("customer@example.com"),
                UserPassword.of("password123"),
                customerType.getId()
        );

        when(userGateway.findById(customerId)).thenReturn(Optional.of(customer));
        when(userTypeGateway.findById(customerType.getId())).thenReturn(Optional.of(customerType));

        assertThatThrownBy(() ->
                authorizationService.validateRestaurantOwnership(restaurant, customerId))
                .isInstanceOf(UnauthorizedException.class);
    }

    // -------------------------------------------------------------------------
    // validateMenuItemOwnership
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should pass menu item validation when requester is the owner")
    void shouldPassMenuItemValidationWhenRequesterIsTheOwner() {
        when(userGateway.findById(requesterId)).thenReturn(Optional.of(restaurantOwnerUser));
        when(userTypeGateway.findById(restaurantOwnerType.getId())).thenReturn(Optional.of(restaurantOwnerType));

        assertThatNoException().isThrownBy(() ->
                authorizationService.validateMenuItemOwnership(restaurant, requesterId));
    }

    @Test
    @DisplayName("Should pass menu item validation when requester is admin")
    void shouldPassMenuItemValidationWhenRequesterIsAdmin() {
        when(userGateway.findById(requesterId)).thenReturn(Optional.of(adminUser));
        when(userTypeGateway.findById(adminType.getId())).thenReturn(Optional.of(adminType));

        assertThatNoException().isThrownBy(() ->
                authorizationService.validateMenuItemOwnership(restaurant, requesterId));
    }

    @Test
    @DisplayName("Should throw UnauthorizedException when requester is not the owner of menu item restaurant")
    void shouldThrowUnauthorizedExceptionWhenRequesterIsNotOwnerOfMenuItemRestaurant() {
        var differentOwnerId = UserId.generate();
        var differentOwner = new User(
                differentOwnerId,
                UserName.of("Other User"),
                UserEmail.of("other@example.com"),
                UserPassword.of("password123"),
                restaurantOwnerType.getId()
        );

        when(userGateway.findById(differentOwnerId)).thenReturn(Optional.of(differentOwner));
        when(userTypeGateway.findById(restaurantOwnerType.getId())).thenReturn(Optional.of(restaurantOwnerType));

        assertThatThrownBy(() ->
                authorizationService.validateMenuItemOwnership(restaurant, differentOwnerId))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Only the owner can manage items of this restaurant.");
    }

    // -------------------------------------------------------------------------
    // validateRestaurantOwnerRole
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should pass role validation when requester is restaurant owner")
    void shouldPassRoleValidationWhenRequesterIsRestaurantOwner() {
        when(userGateway.findById(requesterId)).thenReturn(Optional.of(restaurantOwnerUser));
        when(userTypeGateway.findById(restaurantOwnerType.getId())).thenReturn(Optional.of(restaurantOwnerType));

        assertThatNoException().isThrownBy(() ->
                authorizationService.validateRestaurantOwnerRole(requesterId));
    }

    @Test
    @DisplayName("Should pass role validation when requester is admin")
    void shouldPassRoleValidationWhenRequesterIsAdmin() {
        when(userGateway.findById(requesterId)).thenReturn(Optional.of(adminUser));
        when(userTypeGateway.findById(adminType.getId())).thenReturn(Optional.of(adminType));

        assertThatNoException().isThrownBy(() ->
                authorizationService.validateRestaurantOwnerRole(requesterId));
    }

    @Test
    @DisplayName("Should throw UnauthorizedException when requester is customer")
    void shouldThrowUnauthorizedExceptionWhenRequesterIsCustomerForRoleValidation() {
        when(userGateway.findById(requesterId)).thenReturn(Optional.of(customerUser));
        when(userTypeGateway.findById(customerType.getId())).thenReturn(Optional.of(customerType));

        assertThatThrownBy(() ->
                authorizationService.validateRestaurantOwnerRole(requesterId))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("User is not a Restaurant Owner.");
    }

    // -------------------------------------------------------------------------
    // isAdmin
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should return true when requester is admin")
    void shouldReturnTrueWhenRequesterIsAdmin() {
        when(userGateway.findById(requesterId)).thenReturn(Optional.of(adminUser));
        when(userTypeGateway.findById(adminType.getId())).thenReturn(Optional.of(adminType));

        assertThat(authorizationService.isAdmin(requesterId)).isTrue();
    }

    @Test
    @DisplayName("Should return false when requester is restaurant owner")
    void shouldReturnFalseWhenRequesterIsRestaurantOwner() {
        when(userGateway.findById(requesterId)).thenReturn(Optional.of(restaurantOwnerUser));
        when(userTypeGateway.findById(restaurantOwnerType.getId())).thenReturn(Optional.of(restaurantOwnerType));

        assertThat(authorizationService.isAdmin(requesterId)).isFalse();
    }

    @Test
    @DisplayName("Should return false when requester is customer")
    void shouldReturnFalseWhenRequesterIsCustomer() {
        when(userGateway.findById(requesterId)).thenReturn(Optional.of(customerUser));
        when(userTypeGateway.findById(customerType.getId())).thenReturn(Optional.of(customerType));

        assertThat(authorizationService.isAdmin(requesterId)).isFalse();
    }

    // -------------------------------------------------------------------------
    // NotFoundException
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should throw NotFoundException when User is not found")
    void shouldThrowNotFoundExceptionWhenUserIsNotFound() {
        when(userGateway.findById(requesterId)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                authorizationService.validateRestaurantOwnership(restaurant, requesterId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(requesterId.getValue().toString());
    }

    @Test
    @DisplayName("Should throw NotFoundException when UserType is not found")
    void shouldThrowNotFoundExceptionWhenUserTypeIsNotFound() {
        when(userGateway.findById(requesterId)).thenReturn(Optional.of(restaurantOwnerUser));
        when(userTypeGateway.findById(restaurantOwnerType.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                authorizationService.validateRestaurantOwnership(restaurant, requesterId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(restaurantOwnerType.getId().getValue().toString());
    }
}