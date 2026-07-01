package br.com.postechfiap.toloni.restauranthub.infrastructure.persistence.entities;

import br.com.postechfiap.toloni.restauranthub.domain.user.User;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserEmail;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserId;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserName;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserPassword;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.valueobject.UserTypeId;
import jakarta.persistence.*;

import java.util.UUID;

/// JPA entity representing a [User] in the persistence layer.
@NamedEntityGraph(name = "User.withUserType", attributeNodes = @NamedAttributeNode("userType"))
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

    /// Creates a [UserJpaEntity] from a [User] domain entity.
    ///
    /// @param user the [User] domain entity to convert
    /// @return the corresponding [UserJpaEntity]
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

    /// Converts this JPA entity to a [User] domain entity.
    ///
    /// @return the corresponding [User]
    public User toDomain() {
        return new User(
                UserId.of(id),
                UserName.of(name),
                UserEmail.of(email),
                UserPassword.of(password),
                UserTypeId.of(userType.getId())
        );
    }

    /// @return the associated [UserTypeJpaEntity]
    public UserTypeJpaEntity getUserType() {
        return userType;
    }

    /// @return the unique identifier of this user
    public UUID getId() {
        return id;
    }

    /// @return the name of this user
    public String getName() {
        return name;
    }
}
