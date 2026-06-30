package br.com.postechfiap.toloni.restauranthub.adapters.gateways;

import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.EntityInUseException;
import br.com.postechfiap.toloni.restauranthub.domain.shared.pagination.PageFilter;
import br.com.postechfiap.toloni.restauranthub.domain.shared.pagination.PageRequest;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.UserRole;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.UserType;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.valueobject.UserTypeDescription;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.valueobject.UserTypeId;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.valueobject.UserTypeName;
import br.com.postechfiap.toloni.restauranthub.infrastructure.persistence.entities.UserTypeJpaEntity;
import br.com.postechfiap.toloni.restauranthub.infrastructure.persistence.repositories.UserTypeJpaRepository;
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
class UserTypeGatewayImplTest {

    @Mock
    private UserTypeJpaRepository jpaRepository;

    @InjectMocks
    private UserTypeGatewayImpl gateway;

    private UserTypeId id;
    private UserType userType;
    private UserTypeJpaEntity userTypeEntity;

    @BeforeEach
    void setUp() {
        id = UserTypeId.generate();
        userType = new UserType(
                id,
                UserTypeName.of("Restaurant Owner"),
                UserTypeDescription.of("Owns and manages a restaurant"),
                UserRole.RESTAURANT_OWNER
        );
        userTypeEntity = UserTypeJpaEntity.fromDomain(userType);
    }

    // -------------------------------------------------------------------------
    // save
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should save UserType successfully")
    void shouldSaveUserTypeSuccessfully() {
        when(jpaRepository.save(any(UserTypeJpaEntity.class))).thenReturn(userTypeEntity);

        var result = gateway.save(userType);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(userType.getId());
        assertThat(result.getName()).isEqualTo(userType.getName());
        assertThat(result.getDescription()).isEqualTo(userType.getDescription());
        assertThat(result.getRole()).isEqualTo(userType.getRole());
    }

    @Test
    @DisplayName("Should call jpaRepository save once")
    void shouldCallJpaRepositorySaveOnce() {
        when(jpaRepository.save(any(UserTypeJpaEntity.class))).thenReturn(userTypeEntity);

        gateway.save(userType);

        verify(jpaRepository, times(1)).save(any(UserTypeJpaEntity.class));
    }

    // -------------------------------------------------------------------------
    // findById
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should find UserType by id successfully")
    void shouldFindUserTypeByIdSuccessfully() {
        when(jpaRepository.findById(id.getValue())).thenReturn(Optional.of(userTypeEntity));

        var result = gateway.findById(id);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(id);
        assertThat(result.get().getRole()).isEqualTo(UserRole.RESTAURANT_OWNER);
    }

    @Test
    @DisplayName("Should return empty when UserType is not found by id")
    void shouldReturnEmptyWhenUserTypeIsNotFoundById() {
        when(jpaRepository.findById(id.getValue())).thenReturn(Optional.empty());

        var result = gateway.findById(id);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should call jpaRepository findById with correct UUID")
    void shouldCallJpaRepositoryFindByIdWithCorrectUUID() {
        when(jpaRepository.findById(id.getValue())).thenReturn(Optional.of(userTypeEntity));

        gateway.findById(id);

        verify(jpaRepository, times(1)).findById(id.getValue());
    }

    // -------------------------------------------------------------------------
    // findAll
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should return paginated list of UserTypes without filters")
    void shouldReturnPaginatedListOfUserTypesWithoutFilters() {
        var pageRequest = PageRequest.of(0, 10);
        var springPage = new org.springframework.data.domain.PageImpl<>(
                List.of(userTypeEntity),
                org.springframework.data.domain.PageRequest.of(0, 10),
                1L
        );

        when(jpaRepository.findAll(any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(springPage);

        var result = gateway.findAll(pageRequest);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1L);
        assertThat(result.getPageNumber()).isZero();
        assertThat(result.getPageSize()).isEqualTo(10);
    }

    @Test
    @DisplayName("Should return paginated list of UserTypes with filters")
    void shouldReturnPaginatedListOfUserTypesWithFilters() {
        var pageRequest = PageRequest.of(0, 10,
                List.of(PageFilter.of("name", "Restaurant")),
                List.of());
        var springPage = new org.springframework.data.domain.PageImpl<>(
                List.of(userTypeEntity),
                org.springframework.data.domain.PageRequest.of(0, 10),
                1L
        );

        when(jpaRepository.findAll(
                any(org.springframework.data.jpa.domain.Specification.class),
                any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(springPage);

        var result = gateway.findAll(pageRequest);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName().getValue()).isEqualTo("Restaurant Owner");
    }

    @Test
    @DisplayName("Should return empty page when no UserTypes exist")
    void shouldReturnEmptyPageWhenNoUserTypesExist() {
        var pageRequest = PageRequest.of(0, 10);
        var springPage = new org.springframework.data.domain.PageImpl<UserTypeJpaEntity>(
                List.of(),
                org.springframework.data.domain.PageRequest.of(0, 10),
                0L
        );

        when(jpaRepository.findAll(any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(springPage);

        var result = gateway.findAll(pageRequest);

        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }

    // -------------------------------------------------------------------------
    // deleteById
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should delete UserType by id successfully")
    void shouldDeleteUserTypeByIdSuccessfully() {
        doNothing().when(jpaRepository).deleteById(id.getValue());

        assertThatNoException().isThrownBy(() -> gateway.deleteById(id));

        verify(jpaRepository, times(1)).deleteById(id.getValue());
    }

    @Test
    @DisplayName("Should throw EntityInUseException when UserType is in use")
    void shouldThrowEntityInUseExceptionWhenUserTypeIsInUse() {
        doThrow(DataIntegrityViolationException.class)
                .when(jpaRepository).deleteById(id.getValue());

        assertThatThrownBy(() -> gateway.deleteById(id))
                .isInstanceOf(EntityInUseException.class)
                .hasMessageContaining(id.getValue().toString());
    }

    @Test
    @DisplayName("Should call jpaRepository deleteById with correct UUID")
    void shouldCallJpaRepositoryDeleteByIdWithCorrectUUID() {
        doNothing().when(jpaRepository).deleteById(id.getValue());

        gateway.deleteById(id);

        verify(jpaRepository, times(1)).deleteById(id.getValue());
    }

    // -------------------------------------------------------------------------
    // existsByRole
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should return true when UserType with role exists")
    void shouldReturnTrueWhenUserTypeWithRoleExists() {
        when(jpaRepository.existsByRole(UserRole.RESTAURANT_OWNER)).thenReturn(true);

        assertThat(gateway.existsByRole(UserRole.RESTAURANT_OWNER)).isTrue();
    }

    @Test
    @DisplayName("Should return false when UserType with role does not exist")
    void shouldReturnFalseWhenUserTypeWithRoleDoesNotExist() {
        when(jpaRepository.existsByRole(UserRole.RESTAURANT_OWNER)).thenReturn(false);

        assertThat(gateway.existsByRole(UserRole.RESTAURANT_OWNER)).isFalse();
    }

    @Test
    @DisplayName("Should call jpaRepository existsByRole with correct role")
    void shouldCallJpaRepositoryExistsByRoleWithCorrectRole() {
        when(jpaRepository.existsByRole(UserRole.RESTAURANT_OWNER)).thenReturn(true);

        gateway.existsByRole(UserRole.RESTAURANT_OWNER);

        verify(jpaRepository, times(1)).existsByRole(UserRole.RESTAURANT_OWNER);
    }

    // -------------------------------------------------------------------------
    // existsByRoleAndIdNot
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should return true when another UserType with role exists")
    void shouldReturnTrueWhenAnotherUserTypeWithRoleExists() {
        when(jpaRepository.existsByRoleAndIdNot(UserRole.RESTAURANT_OWNER, id.getValue())).thenReturn(true);

        assertThat(gateway.existsByRoleAndIdNot(UserRole.RESTAURANT_OWNER, id)).isTrue();
    }

    @Test
    @DisplayName("Should return false when no other UserType with role exists")
    void shouldReturnFalseWhenNoOtherUserTypeWithRoleExists() {
        when(jpaRepository.existsByRoleAndIdNot(UserRole.RESTAURANT_OWNER, id.getValue())).thenReturn(false);

        assertThat(gateway.existsByRoleAndIdNot(UserRole.RESTAURANT_OWNER, id)).isFalse();
    }

    @Test
    @DisplayName("Should call jpaRepository existsByRoleAndIdNot with correct role and UUID")
    void shouldCallJpaRepositoryExistsByRoleAndIdNotWithCorrectRoleAndUUID() {
        when(jpaRepository.existsByRoleAndIdNot(UserRole.RESTAURANT_OWNER, id.getValue())).thenReturn(false);

        gateway.existsByRoleAndIdNot(UserRole.RESTAURANT_OWNER, id);

        verify(jpaRepository, times(1)).existsByRoleAndIdNot(UserRole.RESTAURANT_OWNER, id.getValue());
    }

    // -------------------------------------------------------------------------
    // existsById
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should return true when UserType exists by id")
    void shouldReturnTrueWhenUserTypeExistsById() {
        when(jpaRepository.existsById(id.getValue())).thenReturn(true);

        assertThat(gateway.existsById(id)).isTrue();
    }

    @Test
    @DisplayName("Should return false when UserType does not exist by id")
    void shouldReturnFalseWhenUserTypeDoesNotExistById() {
        when(jpaRepository.existsById(id.getValue())).thenReturn(false);

        assertThat(gateway.existsById(id)).isFalse();
    }

    @Test
    @DisplayName("Should call jpaRepository existsById with correct UUID")
    void shouldCallJpaRepositoryExistsByIdWithCorrectUUID() {
        when(jpaRepository.existsById(id.getValue())).thenReturn(true);

        gateway.existsById(id);

        verify(jpaRepository, times(1)).existsById(id.getValue());
    }
}