package br.com.postechfiap.toloni.restauranthub.application.usecases.usertype;

import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.EntityInUseException;
import br.com.postechfiap.toloni.restauranthub.domain.user.UserGateway;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.UserTypeGateway;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.valueobject.UserTypeId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatNoException;
import static org.mockito.Mockito.*;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class DeleteUserTypeUseCaseTest {

    @Mock
    private UserTypeGateway userTypeGateway;

    @Mock
    private UserGateway userGateway;

    @InjectMocks
    private DeleteUserTypeUseCase useCase;

    private DeleteUserTypeUseCase.Input input;

    @BeforeEach
    void setUp() {
        input = new DeleteUserTypeUseCase.Input(UserTypeId.generate());
    }

    // -------------------------------------------------------------------------
    // Success
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should delete UserType successfully when no User is associated")
    void shouldDeleteUserTypeSuccessfullyWhenNoUserIsAssociated() {
        when(userGateway.existsByUserTypeId(input.id())).thenReturn(false);

        useCase.execute(input);

        verify(userTypeGateway, times(1)).deleteById(input.id());
    }

    @Test
    @DisplayName("Should call existsByUserTypeId with correct id")
    void shouldCallExistsByUserTypeIdWithCorrectId() {
        when(userGateway.existsByUserTypeId(input.id())).thenReturn(false);

        useCase.execute(input);

        verify(userGateway, times(1)).existsByUserTypeId(input.id());
    }

    @Test
    @DisplayName("Should be idempotent when UserType does not exist")
    void shouldBeIdempotentWhenUserTypeDoesNotExist() {
        when(userGateway.existsByUserTypeId(input.id())).thenReturn(false);

        assertThatNoException().isThrownBy(() -> useCase.execute(input));

        verify(userTypeGateway, times(1)).deleteById(input.id());
    }

    // -------------------------------------------------------------------------
    // Failure
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should throw EntityInUseException when UserType is associated with a User")
    void shouldThrowEntityInUseExceptionWhenUserTypeIsAssociatedWithUser() {
        when(userGateway.existsByUserTypeId(input.id())).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(EntityInUseException.class)
                .hasMessageContaining(input.id().getValue().toString());
    }

    @Test
    @DisplayName("Should not call deleteById when UserType is associated with a User")
    void shouldNotCallDeleteByIdWhenUserTypeIsAssociatedWithUser() {
        when(userGateway.existsByUserTypeId(input.id())).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(EntityInUseException.class);

        verify(userTypeGateway, never()).deleteById(any());
    }
}