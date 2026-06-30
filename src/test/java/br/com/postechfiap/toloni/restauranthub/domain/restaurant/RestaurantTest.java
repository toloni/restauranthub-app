package br.com.postechfiap.toloni.restauranthub.domain.restaurant;

import br.com.postechfiap.toloni.restauranthub.domain.restaurant.valueobject.*;
import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.DomainException;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("unit")
class RestaurantTest {

    private RestaurantId id;
    private RestaurantName name;
    private RestaurantAddress address;
    private RestaurantCuisineType cuisineType;
    private RestaurantOpeningHours openingHours;
    private UserId ownerId;

    @BeforeEach
    void setUp() {
        id = RestaurantId.generate();
        name = RestaurantName.of("The Great Burger");
        address = RestaurantAddress.of("123 Main St, Springfield");
        cuisineType = RestaurantCuisineType.of("American");
        openingHours = RestaurantOpeningHours.of("Mon-Fri 9am-10pm");
        ownerId = UserId.generate();
    }

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should create Restaurant with valid attributes")
    void shouldCreateRestaurantWithValidAttributes() {
        var restaurant = new Restaurant(id, name, address, cuisineType, openingHours, ownerId);

        assertThat(restaurant.getId()).isEqualTo(id);
        assertThat(restaurant.getName()).isEqualTo(name);
        assertThat(restaurant.getAddress()).isEqualTo(address);
        assertThat(restaurant.getCuisineType()).isEqualTo(cuisineType);
        assertThat(restaurant.getOpeningHours()).isEqualTo(openingHours);
        assertThat(restaurant.getOwnerId()).isEqualTo(ownerId);
    }

    @Test
    @DisplayName("Should throw DomainException when ownerId is null")
    void shouldThrowDomainExceptionWhenOwnerIdIsNull() {
        assertThatThrownBy(() -> new Restaurant(id, name, address, cuisineType, openingHours, null))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("Restaurant must have an owner.");
    }

    // -------------------------------------------------------------------------
    // Update
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should update name when new name is provided")
    void shouldUpdateNameWhenNewNameIsProvided() {
        var restaurant = new Restaurant(id, name, address, cuisineType, openingHours, ownerId);
        var newName = RestaurantName.of("La Bella Italia");

        restaurant.update(newName, null, null, null, null);

        assertThat(restaurant.getName()).isEqualTo(newName);
    }

    @Test
    @DisplayName("Should update address when new address is provided")
    void shouldUpdateAddressWhenNewAddressIsProvided() {
        var restaurant = new Restaurant(id, name, address, cuisineType, openingHours, ownerId);
        var newAddress = RestaurantAddress.of("456 Olive Ave, New York");

        restaurant.update(null, newAddress, null, null, null);

        assertThat(restaurant.getAddress()).isEqualTo(newAddress);
    }

    @Test
    @DisplayName("Should update cuisineType when new cuisineType is provided")
    void shouldUpdateCuisineTypeWhenNewCuisineTypeIsProvided() {
        var restaurant = new Restaurant(id, name, address, cuisineType, openingHours, ownerId);
        var newCuisineType = RestaurantCuisineType.of("Italian");

        restaurant.update(null, null, newCuisineType, null, null);

        assertThat(restaurant.getCuisineType()).isEqualTo(newCuisineType);
    }

    @Test
    @DisplayName("Should update openingHours when new openingHours is provided")
    void shouldUpdateOpeningHoursWhenNewOpeningHoursIsProvided() {
        var restaurant = new Restaurant(id, name, address, cuisineType, openingHours, ownerId);
        var newOpeningHours = RestaurantOpeningHours.of("Tue-Sun 11am-11pm");

        restaurant.update(null, null, null, newOpeningHours, null);

        assertThat(restaurant.getOpeningHours()).isEqualTo(newOpeningHours);
    }

    @Test
    @DisplayName("Should update ownerId when new ownerId is provided")
    void shouldUpdateOwnerIdWhenNewOwnerIdIsProvided() {
        var restaurant = new Restaurant(id, name, address, cuisineType, openingHours, ownerId);
        var newOwnerId = UserId.generate();

        restaurant.update(null, null, null, null, newOwnerId);

        assertThat(restaurant.getOwnerId()).isEqualTo(newOwnerId);
    }

    @Test
    @DisplayName("Should not update name when null is provided")
    void shouldNotUpdateNameWhenNullIsProvided() {
        var restaurant = new Restaurant(id, name, address, cuisineType, openingHours, ownerId);

        restaurant.update(null, null, null, null, null);

        assertThat(restaurant.getName()).isEqualTo(name);
    }

    @Test
    @DisplayName("Should not update address when null is provided")
    void shouldNotUpdateAddressWhenNullIsProvided() {
        var restaurant = new Restaurant(id, name, address, cuisineType, openingHours, ownerId);

        restaurant.update(null, null, null, null, null);

        assertThat(restaurant.getAddress()).isEqualTo(address);
    }

    @Test
    @DisplayName("Should not update cuisineType when null is provided")
    void shouldNotUpdateCuisineTypeWhenNullIsProvided() {
        var restaurant = new Restaurant(id, name, address, cuisineType, openingHours, ownerId);

        restaurant.update(null, null, null, null, null);

        assertThat(restaurant.getCuisineType()).isEqualTo(cuisineType);
    }

    @Test
    @DisplayName("Should not update openingHours when null is provided")
    void shouldNotUpdateOpeningHoursWhenNullIsProvided() {
        var restaurant = new Restaurant(id, name, address, cuisineType, openingHours, ownerId);

        restaurant.update(null, null, null, null, null);

        assertThat(restaurant.getOpeningHours()).isEqualTo(openingHours);
    }

    @Test
    @DisplayName("Should not update ownerId when null is provided")
    void shouldNotUpdateOwnerIdWhenNullIsProvided() {
        var restaurant = new Restaurant(id, name, address, cuisineType, openingHours, ownerId);

        restaurant.update(null, null, null, null, null);

        assertThat(restaurant.getOwnerId()).isEqualTo(ownerId);
    }

    @Test
    @DisplayName("Should update all fields at once")
    void shouldUpdateAllFieldsAtOnce() {
        var restaurant = new Restaurant(id, name, address, cuisineType, openingHours, ownerId);
        var newName = RestaurantName.of("La Bella Italia");
        var newAddress = RestaurantAddress.of("456 Olive Ave, New York");
        var newCuisineType = RestaurantCuisineType.of("Italian");
        var newOpeningHours = RestaurantOpeningHours.of("Tue-Sun 11am-11pm");
        var newOwnerId = UserId.generate();

        restaurant.update(newName, newAddress, newCuisineType, newOpeningHours, newOwnerId);

        assertThat(restaurant.getName()).isEqualTo(newName);
        assertThat(restaurant.getAddress()).isEqualTo(newAddress);
        assertThat(restaurant.getCuisineType()).isEqualTo(newCuisineType);
        assertThat(restaurant.getOpeningHours()).isEqualTo(newOpeningHours);
        assertThat(restaurant.getOwnerId()).isEqualTo(newOwnerId);
    }

    @Test
    @DisplayName("Should keep id immutable after update")
    void shouldKeepIdImmutableAfterUpdate() {
        var restaurant = new Restaurant(id, name, address, cuisineType, openingHours, ownerId);

        restaurant.update(RestaurantName.of("La Bella Italia"), null, null, null, null);

        assertThat(restaurant.getId()).isEqualTo(id);
    }
}