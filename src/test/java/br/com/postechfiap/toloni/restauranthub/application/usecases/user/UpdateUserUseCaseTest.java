package br.com.postechfiap.toloni.restauranthub.application.usecases.user;

import br.com.postechfiap.toloni.restauranthub.application.gateways.UserGateway;
import br.com.postechfiap.toloni.restauranthub.application.gateways.UserTypeGateway;
import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.AlreadyExistsException;
import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.DomainException;
import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.NotFoundException;
import br.com.postechfiap.toloni.restauranthub.domain.user.User;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserEmail;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserId;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserName;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserPassword;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.valueobject.UserTypeId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class UpdateUserUseCaseTest {

    @Mock
    private UserGateway userGateway;

    @Mock
    private UserTypeGateway userTypeGateway;

    @InjectMocks
    private UpdateUserUseCase useCase;

    private UserId id;
    private UserTypeId userTypeId;
    private User user;

    @BeforeEach
    void setUp() {
        id = UserId.generate();
        userTypeId = UserTypeId.generate();
        user = new User(
                id,
                UserName.of("John Doe"),
                UserEmail.of("john@example.com"),
                UserPassword.of("password123"),
                userTypeId
        );
    }

    // -------------------------------------------------------------------------
    // Success
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should update User name successfully")
    void shouldUpdateUserNameSuccessfully() {
        var input = new UpdateUserUseCase.Input(id, "Jane Doe", null, null, null);
        var updatedUser = new User(id, UserName.of("Jane Doe"), UserEmail.of("john@example.com"),
                UserPassword.of("password123"), userTypeId);

        when(userGateway.findById(id)).thenReturn(Optional.of(user));
        when(userGateway.save(any(User.class))).thenReturn(updatedUser);

        var output = useCase.execute(input);

        assertThat(output.name().getValue()).isEqualTo("Jane Doe");
    }

    @Test
    @DisplayName("Should update User email successfully")
    void shouldUpdateUserEmailSuccessfully() {
        var input = new UpdateUserUseCase.Input(id, null, "jane@example.com", null, null);
        var updatedUser = new User(id, UserName.of("John Doe"), UserEmail.of("jane@example.com"),
                UserPassword.of("password123"), userTypeId);

        when(userGateway.findById(id)).thenReturn(Optional.of(user));
        when(userGateway.existsByEmailAndIdNot(UserEmail.of("jane@example.com"), id)).thenReturn(false);
        when(userGateway.save(any(User.class))).thenReturn(updatedUser);

        var output = useCase.execute(input);

        assertThat(output.email().getValue()).isEqualTo("jane@example.com");
    }

    @Test
    @DisplayName("Should update User password successfully")
    void shouldUpdateUserPasswordSuccessfully() {
        var input = new UpdateUserUseCase.Input(id, null, null, "newpassword123", null);
        var updatedUser = new User(id, UserName.of("John Doe"), UserEmail.of("john@example.com"),
                UserPassword.of("newpassword123"), userTypeId);

        when(userGateway.findById(id)).thenReturn(Optional.of(user));
        when(userGateway.save(any(User.class))).thenReturn(updatedUser);

        var output = useCase.execute(input);

        assertThat(output).isNotNull();
    }

    @Test
    @DisplayName("Should update User userTypeId successfully")
    void shouldUpdateUserUserTypeIdSuccessfully() {
        var newUserTypeId = UserTypeId.generate();
        var input = new UpdateUserUseCase.Input(id, null, null, null, newUserTypeId);
        var updatedUser = new User(id, UserName.of("John Doe"), UserEmail.of("john@example.com"),
                UserPassword.of("password123"), newUserTypeId);

        when(userGateway.findById(id)).thenReturn(Optional.of(user));
        when(userTypeGateway.existsById(newUserTypeId)).thenReturn(true);
        when(userGateway.save(any(User.class))).thenReturn(updatedUser);

        var output = useCase.execute(input);

        assertThat(output.userTypeId()).isEqualTo(newUserTypeId);
    }

    @Test
    @DisplayName("Should update all fields successfully")
    void shouldUpdateAllFieldsSuccessfully() {
        var newUserTypeId = UserTypeId.generate();
        var input = new UpdateUserUseCase.Input(id, "Jane Doe", "jane@example.com", "newpassword123", newUserTypeId);
        var updatedUser = new User(id, UserName.of("Jane Doe"), UserEmail.of("jane@example.com"),
                UserPassword.of("newpassword123"), newUserTypeId);

        when(userGateway.findById(id)).thenReturn(Optional.of(user));
        when(userGateway.existsByEmailAndIdNot(UserEmail.of("jane@example.com"), id)).thenReturn(false);
        when(userTypeGateway.existsById(newUserTypeId)).thenReturn(true);
        when(userGateway.save(any(User.class))).thenReturn(updatedUser);

        var output = useCase.execute(input);

        assertThat(output.name().getValue()).isEqualTo("Jane Doe");
        assertThat(output.email().getValue()).isEqualTo("jane@example.com");
        assertThat(output.userTypeId()).isEqualTo(newUserTypeId);
    }

    @Test
    @DisplayName("Should not check email uniqueness when email is null")
    void shouldNotCheckEmailUniquenessWhenEmailIsNull() {
        var input = new UpdateUserUseCase.Input(id, "Jane Doe", null, null, null);

        when(userGateway.findById(id)).thenReturn(Optional.of(user));
        when(userGateway.save(any(User.class))).thenReturn(user);

        useCase.execute(input);

        verify(userGateway, never()).existsByEmailAndIdNot(any(), any());
    }

    @Test
    @DisplayName("Should not check userTypeId when userTypeId is null")
    void shouldNotCheckUserTypeIdWhenUserTypeIdIsNull() {
        var input = new UpdateUserUseCase.Input(id, "Jane Doe", null, null, null);

        when(userGateway.findById(id)).thenReturn(Optional.of(user));
        when(userGateway.save(any(User.class))).thenReturn(user);

        useCase.execute(input);

        verify(userTypeGateway, never()).existsById(any());
    }

    @Test
    @DisplayName("Should call gateway save once")
    void shouldCallGatewaySaveOnce() {
        var input = new UpdateUserUseCase.Input(id, "Jane Doe", null, null, null);

        when(userGateway.findById(id)).thenReturn(Optional.of(user));
        when(userGateway.save(any(User.class))).thenReturn(user);

        useCase.execute(input);

        verify(userGateway, times(1)).save(any(User.class));
    }

    // -------------------------------------------------------------------------
    // Failure
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should throw NotFoundException when User is not found")
    void shouldThrowNotFoundExceptionWhenUserIsNotFound() {
        var input = new UpdateUserUseCase.Input(id, "Jane Doe", null, null, null);

        when(userGateway.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(id.getValue().toString());
    }

    @Test
    @DisplayName("Should not call save when User is not found")
    void shouldNotCallSaveWhenUserIsNotFound() {
        var input = new UpdateUserUseCase.Input(id, "Jane Doe", null, null, null);

        when(userGateway.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(NotFoundException.class);

        verify(userGateway, never()).save(any());
    }

    @Test
    @DisplayName("Should throw AlreadyExistsException when email already exists for another User")
    void shouldThrowAlreadyExistsExceptionWhenEmailAlreadyExistsForAnotherUser() {
        var input = new UpdateUserUseCase.Input(id, null, "jane@example.com", null, null);

        when(userGateway.findById(id)).thenReturn(Optional.of(user));
        when(userGateway.existsByEmailAndIdNot(UserEmail.of("jane@example.com"), id)).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(AlreadyExistsException.class)
                .hasMessageContaining("jane@example.com");
    }

    @Test
    @DisplayName("Should not call save when email already exists for another User")
    void shouldNotCallSaveWhenEmailAlreadyExistsForAnotherUser() {
        var input = new UpdateUserUseCase.Input(id, null, "jane@example.com", null, null);

        when(userGateway.findById(id)).thenReturn(Optional.of(user));
        when(userGateway.existsByEmailAndIdNot(UserEmail.of("jane@example.com"), id)).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(AlreadyExistsException.class);

        verify(userGateway, never()).save(any());
    }

    @Test
    @DisplayName("Should throw NotFoundException when userTypeId does not exist")
    void shouldThrowNotFoundExceptionWhenUserTypeIdDoesNotExist() {
        var newUserTypeId = UserTypeId.generate();
        var input = new UpdateUserUseCase.Input(id, null, null, null, newUserTypeId);

        when(userGateway.findById(id)).thenReturn(Optional.of(user));
        when(userTypeGateway.existsById(newUserTypeId)).thenReturn(false);

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(newUserTypeId.getValue().toString());
    }

    @Test
    @DisplayName("Should not call save when userTypeId does not exist")
    void shouldNotCallSaveWhenUserTypeIdDoesNotExist() {
        var newUserTypeId = UserTypeId.generate();
        var input = new UpdateUserUseCase.Input(id, null, null, null, newUserTypeId);

        when(userGateway.findById(id)).thenReturn(Optional.of(user));
        when(userTypeGateway.existsById(newUserTypeId)).thenReturn(false);

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(NotFoundException.class);

        verify(userGateway, never()).save(any());
    }

    @Test
    @DisplayName("Should throw DomainException when email is invalid")
    void shouldThrowDomainExceptionWhenEmailIsInvalid() {
        var input = new UpdateUserUseCase.Input(id, null, "invalid-email", null, null);

        when(userGateway.findById(id)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("User email is invalid");
    }

    @Test
    @DisplayName("Should throw DomainException when password is too short")
    void shouldThrowDomainExceptionWhenPasswordIsTooShort() {
        var input = new UpdateUserUseCase.Input(id, null, null, "123", null);

        when(userGateway.findById(id)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("User password must be at least 8 characters.");
    }
}