package br.com.postechfiap.toloni.restauranthub.domain.menuitem;

import br.com.postechfiap.toloni.restauranthub.domain.menuitem.valueobject.*;
import br.com.postechfiap.toloni.restauranthub.domain.restaurant.valueobject.RestaurantId;
import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.DomainException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("unit")
class MenuItemTest {

    private MenuItemId id;
    private MenuItemName name;
    private MenuItemDescription description;
    private MenuItemPrice price;
    private MenuItemImagePath imagePath;
    private RestaurantId restaurantId;

    @BeforeEach
    void setUp() {
        id = MenuItemId.generate();
        name = MenuItemName.of("Margherita Pizza");
        description = MenuItemDescription.of("Classic Italian pizza with tomato and mozzarella");
        price = MenuItemPrice.ofBRL(new BigDecimal("12.90"));
        imagePath = MenuItemImagePath.of("/images/pizza.jpg");
        restaurantId = RestaurantId.generate();
    }

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should create MenuItem with valid attributes")
    void shouldCreateMenuItemWithValidAttributes() {
        var menuItem = new MenuItem(id, name, description, price, false, imagePath, restaurantId);

        assertThat(menuItem.getId()).isEqualTo(id);
        assertThat(menuItem.getName()).isEqualTo(name);
        assertThat(menuItem.getDescription()).isEqualTo(description);
        assertThat(menuItem.getPrice()).isEqualTo(price);
        assertThat(menuItem.isDineInOnly()).isFalse();
        assertThat(menuItem.getImagePath()).isEqualTo(imagePath);
        assertThat(menuItem.getRestaurantId()).isEqualTo(restaurantId);
    }

    @Test
    @DisplayName("Should create MenuItem with dineInOnly true")
    void shouldCreateMenuItemWithDineInOnlyTrue() {
        var menuItem = new MenuItem(id, name, description, price, true, imagePath, restaurantId);

        assertThat(menuItem.isDineInOnly()).isTrue();
    }

    @Test
    @DisplayName("Should create MenuItem without imagePath")
    void shouldCreateMenuItemWithoutImagePath() {
        var menuItem = new MenuItem(id, name, description, price, false, MenuItemImagePath.of(null), restaurantId);

        assertThat(menuItem.getImagePath().isPresent()).isFalse();
    }

    @Test
    @DisplayName("Should throw DomainException when restaurantId is null")
    void shouldThrowDomainExceptionWhenRestaurantIdIsNull() {
        assertThatThrownBy(() -> new MenuItem(id, name, description, price, false, imagePath, null))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("MenuItem must belong to a restaurant.");
    }

    @Test
    @DisplayName("Should keep restaurantId immutable after construction")
    void shouldKeepRestaurantIdImmutableAfterConstruction() {
        var menuItem = new MenuItem(id, name, description, price, false, imagePath, restaurantId);

        assertThat(menuItem.getRestaurantId()).isEqualTo(restaurantId);
    }

    // -------------------------------------------------------------------------
    // Update
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should update name when new name is provided")
    void shouldUpdateNameWhenNewNameIsProvided() {
        var menuItem = new MenuItem(id, name, description, price, false, imagePath, restaurantId);
        var newName = MenuItemName.of("Pepperoni Pizza");

        menuItem.update(newName, null, null, null, null);

        assertThat(menuItem.getName()).isEqualTo(newName);
    }

    @Test
    @DisplayName("Should update description when new description is provided")
    void shouldUpdateDescriptionWhenNewDescriptionIsProvided() {
        var menuItem = new MenuItem(id, name, description, price, false, imagePath, restaurantId);
        var newDescription = MenuItemDescription.of("Spicy pepperoni with mozzarella");

        menuItem.update(null, newDescription, null, null, null);

        assertThat(menuItem.getDescription()).isEqualTo(newDescription);
    }

    @Test
    @DisplayName("Should update price when new price is provided")
    void shouldUpdatePriceWhenNewPriceIsProvided() {
        var menuItem = new MenuItem(id, name, description, price, false, imagePath, restaurantId);
        var newPrice = MenuItemPrice.ofBRL(new BigDecimal("15.90"));

        menuItem.update(null, null, newPrice, null, null);

        assertThat(menuItem.getPrice()).isEqualTo(newPrice);
    }

    @Test
    @DisplayName("Should update dineInOnly when new value is provided")
    void shouldUpdateDineInOnlyWhenNewValueIsProvided() {
        var menuItem = new MenuItem(id, name, description, price, false, imagePath, restaurantId);

        menuItem.update(null, null, null, true, null);

        assertThat(menuItem.isDineInOnly()).isTrue();
    }

    @Test
    @DisplayName("Should update imagePath when new imagePath is provided")
    void shouldUpdateImagePathWhenNewImagePathIsProvided() {
        var menuItem = new MenuItem(id, name, description, price, false, imagePath, restaurantId);
        var newImagePath = MenuItemImagePath.of("/images/pepperoni.jpg");

        menuItem.update(null, null, null, null, newImagePath);

        assertThat(menuItem.getImagePath()).isEqualTo(newImagePath);
    }

    @Test
    @DisplayName("Should not update name when null is provided")
    void shouldNotUpdateNameWhenNullIsProvided() {
        var menuItem = new MenuItem(id, name, description, price, false, imagePath, restaurantId);

        menuItem.update(null, null, null, null, null);

        assertThat(menuItem.getName()).isEqualTo(name);
    }

    @Test
    @DisplayName("Should not update description when null is provided")
    void shouldNotUpdateDescriptionWhenNullIsProvided() {
        var menuItem = new MenuItem(id, name, description, price, false, imagePath, restaurantId);

        menuItem.update(null, null, null, null, null);

        assertThat(menuItem.getDescription()).isEqualTo(description);
    }

    @Test
    @DisplayName("Should not update price when null is provided")
    void shouldNotUpdatePriceWhenNullIsProvided() {
        var menuItem = new MenuItem(id, name, description, price, false, imagePath, restaurantId);

        menuItem.update(null, null, null, null, null);

        assertThat(menuItem.getPrice()).isEqualTo(price);
    }

    @Test
    @DisplayName("Should not update dineInOnly when null is provided")
    void shouldNotUpdateDineInOnlyWhenNullIsProvided() {
        var menuItem = new MenuItem(id, name, description, price, false, imagePath, restaurantId);

        menuItem.update(null, null, null, null, null);

        assertThat(menuItem.isDineInOnly()).isFalse();
    }

    @Test
    @DisplayName("Should not update imagePath when null is provided")
    void shouldNotUpdateImagePathWhenNullIsProvided() {
        var menuItem = new MenuItem(id, name, description, price, false, imagePath, restaurantId);

        menuItem.update(null, null, null, null, null);

        assertThat(menuItem.getImagePath()).isEqualTo(imagePath);
    }

    @Test
    @DisplayName("Should update all fields at once")
    void shouldUpdateAllFieldsAtOnce() {
        var menuItem = new MenuItem(id, name, description, price, false, imagePath, restaurantId);
        var newName = MenuItemName.of("Pepperoni Pizza");
        var newDescription = MenuItemDescription.of("Spicy pepperoni with mozzarella");
        var newPrice = MenuItemPrice.ofBRL(new BigDecimal("15.90"));
        var newImagePath = MenuItemImagePath.of("/images/pepperoni.jpg");

        menuItem.update(newName, newDescription, newPrice, true, newImagePath);

        assertThat(menuItem.getName()).isEqualTo(newName);
        assertThat(menuItem.getDescription()).isEqualTo(newDescription);
        assertThat(menuItem.getPrice()).isEqualTo(newPrice);
        assertThat(menuItem.isDineInOnly()).isTrue();
        assertThat(menuItem.getImagePath()).isEqualTo(newImagePath);
    }

    @Test
    @DisplayName("Should keep id immutable after update")
    void shouldKeepIdImmutableAfterUpdate() {
        var menuItem = new MenuItem(id, name, description, price, false, imagePath, restaurantId);

        menuItem.update(MenuItemName.of("Pepperoni Pizza"), null, null, null, null);

        assertThat(menuItem.getId()).isEqualTo(id);
    }

    @Test
    @DisplayName("Should keep restaurantId immutable after update")
    void shouldKeepRestaurantIdImmutableAfterUpdate() {
        var menuItem = new MenuItem(id, name, description, price, false, imagePath, restaurantId);

        menuItem.update(MenuItemName.of("Pepperoni Pizza"), null, null, null, null);

        assertThat(menuItem.getRestaurantId()).isEqualTo(restaurantId);
    }
}