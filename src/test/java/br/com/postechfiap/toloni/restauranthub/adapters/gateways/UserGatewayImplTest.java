package br.com.postechfiap.toloni.restauranthub.adapters.gateways;

import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.EntityInUseException;
import br.com.postechfiap.toloni.restauranthub.domain.shared.pagination.PageFilter;
import br.com.postechfiap.toloni.restauranthub.domain.shared.pagination.PageRequest;
import br.com.postechfiap.toloni.restauranthub.domain.user.User;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserEmail;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserId;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserName;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserPassword;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.UserRole;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.UserType;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.valueobject.UserTypeDescription;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.valueobject.UserTypeId;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.valueobject.UserTypeName;
import br.com.postechfiap.toloni.restauranthub.infrastructure.persistence.entities.UserJpaEntity;
import br.com.postechfiap.toloni.restauranthub.infrastructure.persistence.entities.UserTypeJpaEntity;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class UserGatewayImplTest {

    @Mock
    private UserJpaRepository jpaRepository;

    @InjectMocks
    private UserGatewayImpl gateway;

    private UserId id;
    private UserTypeId userTypeId;
    private User user;
    private UserJpaEntity userEntity;
    private UserTypeJpaEntity userTypeEntity;

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
        userTypeEntity = UserTypeJpaEntity.fromDomain(new UserType(
                userTypeId,
                UserTypeName.of("Restaurant Owner"),
                UserTypeDescription.of("Owns and manages a restaurant"),
                UserRole.RESTAURANT_OWNER
        ));
        userEntity = UserJpaEntity.fromDomain(user);
    }

    // -------------------------------------------------------------------------
    // save
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should save User successfully")
    void shouldSaveUserSuccessfully() {
        when(jpaRepository.save(any(UserJpaEntity.class))).thenReturn(userEntity);

        var result = gateway.save(user);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(user.getId());
        assertThat(result.getName()).isEqualTo(user.getName());
        assertThat(result.getEmail()).isEqualTo(user.getEmail());
        assertThat(result.getUserTypeId()).isEqualTo(user.getUserTypeId());
    }

    @Test
    @DisplayName("Should call jpaRepository save once")
    void shouldCallJpaRepositorySaveOnce() {
        when(jpaRepository.save(any(UserJpaEntity.class))).thenReturn(userEntity);

        gateway.save(user);

        verify(jpaRepository, times(1)).save(any(UserJpaEntity.class));
    }

    // -------------------------------------------------------------------------
    // findById
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should find User by id successfully")
    void shouldFindUserByIdSuccessfully() {
        when(jpaRepository.findById(id.getValue())).thenReturn(Optional.of(userEntity));

        var result = gateway.findById(id);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(id);
        assertThat(result.get().getEmail().getValue()).isEqualTo("john@example.com");
    }

    @Test
    @DisplayName("Should return empty when User is not found by id")
    void shouldReturnEmptyWhenUserIsNotFoundById() {
        when(jpaRepository.findById(id.getValue())).thenReturn(Optional.empty());

        var result = gateway.findById(id);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should call jpaRepository findById with correct UUID")
    void shouldCallJpaRepositoryFindByIdWithCorrectUUID() {
        when(jpaRepository.findById(id.getValue())).thenReturn(Optional.of(userEntity));

        gateway.findById(id);

        verify(jpaRepository, times(1)).findById(id.getValue());
    }

    // -------------------------------------------------------------------------
    // findByIdWithUserTypeName
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should find User by id with UserType name successfully")
    void shouldFindUserByIdWithUserTypeNameSuccessfully() {
        when(jpaRepository.findById(id.getValue())).thenReturn(Optional.of(userEntity));

        var result = gateway.findByIdWithUserTypeName(id);

        assertThat(result).isPresent();
        assertThat(result.get().user().getId()).isEqualTo(id);
    }

    @Test
    @DisplayName("Should return null userTypeName when UserType is not loaded")
    void shouldReturnNullUserTypeNameWhenUserTypeIsNotLoaded() {
        var entityWithoutType = UserJpaEntity.fromDomain(user);
        when(jpaRepository.findById(id.getValue())).thenReturn(Optional.of(entityWithoutType));

        var result = gateway.findByIdWithUserTypeName(id);

        assertThat(result).isPresent();
        assertThat(result.get().userTypeName()).isNull();
    }

    @Test
    @DisplayName("Should return empty when User is not found by id with UserType name")
    void shouldReturnEmptyWhenUserIsNotFoundByIdWithUserTypeName() {
        when(jpaRepository.findById(id.getValue())).thenReturn(Optional.empty());

        var result = gateway.findByIdWithUserTypeName(id);

        assertThat(result).isEmpty();
    }

    // -------------------------------------------------------------------------
    // findAllWithUserTypeName
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should return paginated list of Users with UserType name without filters")
    void shouldReturnPaginatedListOfUsersWithUserTypeNameWithoutFilters() {
        var pageRequest = PageRequest.of(0, 10);
        var springPage = new org.springframework.data.domain.PageImpl<>(
                List.of(userEntity),
                org.springframework.data.domain.PageRequest.of(0, 10),
                1L
        );

        when(jpaRepository.findAll(any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(springPage);

        var result = gateway.findAllWithUserTypeName(pageRequest);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().user().getId()).isEqualTo(id);
        assertThat(result.getTotalElements()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should return paginated list of Users with UserType name with filters")
    void shouldReturnPaginatedListOfUsersWithUserTypeNameWithFilters() {
        var pageRequest = PageRequest.of(0, 10,
                List.of(PageFilter.of("name", "John")),
                List.of());
        var springPage = new org.springframework.data.domain.PageImpl<>(
                List.of(userEntity),
                org.springframework.data.domain.PageRequest.of(0, 10),
                1L
        );

        when(jpaRepository.findAll(
                any(Specification.class),
                any(Pageable.class)))
                .thenReturn(springPage);

        var result = gateway.findAllWithUserTypeName(pageRequest);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).user().getName().getValue()).isEqualTo("John Doe");
    }

    @Test
    @DisplayName("Should return empty page when no Users exist")
    void shouldReturnEmptyPageWhenNoUsersExist() {
        var pageRequest = PageRequest.of(0, 10);
        var springPage = new org.springframework.data.domain.PageImpl<UserJpaEntity>(
                List.of(),
                org.springframework.data.domain.PageRequest.of(0, 10),
                0L
        );

        when(jpaRepository.findAll(any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(springPage);

        var result = gateway.findAllWithUserTypeName(pageRequest);

        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }

    // -------------------------------------------------------------------------
    // findAll
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should return paginated list of Users without filters")
    void shouldReturnPaginatedListOfUsersWithoutFilters() {
        var pageRequest = PageRequest.of(0, 10);
        var springPage = new org.springframework.data.domain.PageImpl<>(
                List.of(userEntity),
                org.springframework.data.domain.PageRequest.of(0, 10),
                1L
        );

        when(jpaRepository.findAll(any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(springPage);

        var result = gateway.findAll(pageRequest);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1L);
    }

    // -------------------------------------------------------------------------
    // deleteById
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should delete User by id successfully")
    void shouldDeleteUserByIdSuccessfully() {
        doNothing().when(jpaRepository).deleteById(id.getValue());

        assertThatNoException().isThrownBy(() -> gateway.deleteById(id));

        verify(jpaRepository, times(1)).deleteById(id.getValue());
    }

    @Test
    @DisplayName("Should throw EntityInUseException when User is in use")
    void shouldThrowEntityInUseExceptionWhenUserIsInUse() {
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
    // existsByEmail
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should return true when User with email exists")
    void shouldReturnTrueWhenUserWithEmailExists() {
        when(jpaRepository.existsByEmail("john@example.com")).thenReturn(true);

        assertThat(gateway.existsByEmail(UserEmail.of("john@example.com"))).isTrue();
    }

    @Test
    @DisplayName("Should return false when User with email does not exist")
    void shouldReturnFalseWhenUserWithEmailDoesNotExist() {
        when(jpaRepository.existsByEmail("john@example.com")).thenReturn(false);

        assertThat(gateway.existsByEmail(UserEmail.of("john@example.com"))).isFalse();
    }

    @Test
    @DisplayName("Should call jpaRepository existsByEmail with correct email")
    void shouldCallJpaRepositoryExistsByEmailWithCorrectEmail() {
        when(jpaRepository.existsByEmail("john@example.com")).thenReturn(true);

        gateway.existsByEmail(UserEmail.of("john@example.com"));

        verify(jpaRepository, times(1)).existsByEmail("john@example.com");
    }

    // -------------------------------------------------------------------------
    // existsByEmailAndIdNot
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should return true when another User with email exists")
    void shouldReturnTrueWhenAnotherUserWithEmailExists() {
        when(jpaRepository.existsByEmailAndIdNot("john@example.com", id.getValue())).thenReturn(true);

        assertThat(gateway.existsByEmailAndIdNot(UserEmail.of("john@example.com"), id)).isTrue();
    }

    @Test
    @DisplayName("Should return false when no other User with email exists")
    void shouldReturnFalseWhenNoOtherUserWithEmailExists() {
        when(jpaRepository.existsByEmailAndIdNot("john@example.com", id.getValue())).thenReturn(false);

        assertThat(gateway.existsByEmailAndIdNot(UserEmail.of("john@example.com"), id)).isFalse();
    }

    @Test
    @DisplayName("Should call jpaRepository existsByEmailAndIdNot with correct email and UUID")
    void shouldCallJpaRepositoryExistsByEmailAndIdNotWithCorrectEmailAndUUID() {
        when(jpaRepository.existsByEmailAndIdNot("john@example.com", id.getValue())).thenReturn(false);

        gateway.existsByEmailAndIdNot(UserEmail.of("john@example.com"), id);

        verify(jpaRepository, times(1)).existsByEmailAndIdNot("john@example.com", id.getValue());
    }

    // -------------------------------------------------------------------------
    // existsById
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should return true when User exists by id")
    void shouldReturnTrueWhenUserExistsById() {
        when(jpaRepository.existsById(id.getValue())).thenReturn(true);

        assertThat(gateway.existsById(id)).isTrue();
    }

    @Test
    @DisplayName("Should return false when User does not exist by id")
    void shouldReturnFalseWhenUserDoesNotExistById() {
        when(jpaRepository.existsById(id.getValue())).thenReturn(false);

        assertThat(gateway.existsById(id)).isFalse();
    }

    // -------------------------------------------------------------------------
    // existsByUserTypeId
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should return true when User with UserTypeId exists")
    void shouldReturnTrueWhenUserWithUserTypeIdExists() {
        when(jpaRepository.existsByUserTypeId(userTypeId.getValue())).thenReturn(true);

        assertThat(gateway.existsByUserTypeId(userTypeId)).isTrue();
    }

    @Test
    @DisplayName("Should return false when no User with UserTypeId exists")
    void shouldReturnFalseWhenNoUserWithUserTypeIdExists() {
        when(jpaRepository.existsByUserTypeId(userTypeId.getValue())).thenReturn(false);

        assertThat(gateway.existsByUserTypeId(userTypeId)).isFalse();
    }

    @Test
    @DisplayName("Should call jpaRepository existsByUserTypeId with correct UUID")
    void shouldCallJpaRepositoryExistsByUserTypeIdWithCorrectUUID() {
        when(jpaRepository.existsByUserTypeId(userTypeId.getValue())).thenReturn(true);

        gateway.existsByUserTypeId(userTypeId);

        verify(jpaRepository, times(1)).existsByUserTypeId(userTypeId.getValue());
    }
}