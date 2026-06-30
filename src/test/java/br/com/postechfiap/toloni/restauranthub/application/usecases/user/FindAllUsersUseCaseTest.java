package br.com.postechfiap.toloni.restauranthub.application.usecases.user;

import br.com.postechfiap.toloni.restauranthub.domain.shared.pagination.Page;
import br.com.postechfiap.toloni.restauranthub.domain.shared.pagination.PageFilter;
import br.com.postechfiap.toloni.restauranthub.domain.shared.pagination.PageRequest;
import br.com.postechfiap.toloni.restauranthub.domain.shared.pagination.PageSort;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class FindAllUsersUseCaseTest {

    @Mock
    private UserGateway userGateway;

    @InjectMocks
    private FindAllUsersUseCase useCase;

    private UserTypeId userTypeId;
    private User johnUser;
    private User janeUser;

    @BeforeEach
    void setUp() {
        userTypeId = UserTypeId.generate();
        johnUser = new User(
                UserId.generate(),
                UserName.of("John Doe"),
                UserEmail.of("john@example.com"),
                UserPassword.of("password123"),
                userTypeId
        );
        janeUser = new User(
                UserId.generate(),
                UserName.of("Jane Doe"),
                UserEmail.of("jane@example.com"),
                UserPassword.of("password123"),
                userTypeId
        );
    }

    // -------------------------------------------------------------------------
    // Success
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should return paginated list of Users")
    void shouldReturnPaginatedListOfUsers() {
        var pageRequest = PageRequest.of(0, 10);
        var enrichedList = List.of(
                new UserWithTypeName(johnUser, "Restaurant Owner"),
                new UserWithTypeName(janeUser, "Customer")
        );
        var page = Page.of(enrichedList, 0, 10, 2L);

        when(userGateway.findAllWithUserTypeName(pageRequest)).thenReturn(page);

        var output = useCase.execute(new FindAllUsersUseCase.Input(pageRequest));

        assertThat(output).isNotNull();
        assertThat(output.getContent()).hasSize(2);
        assertThat(output.getTotalElements()).isEqualTo(2L);
        assertThat(output.getPageNumber()).isZero();
        assertThat(output.getPageSize()).isEqualTo(10);
    }

    @Test
    @DisplayName("Should map User to Output correctly")
    void shouldMapUserToOutputCorrectly() {
        var pageRequest = PageRequest.of(0, 10);
        var enrichedList = List.of(new UserWithTypeName(johnUser, "Restaurant Owner"));
        var page = Page.of(enrichedList, 0, 10, 1L);

        when(userGateway.findAllWithUserTypeName(pageRequest)).thenReturn(page);

        var output = useCase.execute(new FindAllUsersUseCase.Input(pageRequest));

        var first = output.getContent().get(0);
        assertThat(first.id()).isEqualTo(johnUser.getId());
        assertThat(first.name()).isEqualTo(johnUser.getName().getValue());
        assertThat(first.email()).isEqualTo(johnUser.getEmail().getValue());
        assertThat(first.userTypeId()).isEqualTo(johnUser.getUserTypeId());
        assertThat(first.userTypeName()).isEqualTo("Restaurant Owner");
    }

    @Test
    @DisplayName("Should return userTypeName in output")
    void shouldReturnUserTypeNameInOutput() {
        var pageRequest = PageRequest.of(0, 10);
        var enrichedList = List.of(
                new UserWithTypeName(johnUser, "Restaurant Owner"),
                new UserWithTypeName(janeUser, "Customer")
        );
        var page = Page.of(enrichedList, 0, 10, 2L);

        when(userGateway.findAllWithUserTypeName(pageRequest)).thenReturn(page);

        var output = useCase.execute(new FindAllUsersUseCase.Input(pageRequest));

        assertThat(output.getContent().get(0).userTypeName()).isEqualTo("Restaurant Owner");
        assertThat(output.getContent().get(1).userTypeName()).isEqualTo("Customer");
    }

    @Test
    @DisplayName("Should return empty page when no Users exist")
    void shouldReturnEmptyPageWhenNoUsersExist() {
        var pageRequest = PageRequest.of(0, 10);
        var page = Page.<UserWithTypeName>of(List.of(), 0, 10, 0L);

        when(userGateway.findAllWithUserTypeName(pageRequest)).thenReturn(page);

        var output = useCase.execute(new FindAllUsersUseCase.Input(pageRequest));

        assertThat(output.getContent()).isEmpty();
        assertThat(output.getTotalElements()).isZero();
        assertThat(output.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("Should return correct page metadata")
    void shouldReturnCorrectPageMetadata() {
        var pageRequest = PageRequest.of(1, 5);
        var enrichedList = List.of(new UserWithTypeName(johnUser, "Restaurant Owner"));
        var page = Page.of(enrichedList, 1, 5, 6L);

        when(userGateway.findAllWithUserTypeName(pageRequest)).thenReturn(page);

        var output = useCase.execute(new FindAllUsersUseCase.Input(pageRequest));

        assertThat(output.getPageNumber()).isEqualTo(1);
        assertThat(output.getPageSize()).isEqualTo(5);
        assertThat(output.getTotalElements()).isEqualTo(6L);
        assertThat(output.getTotalPages()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should return first page correctly")
    void shouldReturnFirstPageCorrectly() {
        var pageRequest = PageRequest.of(0, 10);
        var enrichedList = List.of(
                new UserWithTypeName(johnUser, "Restaurant Owner"),
                new UserWithTypeName(janeUser, "Customer")
        );
        var page = Page.of(enrichedList, 0, 10, 2L);

        when(userGateway.findAllWithUserTypeName(pageRequest)).thenReturn(page);

        var output = useCase.execute(new FindAllUsersUseCase.Input(pageRequest));

        assertThat(output.isFirst()).isTrue();
        assertThat(output.isLast()).isTrue();
        assertThat(output.hasNext()).isFalse();
        assertThat(output.hasPrevious()).isFalse();
    }

    @Test
    @DisplayName("Should call gateway findAllWithUserTypeName with correct pageRequest")
    void shouldCallGatewayFindAllWithUserTypeNameWithCorrectPageRequest() {
        var pageRequest = PageRequest.of(0, 10);
        var page = Page.<UserWithTypeName>of(List.of(), 0, 10, 0L);

        when(userGateway.findAllWithUserTypeName(pageRequest)).thenReturn(page);

        useCase.execute(new FindAllUsersUseCase.Input(pageRequest));

        verify(userGateway, times(1)).findAllWithUserTypeName(pageRequest);
    }

    @Test
    @DisplayName("Should return paginated list with filters")
    void shouldReturnPaginatedListWithFilters() {
        var pageRequest = PageRequest.of(0, 10,
                List.of(PageFilter.of("name", "John")),
                List.of());
        var enrichedList = List.of(new UserWithTypeName(johnUser, "Restaurant Owner"));
        var page = Page.of(enrichedList, 0, 10, 1L);

        when(userGateway.findAllWithUserTypeName(pageRequest)).thenReturn(page);

        var output = useCase.execute(new FindAllUsersUseCase.Input(pageRequest));

        assertThat(output.getContent()).hasSize(1);
        assertThat(output.getContent().get(0).name()).isEqualTo("John Doe");
    }

    @Test
    @DisplayName("Should return paginated list with sorting")
    void shouldReturnPaginatedListWithSorting() {
        var pageRequest = PageRequest.of(0, 10,
                List.of(),
                List.of(PageSort.asc("name")));
        var enrichedList = List.of(
                new UserWithTypeName(janeUser, "Customer"),
                new UserWithTypeName(johnUser, "Restaurant Owner")
        );
        var page = Page.of(enrichedList, 0, 10, 2L);

        when(userGateway.findAllWithUserTypeName(pageRequest)).thenReturn(page);

        var output = useCase.execute(new FindAllUsersUseCase.Input(pageRequest));

        assertThat(output.getContent().get(0).name()).isEqualTo("Jane Doe");
        assertThat(output.getContent().get(1).name()).isEqualTo("John Doe");
    }
}