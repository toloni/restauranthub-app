package br.com.postechfiap.toloni.restauranthub.adapters.gateways;

import br.com.postechfiap.toloni.restauranthub.domain.restaurant.Restaurant;
import br.com.postechfiap.toloni.restauranthub.domain.restaurant.valueobject.*;
import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.EntityInUseException;
import br.com.postechfiap.toloni.restauranthub.domain.shared.pagination.PageFilter;
import br.com.postechfiap.toloni.restauranthub.domain.shared.pagination.PageRequest;
import br.com.postechfiap.toloni.restauranthub.domain.user.User;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserEmail;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserId;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserName;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserPassword;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.valueobject.UserTypeId;
import br.com.postechfiap.toloni.restauranthub.infrastructure.persistence.entities.RestaurantJpaEntity;
import br.com.postechfiap.toloni.restauranthub.infrastructure.persistence.entities.UserJpaEntity;
import br.com.postechfiap.toloni.restauranthub.infrastructure.persistence.repositories.RestaurantJpaRepository;
import br.com.postechfiap.toloni.restauranthub.infrastructure.persistence.repositories.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class RestaurantGatewayImplTest {

    @Mock
    private RestaurantJpaRepository restaurantJpaRepository;

    @Mock
    private UserJpaRepository userJpaRepository;

    @InjectMocks
    private RestaurantGatewayImpl gateway;

    private RestaurantId id;
    private UserId ownerId;
    private Restaurant restaurant;
    private RestaurantJpaEntity restaurantEntity;
    private UserJpaEntity ownerEntity;

    @BeforeEach
    void setUp() {
        id = RestaurantId.generate();
        ownerId = UserId.generate();

        var userTypeId = UserTypeId.generate();

        var owner = new User(
                ownerId,
                UserName.of("John Doe"),
                UserEmail.of("john@example.com"),
                UserPassword.of("password123"),
                userTypeId
        );

        ownerEntity = UserJpaEntity.fromDomain(owner);

        restaurant = new Restaurant(
                id,
                RestaurantName.of("The Great Burger"),
                RestaurantAddress.of("123 Main St, Springfield"),
                RestaurantCuisineType.of("American"),
                RestaurantOpeningHours.of("Mon-Fri 9am-10pm"),
                ownerId
        );

        restaurantEntity = RestaurantJpaEntity.fromDomain(restaurant, ownerEntity);
    }

    // -------------------------------------------------------------------------
    // save
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should save Restaurant successfully")
    void shouldSaveRestaurantSuccessfully() {
        when(userJpaRepository.getReferenceById(ownerId.getValue())).thenReturn(ownerEntity);
        when(restaurantJpaRepository.save(any(RestaurantJpaEntity.class))).thenReturn(restaurantEntity);

        var result = gateway.save(restaurant);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(restaurant.getId());
        assertThat(result.getName()).isEqualTo(restaurant.getName());
        assertThat(result.getAddress()).isEqualTo(restaurant.getAddress());
        assertThat(result.getCuisineType()).isEqualTo(restaurant.getCuisineType());
        assertThat(result.getOpeningHours()).isEqualTo(restaurant.getOpeningHours());
        assertThat(result.getOwnerId()).isEqualTo(restaurant.getOwnerId());
    }

    @Test
    @DisplayName("Should call jpaRepository save once")
    void shouldCallJpaRepositorySaveOnce() {
        when(userJpaRepository.getReferenceById(ownerId.getValue())).thenReturn(ownerEntity);
        when(restaurantJpaRepository.save(any(RestaurantJpaEntity.class))).thenReturn(restaurantEntity);

        gateway.save(restaurant);

        verify(restaurantJpaRepository, times(1)).save(any(RestaurantJpaEntity.class));
    }

    @Test
    @DisplayName("Should call userJpaRepository getReferenceById with correct UUID")
    void shouldCallUserJpaRepositoryGetReferenceByIdWithCorrectUUID() {
        when(userJpaRepository.getReferenceById(ownerId.getValue())).thenReturn(ownerEntity);
        when(restaurantJpaRepository.save(any(RestaurantJpaEntity.class))).thenReturn(restaurantEntity);

        gateway.save(restaurant);

        verify(userJpaRepository, times(1)).getReferenceById(ownerId.getValue());
    }

    // -------------------------------------------------------------------------
    // findById
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should find Restaurant by id successfully")
    void shouldFindRestaurantByIdSuccessfully() {
        when(restaurantJpaRepository.findById(id.getValue())).thenReturn(Optional.of(restaurantEntity));

        var result = gateway.findById(id);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(id);
        assertThat(result.get().getName().getValue()).isEqualTo("The Great Burger");
    }

    @Test
    @DisplayName("Should return empty when Restaurant is not found by id")
    void shouldReturnEmptyWhenRestaurantIsNotFoundById() {
        when(restaurantJpaRepository.findById(id.getValue())).thenReturn(Optional.empty());

        var result = gateway.findById(id);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should call jpaRepository findById with correct UUID")
    void shouldCallJpaRepositoryFindByIdWithCorrectUUID() {
        when(restaurantJpaRepository.findById(id.getValue())).thenReturn(Optional.of(restaurantEntity));

        gateway.findById(id);

        verify(restaurantJpaRepository, times(1)).findById(id.getValue());
    }

    // -------------------------------------------------------------------------
    // findAll
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should return paginated list of Restaurants")
    void shouldReturnPaginatedListOfRestaurants() {
        var pageRequest = PageRequest.of(0, 10);
        var springPage = new org.springframework.data.domain.PageImpl<>(
                List.of(restaurantEntity),
                org.springframework.data.domain.PageRequest.of(0, 10),
                1L
        );

        when(restaurantJpaRepository.findAll(any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(springPage);

        var result = gateway.findAll(pageRequest);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should return empty page when no Restaurants exist")
    void shouldReturnEmptyPageWhenNoRestaurantsExist() {
        var pageRequest = PageRequest.of(0, 10);
        var springPage = new org.springframework.data.domain.PageImpl<RestaurantJpaEntity>(
                List.of(),
                org.springframework.data.domain.PageRequest.of(0, 10),
                0L
        );

        when(restaurantJpaRepository.findAll(any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(springPage);

        var result = gateway.findAll(pageRequest);

        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }

    // -------------------------------------------------------------------------
    // deleteById
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should delete Restaurant by id successfully")
    void shouldDeleteRestaurantByIdSuccessfully() {
        doNothing().when(restaurantJpaRepository).deleteById(id.getValue());

        assertThatNoException().isThrownBy(() -> gateway.deleteById(id));

        verify(restaurantJpaRepository, times(1)).deleteById(id.getValue());
    }

    @Test
    @DisplayName("Should throw EntityInUseException when Restaurant is in use")
    void shouldThrowEntityInUseExceptionWhenRestaurantIsInUse() {
        doThrow(DataIntegrityViolationException.class)
                .when(restaurantJpaRepository).deleteById(id.getValue());

        assertThatThrownBy(() -> gateway.deleteById(id))
                .isInstanceOf(EntityInUseException.class)
                .hasMessageContaining(id.getValue().toString());
    }

    // -------------------------------------------------------------------------
    // existsByName
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should return true when Restaurant with name exists")
    void shouldReturnTrueWhenRestaurantWithNameExists() {
        when(restaurantJpaRepository.existsByName("The Great Burger")).thenReturn(true);

        assertThat(gateway.existsByName(RestaurantName.of("The Great Burger"))).isTrue();
    }

    @Test
    @DisplayName("Should return false when Restaurant with name does not exist")
    void shouldReturnFalseWhenRestaurantWithNameDoesNotExist() {
        when(restaurantJpaRepository.existsByName("The Great Burger")).thenReturn(false);

        assertThat(gateway.existsByName(RestaurantName.of("The Great Burger"))).isFalse();
    }

    @Test
    @DisplayName("Should call jpaRepository existsByName with correct name")
    void shouldCallJpaRepositoryExistsByNameWithCorrectName() {
        when(restaurantJpaRepository.existsByName("The Great Burger")).thenReturn(true);

        gateway.existsByName(RestaurantName.of("The Great Burger"));

        verify(restaurantJpaRepository, times(1)).existsByName("The Great Burger");
    }

    // -------------------------------------------------------------------------
    // existsByNameAndIdNot
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should return true when another Restaurant with name exists")
    void shouldReturnTrueWhenAnotherRestaurantWithNameExists() {
        when(restaurantJpaRepository.existsByNameAndIdNot("The Great Burger", id.getValue())).thenReturn(true);

        assertThat(gateway.existsByNameAndIdNot(RestaurantName.of("The Great Burger"), id)).isTrue();
    }

    @Test
    @DisplayName("Should return false when no other Restaurant with name exists")
    void shouldReturnFalseWhenNoOtherRestaurantWithNameExists() {
        when(restaurantJpaRepository.existsByNameAndIdNot("The Great Burger", id.getValue())).thenReturn(false);

        assertThat(gateway.existsByNameAndIdNot(RestaurantName.of("The Great Burger"), id)).isFalse();
    }

    @Test
    @DisplayName("Should call jpaRepository existsByNameAndIdNot with correct name and UUID")
    void shouldCallJpaRepositoryExistsByNameAndIdNotWithCorrectNameAndUUID() {
        when(restaurantJpaRepository.existsByNameAndIdNot("The Great Burger", id.getValue())).thenReturn(false);

        gateway.existsByNameAndIdNot(RestaurantName.of("The Great Burger"), id);

        verify(restaurantJpaRepository, times(1)).existsByNameAndIdNot("The Great Burger", id.getValue());
    }

    // -------------------------------------------------------------------------
    // existsByOwnerId
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should return true when Restaurant with ownerId exists")
    void shouldReturnTrueWhenRestaurantWithOwnerIdExists() {
        when(restaurantJpaRepository.existsByOwnerId(ownerId.getValue())).thenReturn(true);

        assertThat(gateway.existsByOwnerId(ownerId)).isTrue();
    }

    @Test
    @DisplayName("Should return false when no Restaurant with ownerId exists")
    void shouldReturnFalseWhenNoRestaurantWithOwnerIdExists() {
        when(restaurantJpaRepository.existsByOwnerId(ownerId.getValue())).thenReturn(false);

        assertThat(gateway.existsByOwnerId(ownerId)).isFalse();
    }

    @Test
    @DisplayName("Should call jpaRepository existsByOwnerId with correct UUID")
    void shouldCallJpaRepositoryExistsByOwnerIdWithCorrectUUID() {
        when(restaurantJpaRepository.existsByOwnerId(ownerId.getValue())).thenReturn(true);

        gateway.existsByOwnerId(ownerId);

        verify(restaurantJpaRepository, times(1)).existsByOwnerId(ownerId.getValue());
    }

    // -------------------------------------------------------------------------
    // findByIdWithOwnerName
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should find Restaurant by id with owner name successfully")
    void shouldFindRestaurantByIdWithOwnerNameSuccessfully() {
        when(restaurantJpaRepository.findById(id.getValue())).thenReturn(Optional.of(restaurantEntity));

        var result = gateway.findByIdWithOwnerName(id);

        assertThat(result).isPresent();
        assertThat(result.get().restaurant().getId()).isEqualTo(id);
        assertThat(result.get().ownerName()).isEqualTo("John Doe");
    }

    @Test
    @DisplayName("Should return empty when Restaurant is not found by id with owner name")
    void shouldReturnEmptyWhenRestaurantIsNotFoundByIdWithOwnerName() {
        when(restaurantJpaRepository.findById(id.getValue())).thenReturn(Optional.empty());

        var result = gateway.findByIdWithOwnerName(id);

        assertThat(result).isEmpty();
    }

    // -------------------------------------------------------------------------
    // findAllWithOwnerName
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should return paginated list of Restaurants with owner name without filters")
    void shouldReturnPaginatedListOfRestaurantsWithOwnerNameWithoutFilters() {
        var pageRequest = PageRequest.of(0, 10);
        var springPage = new org.springframework.data.domain.PageImpl<>(
                List.of(restaurantEntity),
                org.springframework.data.domain.PageRequest.of(0, 10),
                1L
        );

        when(restaurantJpaRepository.findAll(any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(springPage);

        var result = gateway.findAllWithOwnerName(pageRequest);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).restaurant().getId()).isEqualTo(id);
        assertThat(result.getContent().get(0).ownerName()).isEqualTo("John Doe");
        assertThat(result.getTotalElements()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should return paginated list of Restaurants with owner name with filters")
    void shouldReturnPaginatedListOfRestaurantsWithOwnerNameWithFilters() {
        var pageRequest = PageRequest.of(0, 10,
                List.of(PageFilter.of("name", "Burger")),
                List.of());
        var springPage = new org.springframework.data.domain.PageImpl<>(
                List.of(restaurantEntity),
                org.springframework.data.domain.PageRequest.of(0, 10),
                1L
        );

        when(restaurantJpaRepository.findAll(
                any(org.springframework.data.jpa.domain.Specification.class),
                any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(springPage);

        var result = gateway.findAllWithOwnerName(pageRequest);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).restaurant().getName().getValue()).isEqualTo("The Great Burger");
    }

    @Test
    @DisplayName("Should return empty page when no Restaurants exist")
    void shouldReturnEmptyPageWhenNoRestaurantsExistWithOwnerName() {
        var pageRequest = PageRequest.of(0, 10);
        var springPage = new org.springframework.data.domain.PageImpl<RestaurantJpaEntity>(
                List.of(),
                org.springframework.data.domain.PageRequest.of(0, 10),
                0L
        );

        when(restaurantJpaRepository.findAll(any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(springPage);

        var result = gateway.findAllWithOwnerName(pageRequest);

        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }
}