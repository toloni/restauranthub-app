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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class CreateUserUseCaseTest {

    @Mock
    private UserGateway userGateway;

    @Mock
    private UserTypeGateway userTypeGateway;

    @InjectMocks
    private CreateUserUseCase useCase;

    private CreateUserUseCase.Input input;
    private UserTypeId userTypeId;

    @BeforeEach
    void setUp() {
        userTypeId = UserTypeId.generate();
        input = new CreateUserUseCase.Input(
                "John Doe",
                "john@example.com",
                "password123",
                userTypeId
        );
    }

    // -------------------------------------------------------------------------
    // Success
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should create User successfully")
    void shouldCreateUserSuccessfully() {
        var user = new User(
                UserId.generate(),
                UserName.of(input.name()),
                UserEmail.of(input.email()),
                UserPassword.of(input.password()),
                userTypeId
        );

        when(userGateway.existsByEmail(UserEmail.of(input.email()))).thenReturn(false);
        when(userTypeGateway.existsById(userTypeId)).thenReturn(true);
        when(userGateway.save(any(User.class))).thenReturn(user);

        var output = useCase.execute(input);

        assertThat(output).isNotNull();
        assertThat(output.id()).isNotNull();
        assertThat(output.name().getValue()).isEqualTo(input.name());
        assertThat(output.email().getValue()).isEqualTo(input.email());
        assertThat(output.userTypeId()).isEqualTo(userTypeId);
    }

    @Test
    @DisplayName("Should call gateway save once")
    void shouldCallGatewaySaveOnce() {
        var user = new User(
                UserId.generate(),
                UserName.of(input.name()),
                UserEmail.of(input.email()),
                UserPassword.of(input.password()),
                userTypeId
        );

        when(userGateway.existsByEmail(UserEmail.of(input.email()))).thenReturn(false);
        when(userTypeGateway.existsById(userTypeId)).thenReturn(true);
        when(userGateway.save(any(User.class))).thenReturn(user);

        useCase.execute(input);

        verify(userGateway, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should call existsByEmail with correct email")
    void shouldCallExistsByEmailWithCorrectEmail() {
        var user = new User(
                UserId.generate(),
                UserName.of(input.name()),
                UserEmail.of(input.email()),
                UserPassword.of(input.password()),
                userTypeId
        );

        when(userGateway.existsByEmail(UserEmail.of(input.email()))).thenReturn(false);
        when(userTypeGateway.existsById(userTypeId)).thenReturn(true);
        when(userGateway.save(any(User.class))).thenReturn(user);

        useCase.execute(input);

        verify(userGateway, times(1)).existsByEmail(UserEmail.of(input.email()));
    }

    @Test
    @DisplayName("Should normalize email to lowercase")
    void shouldNormalizeEmailToLowercase() {
        var uppercaseEmailInput = new CreateUserUseCase.Input(
                "John Doe",
                "JOHN@EXAMPLE.COM",
                "password123",
                userTypeId
        );
        var user = new User(
                UserId.generate(),
                UserName.of(uppercaseEmailInput.name()),
                UserEmail.of(uppercaseEmailInput.email()),
                UserPassword.of(uppercaseEmailInput.password()),
                userTypeId
        );

        when(userGateway.existsByEmail(any(UserEmail.class))).thenReturn(false);
        when(userTypeGateway.existsById(userTypeId)).thenReturn(true);
        when(userGateway.save(any(User.class))).thenReturn(user);

        var output = useCase.execute(uppercaseEmailInput);

        assertThat(output.email().getValue()).isEqualTo("john@example.com");
    }

    @Test
    @DisplayName("Should return output with generated id")
    void shouldReturnOutputWithGeneratedId() {
        var user = new User(
                UserId.generate(),
                UserName.of(input.name()),
                UserEmail.of(input.email()),
                UserPassword.of(input.password()),
                userTypeId
        );

        when(userGateway.existsByEmail(any(UserEmail.class))).thenReturn(false);
        when(userTypeGateway.existsById(userTypeId)).thenReturn(true);
        when(userGateway.save(any(User.class))).thenReturn(user);

        var output = useCase.execute(input);

        assertThat(output.id()).isNotNull();
        assertThat(output.id().getValue()).isNotNull();
    }

    // -------------------------------------------------------------------------
    // Failure
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should throw AlreadyExistsException when email already exists")
    void shouldThrowAlreadyExistsExceptionWhenEmailAlreadyExists() {
        when(userGateway.existsByEmail(UserEmail.of(input.email()))).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(AlreadyExistsException.class)
                .hasMessageContaining(input.email());
    }

    @Test
    @DisplayName("Should not call save when email already exists")
    void shouldNotCallSaveWhenEmailAlreadyExists() {
        when(userGateway.existsByEmail(UserEmail.of(input.email()))).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(AlreadyExistsException.class);

        verify(userGateway, never()).save(any());
    }

    @Test
    @DisplayName("Should throw DomainException when userTypeId is null")
    void shouldThrowDomainExceptionWhenUserTypeIdIsNull() {
        var invalidInput = new CreateUserUseCase.Input(
                "John Doe",
                "john@example.com",
                "password123",
                null
        );

        when(userGateway.existsByEmail(any(UserEmail.class))).thenReturn(false);

        assertThatThrownBy(() -> useCase.execute(invalidInput))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("User must have a type.");
    }

    @Test
    @DisplayName("Should throw NotFoundException when userTypeId does not exist")
    void shouldThrowNotFoundExceptionWhenUserTypeIdDoesNotExist() {
        when(userGateway.existsByEmail(any(UserEmail.class))).thenReturn(false);
        when(userTypeGateway.existsById(userTypeId)).thenReturn(false);

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(userTypeId.getValue().toString());
    }

    @Test
    @DisplayName("Should not call save when userTypeId does not exist")
    void shouldNotCallSaveWhenUserTypeIdDoesNotExist() {
        when(userGateway.existsByEmail(any(UserEmail.class))).thenReturn(false);
        when(userTypeGateway.existsById(userTypeId)).thenReturn(false);

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(NotFoundException.class);

        verify(userGateway, never()).save(any());
    }

    @Test
    @DisplayName("Should throw DomainException when name is blank")
    void shouldThrowDomainExceptionWhenNameIsBlank() {
        var invalidInput = new CreateUserUseCase.Input(
                "",
                "john@example.com",
                "password123",
                userTypeId
        );

        when(userGateway.existsByEmail(any(UserEmail.class))).thenReturn(false);
        when(userTypeGateway.existsById(userTypeId)).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(invalidInput))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("User name is required.");
    }

    @Test
    @DisplayName("Should throw DomainException when email is invalid")
    void shouldThrowDomainExceptionWhenEmailIsInvalid() {
        var invalidInput = new CreateUserUseCase.Input(
                "John Doe",
                "invalid-email",
                "password123",
                userTypeId
        );

        assertThatThrownBy(() -> useCase.execute(invalidInput))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("User email is invalid");
    }

    @Test
    @DisplayName("Should throw DomainException when password is too short")
    void shouldThrowDomainExceptionWhenPasswordIsTooShort() {
        var invalidInput = new CreateUserUseCase.Input(
                "John Doe",
                "john@example.com",
                "123",
                userTypeId
        );

        when(userGateway.existsByEmail(any(UserEmail.class))).thenReturn(false);
        when(userTypeGateway.existsById(userTypeId)).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(invalidInput))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("User password must be at least 8 characters.");
    }
}