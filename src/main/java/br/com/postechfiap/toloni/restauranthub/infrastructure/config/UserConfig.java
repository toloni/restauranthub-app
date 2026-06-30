package br.com.postechfiap.toloni.restauranthub.infrastructure.config;

import br.com.postechfiap.toloni.restauranthub.adapters.controllers.UserController;
import br.com.postechfiap.toloni.restauranthub.adapters.gateways.UserGatewayImpl;
import br.com.postechfiap.toloni.restauranthub.application.usecases.user.*;
import br.com.postechfiap.toloni.restauranthub.domain.restaurant.RestaurantGateway;
import br.com.postechfiap.toloni.restauranthub.domain.user.UserGateway;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.UserTypeGateway;
import br.com.postechfiap.toloni.restauranthub.infrastructure.persistence.repositories.UserJpaRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/// Spring configuration for [User] beans.
@Configuration
public class UserConfig {

    @Bean
    public UserGatewayImpl userRepositoryImpl(UserJpaRepository userJpaRepository) {
        return new UserGatewayImpl(userJpaRepository);
    }

    @Bean
    public CreateUserUseCase createUserUseCase(UserGateway userGateway, UserTypeGateway userTypeGateway) {
        return new CreateUserUseCase(userGateway, userTypeGateway);
    }

    @Bean
    public FindUserByIdUseCase getUserByIdUseCase(UserGateway userGateway) {
        return new FindUserByIdUseCase(userGateway);
    }

    @Bean
    public FindAllUsersUseCase getAllUsersUseCase(UserGateway userGateway) {
        return new FindAllUsersUseCase(userGateway);
    }

    @Bean
    public UpdateUserUseCase updateUserUseCase(UserGateway userGateway, UserTypeGateway userTypeGateway) {
        return new UpdateUserUseCase(userGateway, userTypeGateway);
    }

    @Bean
    public DeleteUserUseCase deleteUserUseCase(UserGateway userGateway, RestaurantGateway restaurantGateway) {
        return new DeleteUserUseCase(userGateway, restaurantGateway);
    }

    @Bean
    public UserController userController(
            CreateUserUseCase createUserUseCase,
            UpdateUserUseCase updateUserUseCase,
            DeleteUserUseCase deleteUserUseCase,
            FindUserByIdUseCase findUserByIdUseCase,
            FindAllUsersUseCase findAllUsersUseCase
    ) {
        return new UserController(
                createUserUseCase,
                updateUserUseCase,
                deleteUserUseCase,
                findUserByIdUseCase,
                findAllUsersUseCase);
    }

}
