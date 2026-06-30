package br.com.postechfiap.toloni.restauranthub.domain.user;

import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.DomainException;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserEmail;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserId;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserName;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserPassword;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.valueobject.UserTypeId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("unit")
class UserTest {

    private UserId id;
    private UserName name;
    private UserEmail email;
    private UserPassword password;
    private UserTypeId userTypeId;

    @BeforeEach
    void setUp() {
        id = UserId.generate();
        name = UserName.of("John Doe");
        email = UserEmail.of("john@example.com");
        password = UserPassword.of("secret123");
        userTypeId = UserTypeId.generate();
    }

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should create User with valid attributes")
    void shouldCreateUserWithValidAttributes() {
        var user = new User(id, name, email, password, userTypeId);

        assertThat(user.getId()).isEqualTo(id);
        assertThat(user.getName()).isEqualTo(name);
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getPassword()).isEqualTo(password);
        assertThat(user.getUserTypeId()).isEqualTo(userTypeId);
    }

    @Test
    @DisplayName("Should throw DomainException when userTypeId is null")
    void shouldThrowDomainExceptionWhenUserTypeIdIsNull() {
        assertThatThrownBy(() -> new User(id, name, email, password, null))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("User must have a type.");
    }

    // -------------------------------------------------------------------------
    // Update
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should update name when new name is provided")
    void shouldUpdateNameWhenNewNameIsProvided() {
        var user = new User(id, name, email, password, userTypeId);
        var newName = UserName.of("Jane Doe");

        user.update(newName, null, null, null);

        assertThat(user.getName()).isEqualTo(newName);
    }

    @Test
    @DisplayName("Should update email when new email is provided")
    void shouldUpdateEmailWhenNewEmailIsProvided() {
        var user = new User(id, name, email, password, userTypeId);
        var newEmail = UserEmail.of("jane@example.com");

        user.update(null, newEmail, null, null);

        assertThat(user.getEmail()).isEqualTo(newEmail);
    }

    @Test
    @DisplayName("Should update password when new password is provided")
    void shouldUpdatePasswordWhenNewPasswordIsProvided() {
        var user = new User(id, name, email, password, userTypeId);
        var newPassword = UserPassword.of("newpassword123");

        user.update(null, null, newPassword, null);

        assertThat(user.getPassword()).isEqualTo(newPassword);
    }

    @Test
    @DisplayName("Should update userTypeId when new userTypeId is provided")
    void shouldUpdateUserTypeIdWhenNewUserTypeIdIsProvided() {
        var user = new User(id, name, email, password, userTypeId);
        var newUserTypeId = UserTypeId.generate();

        user.update(null, null, null, newUserTypeId);

        assertThat(user.getUserTypeId()).isEqualTo(newUserTypeId);
    }

    @Test
    @DisplayName("Should not update name when null is provided")
    void shouldNotUpdateNameWhenNullIsProvided() {
        var user = new User(id, name, email, password, userTypeId);

        user.update(null, null, null, null);

        assertThat(user.getName()).isEqualTo(name);
    }

    @Test
    @DisplayName("Should not update email when null is provided")
    void shouldNotUpdateEmailWhenNullIsProvided() {
        var user = new User(id, name, email, password, userTypeId);

        user.update(null, null, null, null);

        assertThat(user.getEmail()).isEqualTo(email);
    }

    @Test
    @DisplayName("Should not update password when null is provided")
    void shouldNotUpdatePasswordWhenNullIsProvided() {
        var user = new User(id, name, email, password, userTypeId);

        user.update(null, null, null, null);

        assertThat(user.getPassword()).isEqualTo(password);
    }

    @Test
    @DisplayName("Should not update userTypeId when null is provided")
    void shouldNotUpdateUserTypeIdWhenNullIsProvided() {
        var user = new User(id, name, email, password, userTypeId);

        user.update(null, null, null, null);

        assertThat(user.getUserTypeId()).isEqualTo(userTypeId);
    }

    @Test
    @DisplayName("Should update all fields at once")
    void shouldUpdateAllFieldsAtOnce() {
        var user = new User(id, name, email, password, userTypeId);
        var newName = UserName.of("Jane Doe");
        var newEmail = UserEmail.of("jane@example.com");
        var newPassword = UserPassword.of("newpassword123");
        var newUserTypeId = UserTypeId.generate();

        user.update(newName, newEmail, newPassword, newUserTypeId);

        assertThat(user.getName()).isEqualTo(newName);
        assertThat(user.getEmail()).isEqualTo(newEmail);
        assertThat(user.getPassword()).isEqualTo(newPassword);
        assertThat(user.getUserTypeId()).isEqualTo(newUserTypeId);
    }

    @Test
    @DisplayName("Should keep id immutable after update")
    void shouldKeepIdImmutableAfterUpdate() {
        var user = new User(id, name, email, password, userTypeId);

        user.update(UserName.of("Jane Doe"), null, null, null);

        assertThat(user.getId()).isEqualTo(id);
    }
}