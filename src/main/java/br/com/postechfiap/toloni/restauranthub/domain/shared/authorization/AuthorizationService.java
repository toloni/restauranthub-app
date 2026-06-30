package br.com.postechfiap.toloni.restauranthub.domain.shared.authorization;

import br.com.postechfiap.toloni.restauranthub.domain.restaurant.Restaurant;
import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.NotFoundException;
import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.UnauthorizedException;
import br.com.postechfiap.toloni.restauranthub.domain.user.UserGateway;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserId;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.UserRole;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.UserTypeGateway;

/// Service responsible for validating authorization rules across the domain.
///
/// Centralizes ownership and role-based access control, avoiding duplication
/// of authorization logic across use cases.
///
/// ## Rules
/// - [UserRole#ADMIN] has full access to all resources
/// - [UserRole#RESTAURANT_OWNER] can only manage their own resources
/// - [UserRole#CUSTOMER] has no management access
public class AuthorizationService {

    private final UserGateway userGateway;
    private final UserTypeGateway userTypeGateway;

    /// @param userGateway     the gateway for [User] persistence operations
    /// @param userTypeGateway the gateway for [UserType] persistence operations
    public AuthorizationService(UserGateway userGateway, UserTypeGateway userTypeGateway) {
        this.userGateway = userGateway;
        this.userTypeGateway = userTypeGateway;
    }

    /// Validates whether the requester is the owner of the [Restaurant] or an admin.
    ///
    /// @param restaurant  the [Restaurant] to validate ownership against
    /// @param requesterId the [UserId] of the requester
    /// @throws NotFoundException     if no [User] or [UserType] is found for the requester
    /// @throws UnauthorizedException if the requester is neither the owner nor an admin
    public void validateRestaurantOwnership(Restaurant restaurant, UserId requesterId) {
        if (isAdmin(requesterId)) return;

        if (!restaurant.getOwnerId().equals(requesterId))
            throw new UnauthorizedException("Only the owner can manage this restaurant.");
    }

    /// Validates whether the requester is the owner of the [Restaurant] that
    /// the [MenuItem] belongs to, or an admin.
    ///
    /// @param restaurant  the [Restaurant] the [MenuItem] belongs to
    /// @param requesterId the [UserId] of the requester
    /// @throws NotFoundException     if no [User] or [UserType] is found for the requester
    /// @throws UnauthorizedException if the requester is neither the owner nor an admin
    public void validateMenuItemOwnership(Restaurant restaurant, UserId requesterId) {
        if (isAdmin(requesterId)) return;

        if (!restaurant.getOwnerId().equals(requesterId))
            throw new UnauthorizedException("Only the owner can manage items of this restaurant.");
    }

    /// Validates whether the requester has the [UserRole#RESTAURANT_OWNER] role or is an admin.
    ///
    /// @param requesterId the [UserId] of the requester
    /// @throws NotFoundException     if no [User] or [UserType] is found for the requester
    /// @throws UnauthorizedException if the requester does not have the required role
    public void validateRestaurantOwnerRole(UserId requesterId) {
        var role = getRole(requesterId);

        if (role != UserRole.RESTAURANT_OWNER && role != UserRole.ADMIN)
            throw new UnauthorizedException("User is not a Restaurant Owner.");
    }

    /// Validates whether the requester has the [UserRole#RESTAURANT_OWNER] role.
    ///
    /// @param requesterId the [UserId] of the requester
    /// @throws NotFoundException     if no [User] or [UserType] is found for the requester
    /// @throws UnauthorizedException if the requester does not have the required role
    public void validateRestaurantTransferOwnership(UserId requesterId, UserId newOwnerId) {

        if (!this.isAdmin(requesterId))
            throw new UnauthorizedException("Only an admin can transfer restaurant ownership.");

        var newOwner = userGateway.findById(newOwnerId)
                .orElseThrow(() -> new NotFoundException("User", newOwnerId.getValue().toString()));

        var newOwnerType = userTypeGateway.findById(newOwner.getUserTypeId())
                .orElseThrow(() -> new NotFoundException("UserType", newOwner.getUserTypeId().getValue().toString()));

        if (newOwnerType.getRole() != UserRole.RESTAURANT_OWNER)
            throw new UnauthorizedException("New owner must have the RESTAURANT_OWNER role.");
    }

    /// Checks whether the requester has the [UserRole#ADMIN] role.
    ///
    /// @param requesterId the [UserId] of the requester
    /// @return `true` if the requester is an admin, `false` otherwise
    /// @throws NotFoundException if no [User] or [UserType] is found for the requester
    public boolean isAdmin(UserId requesterId) {
        return getRole(requesterId) == UserRole.ADMIN;
    }

    private UserRole getRole(UserId requesterId) {
        var user = userGateway.findById(requesterId)
                .orElseThrow(() -> new NotFoundException("User", requesterId.getValue().toString()));

        var userType = userTypeGateway.findById(user.getUserTypeId())
                .orElseThrow(() -> new NotFoundException("UserType", user.getUserTypeId().getValue().toString()));

        return userType.getRole();
    }
}
