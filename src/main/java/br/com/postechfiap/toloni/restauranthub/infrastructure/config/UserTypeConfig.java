package br.com.postechfiap.toloni.restauranthub.infrastructure.config;

import br.com.postechfiap.toloni.restauranthub.adapters.controllers.UserTypeController;
import br.com.postechfiap.toloni.restauranthub.adapters.gateways.UserTypeGatewayImpl;
import br.com.postechfiap.toloni.restauranthub.application.usecases.usertype.*;
import br.com.postechfiap.toloni.restauranthub.domain.user.UserGateway;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.UserTypeGateway;
import br.com.postechfiap.toloni.restauranthub.infrastructure.persistence.repositories.UserTypeJpaRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserTypeConfig {

    @Bean
    public UserTypeGatewayImpl userTypeRepositoryImpl(UserTypeJpaRepository userTypeJpaRepository) {
        return new UserTypeGatewayImpl(userTypeJpaRepository);
    }

    @Bean
    public CreateUserTypeUseCase createUserTypeUseCase(UserTypeGateway userTypeGateway) {
        return new CreateUserTypeUseCase(userTypeGateway);
    }

    @Bean
    public FindUserTypeByIdUseCase getUserTypeByIdUseCase(UserTypeGateway userTypeGateway) {
        return new FindUserTypeByIdUseCase(userTypeGateway);
    }

    @Bean
    public FindAllUserTypesUseCase getAllUserTypesUseCase(UserTypeGateway userTypeGateway) {
        return new FindAllUserTypesUseCase(userTypeGateway);
    }

    @Bean
    public UpdateUserTypeUseCase updateUserTypeUseCase(UserTypeGateway repository) {
        return new UpdateUserTypeUseCase(repository);
    }

    @Bean
    public DeleteUserTypeUseCase deleteUserTypeUseCase(UserTypeGateway userTypeGateway, UserGateway userGateway) {
        return new DeleteUserTypeUseCase(userTypeGateway, userGateway);
    }

    @Bean
    public UserTypeController userTypeController(
            CreateUserTypeUseCase createUserTypeUseCase,
            UpdateUserTypeUseCase updateUserTypeUseCase,
            DeleteUserTypeUseCase deleteUserTypeUseCase,
            FindUserTypeByIdUseCase findUserTypeByIdUseCase,
            FindAllUserTypesUseCase findAllUserTypesUseCase
    ) {
        return new UserTypeController(
                createUserTypeUseCase,
                updateUserTypeUseCase,
                deleteUserTypeUseCase,
                findUserTypeByIdUseCase,
                findAllUserTypesUseCase);
    }

}
