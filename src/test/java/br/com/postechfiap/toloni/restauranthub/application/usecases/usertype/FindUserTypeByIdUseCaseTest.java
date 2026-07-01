package br.com.postechfiap.toloni.restauranthub.application.usecases.usertype;

import br.com.postechfiap.toloni.restauranthub.application.gateways.UserTypeGateway;
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
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class FindUserTypeByIdUseCaseTest {

    @Mock
    private UserTypeGateway userTypeGateway;

    @InjectMocks
    private FindUserTypeByIdUseCase useCase;

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
    @DisplayName("Should find UserType by id successfully")
    void shouldFindUserTypeByIdSuccessfully() {
        when(userTypeGateway.findById(id)).thenReturn(Optional.of(userType));

        var output = useCase.execute(new FindUserTypeByIdUseCase.Input(id));

        assertThat(output).isNotNull();
        assertThat(output.id()).isEqualTo(id);
        assertThat(output.name()).isEqualTo(userType.getName());
        assertThat(output.description()).isEqualTo(userType.getDescription());
        assertThat(output.role()).isEqualTo(userType.getRole());
    }

    @Test
    @DisplayName("Should map UserType to Output correctly")
    void shouldMapUserTypeToOutputCorrectly() {
        when(userTypeGateway.findById(id)).thenReturn(Optional.of(userType));

        var output = useCase.execute(new FindUserTypeByIdUseCase.Input(id));

        assertThat(output.id()).isEqualTo(userType.getId());
        assertThat(output.name().getValue()).isEqualTo("Restaurant Owner");
        assertThat(output.description().getValue()).isEqualTo("Owns and manages a restaurant");
        assertThat(output.role()).isEqualTo(UserRole.RESTAURANT_OWNER);
    }

    @Test
    @DisplayName("Should call gateway findById with correct id")
    void shouldCallGatewayFindByIdWithCorrectId() {
        when(userTypeGateway.findById(id)).thenReturn(Optional.of(userType));

        useCase.execute(new FindUserTypeByIdUseCase.Input(id));

        verify(userTypeGateway, times(1)).findById(id);
    }

    @Test
    @DisplayName("Should find UserType with CUSTOMER role")
    void shouldFindUserTypeWithCustomerRole() {
        var customerId = UserTypeId.generate();
        var customerType = new UserType(
                customerId,
                UserTypeName.of("Customer"),
                UserTypeDescription.of("Browses and orders food"),
                UserRole.CUSTOMER
        );

        when(userTypeGateway.findById(customerId)).thenReturn(Optional.of(customerType));

        var output = useCase.execute(new FindUserTypeByIdUseCase.Input(customerId));

        assertThat(output.role()).isEqualTo(UserRole.CUSTOMER);
    }

    // -------------------------------------------------------------------------
    // Failure
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should throw NotFoundException when UserType is not found")
    void shouldThrowNotFoundExceptionWhenUserTypeIsNotFound() {
        when(userTypeGateway.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(new FindUserTypeByIdUseCase.Input(id)))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(id.getValue().toString());
    }

    @Test
    @DisplayName("Should not return output when UserType is not found")
    void shouldNotReturnOutputWhenUserTypeIsNotFound() {
        when(userTypeGateway.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(new FindUserTypeByIdUseCase.Input(id)))
                .isInstanceOf(NotFoundException.class);
    }
}