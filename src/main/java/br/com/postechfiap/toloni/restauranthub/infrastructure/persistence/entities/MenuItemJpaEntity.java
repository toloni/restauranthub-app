package br.com.postechfiap.toloni.restauranthub.infrastructure.persistence.entities;

import br.com.postechfiap.toloni.restauranthub.domain.menuitem.MenuItem;
import br.com.postechfiap.toloni.restauranthub.domain.menuitem.valueobject.*;
import br.com.postechfiap.toloni.restauranthub.domain.restaurant.valueobject.RestaurantId;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

@Entity
@Table(name = "menu_items")
public class MenuItemJpaEntity {

    @Id
    private UUID id;
    private String name;
    private String description;
    private BigDecimal price;
    private String currency;

    @Column(name = "dine_in_only")
    private boolean dineInOnly;

    @Column(name = "image_path")
    private String imagePath;

    @Column(name = "restaurant_id", insertable = false, updatable = false)
    private UUID restaurantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private RestaurantJpaEntity restaurant;

    public static MenuItemJpaEntity fromDomain(MenuItem menuItem, RestaurantJpaEntity restaurant) {
        var entity = new MenuItemJpaEntity();
        entity.id = menuItem.getId().getValue();
        entity.name = menuItem.getName().getValue();
        entity.description = menuItem.getDescription().getValue();
        entity.price = menuItem.getPrice().getAmount();
        entity.currency = menuItem.getPrice().getCurrency().getCurrencyCode();
        entity.dineInOnly = menuItem.isDineInOnly();
        entity.imagePath = menuItem.getImagePath().getValue();
        entity.restaurant = restaurant;
        return entity;
    }

    public MenuItem toDomain() {
        return new MenuItem(
                MenuItemId.of(id),
                MenuItemName.of(name),
                MenuItemDescription.of(description),
                MenuItemPrice.of(price, Currency.getInstance(currency)),
                dineInOnly,
                MenuItemImagePath.of(imagePath),
                RestaurantId.of(restaurant.getId())
        );
    }

    public RestaurantJpaEntity getRestaurant() {
        return restaurant;
    }
}
