package br.com.postechfiap.toloni.restauranthub.domain.usertype;

import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.DomainException;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.valueobject.UserTypeDescription;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.valueobject.UserTypeId;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.valueobject.UserTypeName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("unit")
class UserTypeTest {

    private UserTypeId id;
    private UserTypeName name;
    private UserTypeDescription description;

    @BeforeEach
    void setUp() {
        id = UserTypeId.generate();
        name = UserTypeName.of("Restaurant Owner");
        description = UserTypeDescription.of("Owns and manages a restaurant");
    }

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should create UserType with valid attributes")
    void shouldCreateUserTypeWithValidAttributes() {
        var userType = new UserType(id, name, description, UserRole.RESTAURANT_OWNER);

        assertThat(userType.getId()).isEqualTo(id);
        assertThat(userType.getName()).isEqualTo(name);
        assertThat(userType.getDescription()).isEqualTo(description);
        assertThat(userType.getRole()).isEqualTo(UserRole.RESTAURANT_OWNER);
    }

    @Test
    @DisplayName("Should throw DomainException when role is null")
    void shouldThrowDomainExceptionWhenRoleIsNull() {
        assertThatThrownBy(() -> new UserType(id, name, description, null))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("UserType role is required");
    }

    @Test
    @DisplayName("Should create UserType with CUSTOMER role")
    void shouldCreateUserTypeWithCustomerRole() {
        var userType = new UserType(id, name, description, UserRole.CUSTOMER);

        assertThat(userType.getRole()).isEqualTo(UserRole.CUSTOMER);
    }

    // -------------------------------------------------------------------------
    // Update
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should update name when new name is provided")
    void shouldUpdateNameWhenNewNameIsProvided() {
        var userType = new UserType(id, name, description, UserRole.RESTAURANT_OWNER);
        var newName = UserTypeName.of("Customer");

        userType.update(newName, null, null);

        assertThat(userType.getName()).isEqualTo(newName);
    }

    @Test
    @DisplayName("Should update description when new description is provided")
    void shouldUpdateDescriptionWhenNewDescriptionIsProvided() {
        var userType = new UserType(id, name, description, UserRole.RESTAURANT_OWNER);
        var newDescription = UserTypeDescription.of("New description");

        userType.update(null, newDescription, null);

        assertThat(userType.getDescription()).isEqualTo(newDescription);
    }

    @Test
    @DisplayName("Should update role when new role is different")
    void shouldUpdateRoleWhenNewRoleIsDifferent() {
        var userType = new UserType(id, name, description, UserRole.RESTAURANT_OWNER);

        userType.update(null, null, UserRole.CUSTOMER);

        assertThat(userType.getRole()).isEqualTo(UserRole.CUSTOMER);
    }

    @Test
    @DisplayName("Should not update role when new role is the same")
    void shouldNotUpdateRoleWhenNewRoleIsTheSame() {
        var userType = new UserType(id, name, description, UserRole.RESTAURANT_OWNER);

        userType.update(null, null, UserRole.RESTAURANT_OWNER);

        assertThat(userType.getRole()).isEqualTo(UserRole.RESTAURANT_OWNER);
    }

    @Test
    @DisplayName("Should not update name when null is provided")
    void shouldNotUpdateNameWhenNullIsProvided() {
        var userType = new UserType(id, name, description, UserRole.RESTAURANT_OWNER);

        userType.update(null, null, null);

        assertThat(userType.getName()).isEqualTo(name);
    }

    @Test
    @DisplayName("Should not update description when null is provided")
    void shouldNotUpdateDescriptionWhenNullIsProvided() {
        var userType = new UserType(id, name, description, UserRole.RESTAURANT_OWNER);

        userType.update(null, null, null);

        assertThat(userType.getDescription()).isEqualTo(description);
    }

    @Test
    @DisplayName("Should update all fields at once")
    void shouldUpdateAllFieldsAtOnce() {
        var userType = new UserType(id, name, description, UserRole.RESTAURANT_OWNER);
        var newName = UserTypeName.of("Customer");
        var newDescription = UserTypeDescription.of("Browses and orders food");

        userType.update(newName, newDescription, UserRole.CUSTOMER);

        assertThat(userType.getName()).isEqualTo(newName);
        assertThat(userType.getDescription()).isEqualTo(newDescription);
        assertThat(userType.getRole()).isEqualTo(UserRole.CUSTOMER);
    }

    @Test
    @DisplayName("Should keep id immutable after update")
    void shouldKeepIdImmutableAfterUpdate() {
        var userType = new UserType(id, name, description, UserRole.RESTAURANT_OWNER);

        userType.update(UserTypeName.of("Customer"), null, null);

        assertThat(userType.getId()).isEqualTo(id);
    }
}