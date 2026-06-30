package br.com.postechfiap.toloni.restauranthub.infrastructure.persistence.repositories;

import br.com.postechfiap.toloni.restauranthub.domain.usertype.UserRole;
import br.com.postechfiap.toloni.restauranthub.infrastructure.persistence.entities.UserTypeJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface UserTypeJpaRepository extends JpaRepository<UserTypeJpaEntity, UUID>,
        JpaSpecificationExecutor<UserTypeJpaEntity> {

    boolean existsByRole(UserRole role);

    boolean existsByRoleAndIdNot(UserRole role, UUID id);
}