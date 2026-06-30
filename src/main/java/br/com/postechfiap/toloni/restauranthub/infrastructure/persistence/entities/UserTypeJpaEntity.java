package br.com.postechfiap.toloni.restauranthub.infrastructure.persistence.entities;

import br.com.postechfiap.toloni.restauranthub.domain.usertype.UserRole;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.UserType;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.valueobject.UserTypeDescription;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.valueobject.UserTypeId;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.valueobject.UserTypeName;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "user_types")
public class UserTypeJpaEntity {

    @Id
    private UUID id;
    private String name;
    private String description;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    public static UserTypeJpaEntity fromDomain(UserType userType) {
        var entity = new UserTypeJpaEntity();
        entity.id = userType.getId().getValue();
        entity.name = userType.getName().getValue();
        entity.description = userType.getDescription().getValue();
        entity.role = userType.getRole();
        return entity;
    }

    public UserType toDomain() {
        return new UserType(
                UserTypeId.of(id),
                UserTypeName.of(name),
                UserTypeDescription.of(description),
                role
        );
    }

    public void setId(UUID value) {
        this.id = value;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}