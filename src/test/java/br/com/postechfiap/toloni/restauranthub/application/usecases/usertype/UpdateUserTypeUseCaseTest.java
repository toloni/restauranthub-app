package br.com.postechfiap.toloni.restauranthub.application.usecases.usertype;

import br.com.postechfiap.toloni.restauranthub.application.gateways.UserTypeGateway;
import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.AlreadyExistsException;
import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.DomainException;
import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.NotFoundException;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class UpdateUserTypeUseCaseTest {

    @Mock
    private UserTypeGateway userTypeGateway;

    @InjectMocks
    private UpdateUserTypeUseCase useCase;

    private UserTypeId id;
    private UserType userType;

    @BeforeEach
    void setUp() {
        id = UserTypeId.generate();
        userType = new UserType(
                id,
                UserTypeName.of("Restaurant Owner"),
                UserTypeDescription.of("Owns and manages a restaurant"),
                UserRole.RESTAURANT_OWNER
        );
    }

    // -------------------------------------------------------------------------
    // Success
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should update UserType name successfully")
    void shouldUpdateUserTypeNameSuccessfully() {
        var input = new UpdateUserTypeUseCase.Input(id, "New Name", null, null);

        when(userTypeGateway.findById(id)).thenReturn(Optional.of(userType));
        when(userTypeGateway.save(any(UserType.class))).thenReturn(userType);

        var output = useCase.execute(input);

        assertThat(output).isNotNull();
        assertThat(output.id()).isEqualTo(id);
    }

    @Test
    @DisplayName("Should update UserType description successfully")
    void shouldUpdateUserTypeDescriptionSuccessfully() {
        var input = new UpdateUserTypeUseCase.Input(id, null, "New Description", null);

        when(userTypeGateway.findById(id)).thenReturn(Optional.of(userType));
        when(userTypeGateway.save(any(UserType.class))).thenReturn(userType);

        var output = useCase.execute(input);

        assertThat(output).isNotNull();
    }

    @Test
    @DisplayName("Should update UserType role successfully")
    void shouldUpdateUserTypeRoleSuccessfully() {
        var input = new UpdateUserTypeUseCase.Input(id, null, null, UserRole.CUSTOMER);
        var updatedUserType = new UserType(
                id,
                UserTypeName.of("Restaurant Owner"),
                UserTypeDescription.of("Owns and manages a restaurant"),
                UserRole.CUSTOMER
        );

        when(userTypeGateway.findById(id)).thenReturn(Optional.of(userType));
        when(userTypeGateway.existsByRoleAndIdNot(UserRole.CUSTOMER, id)).thenReturn(false);
        when(userTypeGateway.save(any(UserType.class))).thenReturn(updatedUserType);

        var output = useCase.execute(input);

        assertThat(output.role()).isEqualTo(UserRole.CUSTOMER);
    }

    @Test
    @DisplayName("Should update all fields successfully")
    void shouldUpdateAllFieldsSuccessfully() {
        var input = new UpdateUserTypeUseCase.Input(id, "Customer", "Browses and orders food", UserRole.CUSTOMER);
        var updatedUserType = new UserType(
                id,
                UserTypeName.of("Customer"),
                UserTypeDescription.of("Browses and orders food"),
                UserRole.CUSTOMER
        );

        when(userTypeGateway.findById(id)).thenReturn(Optional.of(userType));
        when(userTypeGateway.existsByRoleAndIdNot(UserRole.CUSTOMER, id)).thenReturn(false);
        when(userTypeGateway.save(any(UserType.class))).thenReturn(updatedUserType);

        var output = useCase.execute(input);

        assertThat(output.name().getValue()).isEqualTo("Customer");
        assertThat(output.description().getValue()).isEqualTo("Browses and orders food");
        assertThat(output.role()).isEqualTo(UserRole.CUSTOMER);
    }

    @Test
    @DisplayName("Should update UserType without changing role when role is null")
    void shouldUpdateUserTypeWithoutChangingRoleWhenRoleIsNull() {
        var input = new UpdateUserTypeUseCase.Input(id, "New Name", null, null);

        when(userTypeGateway.findById(id)).thenReturn(Optional.of(userType));
        when(userTypeGateway.save(any(UserType.class))).thenReturn(userType);

        useCase.execute(input);

        verify(userTypeGateway, never()).existsByRoleAndIdNot(any(), any());
    }

    @Test
    @DisplayName("Should call gateway save once")
    void shouldCallGatewaySaveOnce() {
        var input = new UpdateUserTypeUseCase.Input(id, "New Name", null, null);

        when(userTypeGateway.findById(id)).thenReturn(Optional.of(userType));
        when(userTypeGateway.save(any(UserType.class))).thenReturn(userType);

        useCase.execute(input);

        verify(userTypeGateway, times(1)).save(any(UserType.class));
    }

    @Test
    @DisplayName("Should map updated UserType to Output correctly")
    void shouldMapUpdatedUserTypeToOutputCorrectly() {
        var input = new UpdateUserTypeUseCase.Input(id, "New Name", "New Description", null);
        var updatedUserType = new UserType(
                id,
                UserTypeName.of("New Name"),
                UserTypeDescription.of("New Description"),
                UserRole.RESTAURANT_OWNER
        );

        when(userTypeGateway.findById(id)).thenReturn(Optional.of(userType));
        when(userTypeGateway.save(any(UserType.class))).thenReturn(updatedUserType);

        var output = useCase.execute(input);

        assertThat(output.id()).isEqualTo(id);
        assertThat(output.name().getValue()).isEqualTo("New Name");
        assertThat(output.description().getValue()).isEqualTo("New Description");
        assertThat(output.role()).isEqualTo(UserRole.RESTAURANT_OWNER);
    }

    // -------------------------------------------------------------------------
    // Failure
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should throw NotFoundException when UserType is not found")
    void shouldThrowNotFoundExceptionWhenUserTypeIsNotFound() {
        var input = new UpdateUserTypeUseCase.Input(id, "New Name", null, null);

        when(userTypeGateway.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(id.getValue().toString());
    }

    @Test
    @DisplayName("Should not call save when UserType is not found")
    void shouldNotCallSaveWhenUserTypeIsNotFound() {
        var input = new UpdateUserTypeUseCase.Input(id, "New Name", null, null);

        when(userTypeGateway.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(NotFoundException.class);

        verify(userTypeGateway, never()).save(any());
    }

    @Test
    @DisplayName("Should throw AlreadyExistsException when role already exists in another UserType")
    void shouldThrowAlreadyExistsExceptionWhenRoleAlreadyExistsInAnotherUserType() {
        var input = new UpdateUserTypeUseCase.Input(id, null, null, UserRole.CUSTOMER);

        when(userTypeGateway.findById(id)).thenReturn(Optional.of(userType));
        when(userTypeGateway.existsByRoleAndIdNot(UserRole.CUSTOMER, id)).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(AlreadyExistsException.class)
                .hasMessageContaining("CUSTOMER");
    }

    @Test
    @DisplayName("Should not call save when role already exists in another UserType")
    void shouldNotCallSaveWhenRoleAlreadyExistsInAnotherUserType() {
        var input = new UpdateUserTypeUseCase.Input(id, null, null, UserRole.CUSTOMER);

        when(userTypeGateway.findById(id)).thenReturn(Optional.of(userType));
        when(userTypeGateway.existsByRoleAndIdNot(UserRole.CUSTOMER, id)).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(AlreadyExistsException.class);

        verify(userTypeGateway, never()).save(any());
    }

    @Test
    @DisplayName("Should throw DomainException when name is blank")
    void shouldThrowDomainExceptionWhenNameIsBlank() {
        var input = new UpdateUserTypeUseCase.Input(id, "", null, null);

        when(userTypeGateway.findById(id)).thenReturn(Optional.of(userType));

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("UserType name is required.");
    }

    @Test
    @DisplayName("Should throw DomainException when description is blank")
    void shouldThrowDomainExceptionWhenDescriptionIsBlank() {
        var input = new UpdateUserTypeUseCase.Input(id, null, "", null);

        when(userTypeGateway.findById(id)).thenReturn(Optional.of(userType));

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("UserType description is required.");
    }
}