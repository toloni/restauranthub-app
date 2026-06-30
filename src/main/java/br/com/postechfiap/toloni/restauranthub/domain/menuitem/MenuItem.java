package br.com.postechfiap.toloni.restauranthub.domain.menuitem;

import br.com.postechfiap.toloni.restauranthub.domain.menuitem.valueobject.*;
import br.com.postechfiap.toloni.restauranthub.domain.restaurant.Restaurant;
import br.com.postechfiap.toloni.restauranthub.domain.restaurant.valueobject.RestaurantId;
import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.DomainException;

/// Represents a menu item in the domain layer.
///
/// A `MenuItem` is an entity that belongs to a [Restaurant], identified
/// by its [RestaurantId]. It holds culinary details, pricing with currency,
/// availability, and an optional image. It enforces domain invariants on
/// construction and allows controlled mutation via [#update].
///
/// ## Example
/// ```java
/// var menuItem = new MenuItem(
///     MenuItemId.generate(),
///     MenuItemName.of("Margherita Pizza"),
///     MenuItemDescription.of("Classic Italian pizza with tomato and mozzarella"),
///     MenuItemPrice.ofBRL(new BigDecimal("12.90")),
///     false,
///     MenuItemImagePath.of("/images/pizza.jpg"),
///     restaurantId
/// );
/// ```
public class MenuItem {

    private final MenuItemId id;
    private MenuItemName name;
    private MenuItemDescription description;
    private MenuItemPrice price;
    private boolean dineInOnly;
    private MenuItemImagePath imagePath;
    private final RestaurantId restaurantId;

    /// Creates a new `MenuItem` with the given attributes.
    ///
    /// @param id           the unique identifier of this menu item
    /// @param name         the name of this menu item
    /// @param description  the description of this menu item
    /// @param price        the price of this menu item, including currency
    /// @param dineInOnly   whether this item is available for dine-in only
    /// @param imagePath    the image path of this menu item, may be null
    /// @param restaurantId the [RestaurantId] referencing the restaurant this item belongs to
    /// @throws DomainException if `restaurantId` is `null`
    public MenuItem(MenuItemId id, MenuItemName name, MenuItemDescription description,
                    MenuItemPrice price, boolean dineInOnly, MenuItemImagePath imagePath,
                    RestaurantId restaurantId) {
        if (restaurantId == null)
            throw new DomainException("MenuItem must belong to a restaurant.");
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.dineInOnly = dineInOnly;
        this.imagePath = imagePath;
        this.restaurantId = restaurantId;
    }

    /// Updates the mutable attributes of this `MenuItem`.
    ///
    /// Only non-null values are applied. If a parameter is `null`,
    /// the corresponding field remains unchanged.
    ///
    /// @param name        the new name, or `null` to keep the current value
    /// @param description the new description, or `null` to keep the current value
    /// @param price       the new price with currency, or `null` to keep the current value
    /// @param dineInOnly  the new dine-in only flag, or `null` to keep the current value
    /// @param imagePath   the new image path, or `null` to keep the current value
    public void update(MenuItemName name, MenuItemDescription description, MenuItemPrice price,
                       Boolean dineInOnly, MenuItemImagePath imagePath) {
        if (name != null) this.name = name;
        if (description != null) this.description = description;
        if (price != null) this.price = price;
        if (dineInOnly != null) this.dineInOnly = dineInOnly;
        if (imagePath != null) this.imagePath = imagePath;
    }

    /// @return the unique identifier of this menu item
    public MenuItemId getId() {
        return id;
    }

    /// @return the name of this menu item
    public MenuItemName getName() {
        return name;
    }

    /// @return the description of this menu item
    public MenuItemDescription getDescription() {
        return description;
    }

    /// @return the price of this menu item, including currency and correct scale
    public MenuItemPrice getPrice() {
        return price;
    }

    /// @return `true` if this item is available for dine-in only, `false` otherwise
    public boolean isDineInOnly() {
        return dineInOnly;
    }

    /// @return the image path of this menu item
    public MenuItemImagePath getImagePath() {
        return imagePath;
    }

    /// @return the [RestaurantId] referencing the restaurant this item belongs to
    public RestaurantId getRestaurantId() {
        return restaurantId;
    }
}
