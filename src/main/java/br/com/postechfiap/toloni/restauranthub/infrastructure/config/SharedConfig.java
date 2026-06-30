package br.com.postechfiap.toloni.restauranthub.infrastructure.config;

import br.com.postechfiap.toloni.restauranthub.domain.shared.authorization.AuthorizationService;
import br.com.postechfiap.toloni.restauranthub.domain.user.UserGateway;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.UserTypeGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SharedConfig {

    @Bean
    public AuthorizationService authorizationService(UserGateway userGateway,
                                                     UserTypeGateway userTypeGateway) {
        return new AuthorizationService(userGateway, userTypeGateway);
    }
}
