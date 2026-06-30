package br.com.postechfiap.toloni.restauranthub.application.usecases.user;

import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.NotFoundException;
import br.com.postechfiap.toloni.restauranthub.domain.user.User;
import br.com.postechfiap.toloni.restauranthub.domain.user.UserGateway;
import br.com.postechfiap.toloni.restauranthub.domain.user.UserWithTypeName;
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
import static org.mockito.Mockito.*;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class FindUserByIdUseCaseTest {

    @Mock
    private UserGateway userGateway;

    @InjectMocks
    private FindUserByIdUseCase useCase;

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
    @DisplayName("Should find User by id successfully")
    void shouldFindUserByIdSuccessfully() {
        var enriched = new UserWithTypeName(user, "Restaurant Owner");

        when(userGateway.findByIdWithUserTypeName(id)).thenReturn(Optional.of(enriched));

        var output = useCase.execute(new FindUserByIdUseCase.Input(id));

        assertThat(output).isNotNull();
        assertThat(output.id()).isEqualTo(id);
        assertThat(output.name()).isEqualTo(user.getName().getValue());
        assertThat(output.email()).isEqualTo(user.getEmail().getValue());
        assertThat(output.userTypeId()).isEqualTo(userTypeId);
        assertThat(output.userTypeName()).isEqualTo("Restaurant Owner");
    }

    @Test
    @DisplayName("Should map User to Output correctly")
    void shouldMapUserToOutputCorrectly() {
        var enriched = new UserWithTypeName(user, "Restaurant Owner");

        when(userGateway.findByIdWithUserTypeName(id)).thenReturn(Optional.of(enriched));

        var output = useCase.execute(new FindUserByIdUseCase.Input(id));

        assertThat(output.id()).isEqualTo(user.getId());
        assertThat(output.name()).isEqualTo("John Doe");
        assertThat(output.email()).isEqualTo("john@example.com");
        assertThat(output.userTypeId()).isEqualTo(userTypeId);
        assertThat(output.userTypeName()).isEqualTo("Restaurant Owner");
    }

    @Test
    @DisplayName("Should return userTypeName in output")
    void shouldReturnUserTypeNameInOutput() {
        var enriched = new UserWithTypeName(user, "Customer");

        when(userGateway.findByIdWithUserTypeName(id)).thenReturn(Optional.of(enriched));

        var output = useCase.execute(new FindUserByIdUseCase.Input(id));

        assertThat(output.userTypeName()).isEqualTo("Customer");
    }

    @Test
    @DisplayName("Should return null userTypeName when UserType is not found")
    void shouldReturnNullUserTypeNameWhenUserTypeIsNotFound() {
        var enriched = new UserWithTypeName(user, null);

        when(userGateway.findByIdWithUserTypeName(id)).thenReturn(Optional.of(enriched));

        var output = useCase.execute(new FindUserByIdUseCase.Input(id));

        assertThat(output.userTypeName()).isNull();
    }

    @Test
    @DisplayName("Should call gateway findByIdWithUserTypeName with correct id")
    void shouldCallGatewayFindByIdWithUserTypeNameWithCorrectId() {
        var enriched = new UserWithTypeName(user, "Restaurant Owner");

        when(userGateway.findByIdWithUserTypeName(id)).thenReturn(Optional.of(enriched));

        useCase.execute(new FindUserByIdUseCase.Input(id));

        verify(userGateway, times(1)).findByIdWithUserTypeName(id);
    }

    // -------------------------------------------------------------------------
    // Failure
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should throw NotFoundException when User is not found")
    void shouldThrowNotFoundExceptionWhenUserIsNotFound() {
        when(userGateway.findByIdWithUserTypeName(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(new FindUserByIdUseCase.Input(id)))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(id.getValue().toString());
    }

    @Test
    @DisplayName("Should not return output when User is not found")
    void shouldNotReturnOutputWhenUserIsNotFound() {
        when(userGateway.findByIdWithUserTypeName(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(new FindUserByIdUseCase.Input(id)))
                .isInstanceOf(NotFoundException.class);
    }
}