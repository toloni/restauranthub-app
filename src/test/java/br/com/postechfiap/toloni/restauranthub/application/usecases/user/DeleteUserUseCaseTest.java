package br.com.postechfiap.toloni.restauranthub.application.usecases.user;

import br.com.postechfiap.toloni.restauranthub.domain.restaurant.RestaurantGateway;
import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.EntityInUseException;
import br.com.postechfiap.toloni.restauranthub.domain.user.UserGateway;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class DeleteUserUseCaseTest {

    @Mock
    private UserGateway userGateway;

    @Mock
    private RestaurantGateway restaurantGateway;

    @InjectMocks
    private DeleteUserUseCase useCase;

    private DeleteUserUseCase.Input input;

    @BeforeEach
    void setUp() {
        input = new DeleteUserUseCase.Input(UserId.generate());
    }

    // -------------------------------------------------------------------------
    // Success
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should delete User successfully when no Restaurant is associated")
    void shouldDeleteUserSuccessfullyWhenNoRestaurantIsAssociated() {
        when(restaurantGateway.existsByOwnerId(input.id())).thenReturn(false);

        useCase.execute(input);

        verify(userGateway, times(1)).deleteById(input.id());
    }

    @Test
    @DisplayName("Should call existsByOwnerId with correct id")
    void shouldCallExistsByOwnerIdWithCorrectId() {
        when(restaurantGateway.existsByOwnerId(input.id())).thenReturn(false);

        useCase.execute(input);

        verify(restaurantGateway, times(1)).existsByOwnerId(input.id());
    }

    @Test
    @DisplayName("Should be idempotent when User does not exist")
    void shouldBeIdempotentWhenUserDoesNotExist() {
        when(restaurantGateway.existsByOwnerId(input.id())).thenReturn(false);

        assertThatNoException().isThrownBy(() -> useCase.execute(input));

        verify(userGateway, times(1)).deleteById(input.id());
    }

    // -------------------------------------------------------------------------
    // Failure
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should throw EntityInUseException when User is associated with a Restaurant")
    void shouldThrowEntityInUseExceptionWhenUserIsAssociatedWithRestaurant() {
        when(restaurantGateway.existsByOwnerId(input.id())).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(EntityInUseException.class)
                .hasMessageContaining(input.id().getValue().toString());
    }

    @Test
    @DisplayName("Should not call deleteById when User is associated with a Restaurant")
    void shouldNotCallDeleteByIdWhenUserIsAssociatedWithRestaurant() {
        when(restaurantGateway.existsByOwnerId(input.id())).thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(EntityInUseException.class);

        verify(userGateway, never()).deleteById(any());
    }
}