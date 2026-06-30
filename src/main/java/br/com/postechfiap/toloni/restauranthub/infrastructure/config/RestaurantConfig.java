package br.com.postechfiap.toloni.restauranthub.infrastructure.config;

import br.com.postechfiap.toloni.restauranthub.adapters.controllers.RestaurantController;
import br.com.postechfiap.toloni.restauranthub.adapters.gateways.RestaurantGatewayImpl;
import br.com.postechfiap.toloni.restauranthub.application.usecases.restaurant.*;
import br.com.postechfiap.toloni.restauranthub.domain.menuitem.MenuItemGateway;
import br.com.postechfiap.toloni.restauranthub.domain.restaurant.RestaurantGateway;
import br.com.postechfiap.toloni.restauranthub.domain.shared.authorization.AuthorizationService;
import br.com.postechfiap.toloni.restauranthub.domain.user.UserGateway;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.UserTypeGateway;
import br.com.postechfiap.toloni.restauranthub.infrastructure.persistence.repositories.RestaurantJpaRepository;
import br.com.postechfiap.toloni.restauranthub.infrastructure.persistence.repositories.UserJpaRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/// Spring configuration for [Restaurant] beans.
@Configuration
public class RestaurantConfig {

    @Bean
    public RestaurantGateway restaurantGateway(RestaurantJpaRepository jpaRepository,
                                               UserJpaRepository userJpaRepository) {
        return new RestaurantGatewayImpl(jpaRepository, userJpaRepository);
    }

    @Bean
    public CreateRestaurantUseCase createRestaurantUseCase(RestaurantGateway restaurantGateway,
                                                           UserGateway userGateway,
                                                           UserTypeGateway userTypeGateway,
                                                           AuthorizationService authorizationService) {
        return new CreateRestaurantUseCase(restaurantGateway, userGateway, userTypeGateway, authorizationService);
    }

    @Bean
    public UpdateRestaurantUseCase updateRestaurantUseCase(RestaurantGateway restaurantGateway,
                                                           UserGateway userGateway,
                                                           AuthorizationService authorizationService) {
        return new UpdateRestaurantUseCase(restaurantGateway, userGateway, authorizationService);
    }

    @Bean
    public DeleteRestaurantUseCase deleteRestaurantUseCase(RestaurantGateway restaurantGateway,
                                                           MenuItemGateway menuItemGateway,
                                                           AuthorizationService authorizationService) {
        return new DeleteRestaurantUseCase(restaurantGateway, menuItemGateway, authorizationService);
    }

    @Bean
    public FindRestaurantByIdUseCase findRestaurantByIdUseCase(RestaurantGateway restaurantGateway) {
        return new FindRestaurantByIdUseCase(restaurantGateway);
    }

    @Bean
    public FindAllRestaurantsUseCase findAllRestaurantsUseCase(RestaurantGateway restaurantGateway) {
        return new FindAllRestaurantsUseCase(restaurantGateway);
    }

    @Bean
    public TransferRestaurantOwnershipUseCase transferRestaurantOwnershipUseCase(
            RestaurantGateway restaurantGateway,
            AuthorizationService authorizationService) {
        return new TransferRestaurantOwnershipUseCase(restaurantGateway, authorizationService);
    }

    @Bean
    public RestaurantController restaurantController(
            CreateRestaurantUseCase createRestaurantUseCase,
            UpdateRestaurantUseCase updateRestaurantUseCase,
            DeleteRestaurantUseCase deleteRestaurantUseCase,
            FindRestaurantByIdUseCase findRestaurantByIdUseCase,
            FindAllRestaurantsUseCase findAllRestaurantsUseCase,
            TransferRestaurantOwnershipUseCase transferRestaurantOwnershipUseCase) {
        return new RestaurantController(
                createRestaurantUseCase,
                updateRestaurantUseCase,
                deleteRestaurantUseCase,
                findRestaurantByIdUseCase,
                findAllRestaurantsUseCase,
                transferRestaurantOwnershipUseCase
        );
    }
}
