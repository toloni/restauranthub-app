package br.com.postechfiap.toloni.restauranthub.infrastructure.persistence.repositories;

import br.com.postechfiap.toloni.restauranthub.infrastructure.persistence.entities.MenuItemJpaEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface MenuItemJpaRepository extends JpaRepository<MenuItemJpaEntity, UUID>,
        JpaSpecificationExecutor<MenuItemJpaEntity> {

    boolean existsByNameAndRestaurantId(String name, UUID restaurantId);

    boolean existsByNameAndRestaurantIdAndIdNot(String name, UUID restaurantId, UUID id);

    boolean existsByRestaurantId(UUID restaurantId);

    Page<MenuItemJpaEntity> findAllByRestaurantId(UUID restaurantId, Pageable pageable);

    @Transactional
    void deleteAllByRestaurantId(UUID restaurantId);
}