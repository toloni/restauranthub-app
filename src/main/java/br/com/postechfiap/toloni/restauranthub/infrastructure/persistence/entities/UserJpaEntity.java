package br.com.postechfiap.toloni.restauranthub.infrastructure.persistence.entities;

import br.com.postechfiap.toloni.restauranthub.domain.user.User;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserEmail;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserId;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserName;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserPassword;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.valueobject.UserTypeId;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "users")
public class UserJpaEntity {

    @Id
    private UUID id;
    private String name;
    private String email;
    private String password;

    @Column(name = "user_type_id", insertable = false, updatable = false)
    private UUID userTypeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_type_id")
    private UserTypeJpaEntity userType;

    public static UserJpaEntity fromDomain(User user) {
        var entity = new UserJpaEntity();
        entity.id = user.getId().getValue();
        entity.name = user.getName().getValue();
        entity.email = user.getEmail().getValue();
        entity.password = user.getPassword().getValue();
        var userTypeEntity = new UserTypeJpaEntity();
        userTypeEntity.setId(user.getUserTypeId().getValue());
        entity.userType = userTypeEntity;
        return entity;
    }

    public User toDomain() {
        return new User(
                UserId.of(id),
                UserName.of(name),
                UserEmail.of(email),
                UserPassword.of(password),
                UserTypeId.of(userType.getId())
        );
    }

    public UserTypeJpaEntity getUserType() {
        return userType;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
