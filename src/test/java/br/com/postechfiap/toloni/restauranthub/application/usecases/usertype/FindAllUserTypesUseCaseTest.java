package br.com.postechfiap.toloni.restauranthub.application.usecases.usertype;

import br.com.postechfiap.toloni.restauranthub.domain.shared.pagination.Page;
import br.com.postechfiap.toloni.restauranthub.domain.shared.pagination.PageFilter;
import br.com.postechfiap.toloni.restauranthub.domain.shared.pagination.PageRequest;
import br.com.postechfiap.toloni.restauranthub.domain.shared.pagination.PageSort;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.UserRole;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.UserType;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.UserTypeGateway;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class FindAllUserTypesUseCaseTest {

    @Mock
    private UserTypeGateway userTypeGateway;

    @InjectMocks
    private FindAllUserTypesUseCase useCase;

    private UserType restaurantOwnerType;
    private UserType customerType;

    @BeforeEach
    void setUp() {
        restaurantOwnerType = new UserType(
                UserTypeId.generate(),
                UserTypeName.of("Restaurant Owner"),
                UserTypeDescription.of("Owns and manages a restaurant"),
                UserRole.RESTAURANT_OWNER
        );
        customerType = new UserType(
                UserTypeId.generate(),
                UserTypeName.of("Customer"),
                UserTypeDescription.of("Browses and orders food"),
                UserRole.CUSTOMER
        );
    }

    // -------------------------------------------------------------------------
    // Success
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should return paginated list of UserTypes")
    void shouldReturnPaginatedListOfUserTypes() {
        var pageRequest = PageRequest.of(0, 10);
        var page = Page.of(List.of(restaurantOwnerType, customerType), 0, 10, 2L);

        when(userTypeGateway.findAll(pageRequest)).thenReturn(page);

        var output = useCase.execute(new FindAllUserTypesUseCase.Input(pageRequest));

        assertThat(output).isNotNull();
        assertThat(output.getContent()).hasSize(2);
        assertThat(output.getTotalElements()).isEqualTo(2L);
        assertThat(output.getPageNumber()).isZero();
        assertThat(output.getPageSize()).isEqualTo(10);
    }

    @Test
    @DisplayName("Should map UserType to Output correctly")
    void shouldMapUserTypeToOutputCorrectly() {
        var pageRequest = PageRequest.of(0, 10);
        var page = Page.of(List.of(restaurantOwnerType), 0, 10, 1L);

        when(userTypeGateway.findAll(pageRequest)).thenReturn(page);

        var output = useCase.execute(new FindAllUserTypesUseCase.Input(pageRequest));

        var first = output.getContent().get(0);
        assertThat(first.id()).isEqualTo(restaurantOwnerType.getId());
        assertThat(first.name()).isEqualTo(restaurantOwnerType.getName().getValue());
        assertThat(first.description()).isEqualTo(restaurantOwnerType.getDescription().getValue());
        assertThat(first.role()).isEqualTo(restaurantOwnerType.getRole());
    }

    @Test
    @DisplayName("Should return empty page when no UserTypes exist")
    void shouldReturnEmptyPageWhenNoUserTypesExist() {
        var pageRequest = PageRequest.of(0, 10);
        var page = Page.<UserType>of(List.of(), 0, 10, 0L);

        when(userTypeGateway.findAll(pageRequest)).thenReturn(page);

        var output = useCase.execute(new FindAllUserTypesUseCase.Input(pageRequest));

        assertThat(output.getContent()).isEmpty();
        assertThat(output.getTotalElements()).isZero();
        assertThat(output.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("Should return correct page metadata")
    void shouldReturnCorrectPageMetadata() {
        var pageRequest = PageRequest.of(1, 5);
        var page = Page.of(List.of(customerType), 1, 5, 6L);

        when(userTypeGateway.findAll(pageRequest)).thenReturn(page);

        var output = useCase.execute(new FindAllUserTypesUseCase.Input(pageRequest));

        assertThat(output.getPageNumber()).isEqualTo(1);
        assertThat(output.getPageSize()).isEqualTo(5);
        assertThat(output.getTotalElements()).isEqualTo(6L);
        assertThat(output.getTotalPages()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should return first page correctly")
    void shouldReturnFirstPageCorrectly() {
        var pageRequest = PageRequest.of(0, 10);
        var page = Page.of(List.of(restaurantOwnerType, customerType), 0, 10, 2L);

        when(userTypeGateway.findAll(pageRequest)).thenReturn(page);

        var output = useCase.execute(new FindAllUserTypesUseCase.Input(pageRequest));

        assertThat(output.isFirst()).isTrue();
        assertThat(output.isLast()).isTrue();
        assertThat(output.hasNext()).isFalse();
        assertThat(output.hasPrevious()).isFalse();
    }

    @Test
    @DisplayName("Should call gateway findAll with correct pageRequest")
    void shouldCallGatewayFindAllWithCorrectPageRequest() {
        var pageRequest = PageRequest.of(0, 10);
        var page = Page.<UserType>of(List.of(), 0, 10, 0L);

        when(userTypeGateway.findAll(pageRequest)).thenReturn(page);

        useCase.execute(new FindAllUserTypesUseCase.Input(pageRequest));

        verify(userTypeGateway, times(1)).findAll(pageRequest);
    }

    @Test
    @DisplayName("Should return paginated list with filters")
    void shouldReturnPaginatedListWithFilters() {
        var pageRequest = PageRequest.of(0, 10,
                List.of(PageFilter.of("name", "Restaurant")),
                List.of());
        var page = Page.of(List.of(restaurantOwnerType), 0, 10, 1L);

        when(userTypeGateway.findAll(pageRequest)).thenReturn(page);

        var output = useCase.execute(new FindAllUserTypesUseCase.Input(pageRequest));

        assertThat(output.getContent()).hasSize(1);
        assertThat(output.getContent().get(0).name()).isEqualTo("Restaurant Owner");
    }

    @Test
    @DisplayName("Should return paginated list with sorting")
    void shouldReturnPaginatedListWithSorting() {
        var pageRequest = PageRequest.of(0, 10,
                List.of(),
                List.of(PageSort.asc("name")));
        var page = Page.of(List.of(customerType, restaurantOwnerType), 0, 10, 2L);

        when(userTypeGateway.findAll(pageRequest)).thenReturn(page);

        var output = useCase.execute(new FindAllUserTypesUseCase.Input(pageRequest));

        assertThat(output.getContent()).hasSize(2);
        assertThat(output.getContent().get(0).name()).isEqualTo("Customer");
        assertThat(output.getContent().get(1).name()).isEqualTo("Restaurant Owner");
    }
}