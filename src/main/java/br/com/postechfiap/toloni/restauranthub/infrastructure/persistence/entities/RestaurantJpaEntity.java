package br.com.postechfiap.toloni.restauranthub.infrastructure.persistence.entities;

import br.com.postechfiap.toloni.restauranthub.domain.restaurant.Restaurant;
import br.com.postechfiap.toloni.restauranthub.domain.restaurant.valueobject.*;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserId;
import jakarta.persistence.*;

import java.util.UUID;

/// JPA entity representing a [Restaurant] in the persistence layer.
@Entity
@Table(name = "restaurants")
public class RestaurantJpaEntity {

    @Id
    private UUID id;
    private String name;
    private String address;

    @Column(name = "cuisine_type")
    private String cuisineType;

    @Column(name = "opening_hours")
    private String openingHours;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private UserJpaEntity owner;

    public static RestaurantJpaEntity fromDomain(Restaurant restaurant, UserJpaEntity owner) {
        var entity = new RestaurantJpaEntity();
        entity.id = restaurant.getId().getValue();
        entity.name = restaurant.getName().getValue();
        entity.address = restaurant.getAddress().getValue();
        entity.cuisineType = restaurant.getCuisineType().getValue();
        entity.openingHours = restaurant.getOpeningHours().getValue();
        entity.owner = owner;
        return entity;
    }

    public Restaurant toDomain() {
        return new Restaurant(
                RestaurantId.of(id),
                RestaurantName.of(name),
                RestaurantAddress.of(address),
                RestaurantCuisineType.of(cuisineType),
                RestaurantOpeningHours.of(openingHours),
                UserId.of(owner.getId())
        );
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public UserJpaEntity getOwner() {
        return owner;
    }
}
