package br.com.postechfiap.toloni.restauranthub.infrastructure.config;

import br.com.postechfiap.toloni.restauranthub.adapters.controllers.MenuItemController;
import br.com.postechfiap.toloni.restauranthub.adapters.gateways.MenuItemGatewayImpl;
import br.com.postechfiap.toloni.restauranthub.application.usecases.menuitem.*;
import br.com.postechfiap.toloni.restauranthub.domain.menuitem.MenuItemGateway;
import br.com.postechfiap.toloni.restauranthub.domain.restaurant.RestaurantGateway;
import br.com.postechfiap.toloni.restauranthub.domain.shared.authorization.AuthorizationService;
import br.com.postechfiap.toloni.restauranthub.infrastructure.persistence.repositories.MenuItemJpaRepository;
import br.com.postechfiap.toloni.restauranthub.infrastructure.persistence.repositories.RestaurantJpaRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/// Spring configuration for [MenuItem] beans.
@Configuration
public class MenuItemConfig {

    @Bean
    public MenuItemGateway menuItemGateway(MenuItemJpaRepository jpaRepository,
                                           RestaurantJpaRepository restaurantJpaRepository) {
        return new MenuItemGatewayImpl(jpaRepository, restaurantJpaRepository);
    }

    @Bean
    public CreateMenuItemUseCase createMenuItemUseCase(MenuItemGateway menuItemGateway,
                                                       RestaurantGateway restaurantGateway,
                                                       AuthorizationService authorizationService) {
        return new CreateMenuItemUseCase(menuItemGateway, restaurantGateway, authorizationService);
    }

    @Bean
    public UpdateMenuItemUseCase updateMenuItemUseCase(MenuItemGateway menuItemGateway,
                                                       RestaurantGateway restaurantGateway,
                                                       AuthorizationService authorizationService) {
        return new UpdateMenuItemUseCase(menuItemGateway, restaurantGateway, authorizationService);
    }

    @Bean
    public DeleteMenuItemUseCase deleteMenuItemUseCase(MenuItemGateway menuItemGateway,
                                                       RestaurantGateway restaurantGateway,
                                                       AuthorizationService authorizationService) {
        return new DeleteMenuItemUseCase(menuItemGateway, restaurantGateway, authorizationService);
    }

    @Bean
    public FindMenuItemByIdUseCase findMenuItemByIdUseCase(MenuItemGateway menuItemGateway) {
        return new FindMenuItemByIdUseCase(menuItemGateway);
    }

    @Bean
    public FindAllMenuItemsUseCase findAllMenuItemsUseCase(MenuItemGateway menuItemGateway) {
        return new FindAllMenuItemsUseCase(menuItemGateway);
    }

    @Bean
    public MenuItemController menuItemController(
            CreateMenuItemUseCase createMenuItemUseCase,
            UpdateMenuItemUseCase updateMenuItemUseCase,
            DeleteMenuItemUseCase deleteMenuItemUseCase,
            FindMenuItemByIdUseCase findMenuItemByIdUseCase,
            FindAllMenuItemsUseCase findAllMenuItemsUseCase) {
        return new MenuItemController(
                createMenuItemUseCase,
                updateMenuItemUseCase,
                deleteMenuItemUseCase,
                findMenuItemByIdUseCase,
                findAllMenuItemsUseCase
        );
    }
}
