package br.com.postechfiap.toloni.restauranthub.application.usecases.usertype;

import br.com.postechfiap.toloni.restauranthub.application.gateways.UserTypeGateway;
import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.AlreadyExistsException;
import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.DomainException;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.UserRole;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.UserType;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class CreateUserTypeUseCaseTest {

    @Mock
    private UserTypeGateway userTypeGateway;

    @InjectMocks
    private CreateUserTypeUseCase useCase;

    private CreateUserTypeUseCase.Input input;

    @BeforeEach
    void setUp() {
        input = new CreateUserTypeUseCase.Input(
                "Restaurant Owner",
                "Owns and manages a restaurant",
                UserRole.RESTAURANT_OWNER
        );
    }

    // -------------------------------------------------------------------------
    // Success
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should create UserType successfully")
    void shouldCreateUserTypeSuccessfully() {
        var userType = new UserType(
                UserTypeId.generate(),
                UserTypeName.of(input.name()),
                UserTypeDescription.of(input.description()),
                input.role()
        );

        when(userTypeGateway.existsByRole(input.role())).thenReturn(false);
        when(userTypeGateway.save(any(UserType.class))).thenReturn(userType);

        var output = useCase.execute(input);

        assertThat(output).isNotNull();
        assertThat(output.id()).isNotNull();
        assertThat(output.name().getValue()).isEqualTo(input.name());
        assertThat(output.description().getValue()).isEqualTo(input.description());
        assertThat(output.role()).isEqualTo(input.role());
    }

    @Test
    @DisplayName("Should create UserType with CUSTOMER role successfully")
    void shouldCreateUserTypeWithCustomerRoleSuccessfully() {
        var customerInput = new CreateUserTypeUseCase.Input(
                "Customer",
                "Browses and orders food",
                UserRole.CUSTOMER
        );
        var userType = new UserType(
                UserTypeId.generate(),
                UserTypeName.of(customerInput.name()),
                UserTypeDescription.of(customerInput.description()),
                customerInput.role()
        );

        when(userTypeGateway.existsByRole(customerInput.role())).thenReturn(false);
        when(userTypeGateway.save(any(UserType.class))).thenReturn(userType);

        var output = useCase.execute(customerInput);

        assertThat(output.role()).isEqualTo(UserRole.CUSTOMER);
    }

    @Test
    @DisplayName("Should call gateway save once")
    void shouldCallGatewaySaveOnce() {
        var userType = new UserType(
                UserTypeId.generate(),
                UserTypeName.of(input.name()),
                UserTypeDescription.of(input.description()),
                input.role()
        );

        when(userTypeGateway.existsByRole(input.role())).thenReturn(false);
        when(userTypeGateway.save(any(UserType.class))).thenReturn(userType);

        useCase.execute(input);

        verify(userTypeGateway, times(1)).save(any(UserType.class));
    }

    @Test
    @DisplayName("Should call existsByRole with correct role")
    void shouldCallExistsByRoleWithCorrectRole() {
        var userType = new UserType(
                UserTypeId.generate(),
                UserTypeName.of(input.name()),
                UserTypeDescription.of(input.description()),
                input.role()
        );

        when(userTypeGateway.existsByRole(input.role())).thenReturn(false);
        when(userTypeGateway.save(any(UserType.class))).thenReturn(userType);

        useCase.execute(input);

        verify(userTypeGateway, times(1)).existsByRole(UserRole.RESTAURANT_OWNER);
    }

    @Test
    @DisplayName("Should return output with generated id")
    void shouldReturnOutputWithGeneratedId() {
        var userType = new UserType(
                UserTypeId.generate(),
                UserTypeName.of(input.name()),
                UserTypeDescription.of(input.description()),
                input.role()
        );

        when(userTypeGateway.existsByRole(input.role())).thenReturn(false);
        when(userTypeGateway.save(any(UserType.class))).thenReturn(userType);

        var output = useCase.execute(input);

        assertThat(output.id()).isNotNull();
        assertThat(output.id().getValue()).isNotNull();
    }

    // -------------------------------------------------------------------------
    // Failure
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should throw AlreadyExistsException when role already exists")
    void shouldThrowAlreadyExistsExceptionWhenRoleAlreadyExists() {
        when(userTypeGateway.existsByRole(input.role())).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(AlreadyExistsException.class)
                .hasMessageContaining("RESTAURANT_OWNER");
    }

    @Test
    @DisplayName("Should not call save when role already exists")
    void shouldNotCallSaveWhenRoleAlreadyExists() {
        when(userTypeGateway.existsByRole(input.role())).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(AlreadyExistsException.class);

        verify(userTypeGateway, never()).save(any(UserType.class));
    }

    @Test
    @DisplayName("Should throw DomainException when name is blank")
    void shouldThrowDomainExceptionWhenNameIsBlank() {
        var invalidInput = new CreateUserTypeUseCase.Input(
                "",
                "Owns and manages a restaurant",
                UserRole.RESTAURANT_OWNER
        );

        when(userTypeGateway.existsByRole(invalidInput.role())).thenReturn(false);

        assertThatThrownBy(() -> useCase.execute(invalidInput))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("UserType name is required.");
    }

    @Test
    @DisplayName("Should throw DomainException when description is blank")
    void shouldThrowDomainExceptionWhenDescriptionIsBlank() {
        var invalidInput = new CreateUserTypeUseCase.Input(
                "Restaurant Owner",
                "",
                UserRole.RESTAURANT_OWNER
        );

        when(userTypeGateway.existsByRole(invalidInput.role())).thenReturn(false);

        assertThatThrownBy(() -> useCase.execute(invalidInput))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("UserType description is required.");
    }

    @Test
    @DisplayName("Should throw DomainException when name is null")
    void shouldThrowDomainExceptionWhenNameIsNull() {
        var invalidInput = new CreateUserTypeUseCase.Input(
                null,
                "Owns and manages a restaurant",
                UserRole.RESTAURANT_OWNER
        );

        when(userTypeGateway.existsByRole(invalidInput.role())).thenReturn(false);

        assertThatThrownBy(() -> useCase.execute(invalidInput))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("UserType name is required.");
    }

    @Test
    @DisplayName("Should throw DomainException when description is null")
    void shouldThrowDomainExceptionWhenDescriptionIsNull() {
        var invalidInput = new CreateUserTypeUseCase.Input(
                "Restaurant Owner",
                null,
                UserRole.RESTAURANT_OWNER
        );

        when(userTypeGateway.existsByRole(invalidInput.role())).thenReturn(false);

        assertThatThrownBy(() -> useCase.execute(invalidInput))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("UserType description is required.");
    }
}