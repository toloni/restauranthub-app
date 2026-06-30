package br.com.postechfiap.toloni.restauranthub.infrastructure.persistence.shared;

import br.com.postechfiap.toloni.restauranthub.domain.shared.pagination.PageFilter;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.UUID;

/// Generic specification builder for JPA entities.
///
/// Builds a [Specification] from a list of [PageFilter],
/// applying a case-insensitive `LIKE` filter for String fields
/// and an equality filter for other types such as UUID.
public final class JpaSpecificationBuilder {

    private JpaSpecificationBuilder() {
    }

    /// Builds a [Specification] from a list of [PageFilter].
    ///
    /// @param filters the list of [PageFilter] to apply
    /// @param <T>     the type of the JPA entity
    /// @return a [Specification] combining all filters with AND
    public static <T> Specification<T> fromFilters(List<PageFilter> filters) {
        return filters.stream()
                .map(JpaSpecificationBuilder::<T>toSpecification)
                .reduce((root, query, cb) -> cb.conjunction(), Specification::and);
    }

    private static <T> Specification<T> toSpecification(PageFilter filter) {
        return (root, query, cb) -> {
            var path = root.get(filter.getField());
            var javaType = path.getJavaType();

            if (javaType.equals(UUID.class)) {
                try {
                    return cb.equal(path, UUID.fromString(filter.getValue()));
                } catch (IllegalArgumentException e) {
                    return cb.conjunction();
                }
            }

            if (javaType.isEnum()) {
                try {
                    @SuppressWarnings("unchecked")
                    var enumValue = Enum.valueOf((Class<Enum>) javaType, filter.getValue().toUpperCase());
                    return cb.equal(path, enumValue);
                } catch (IllegalArgumentException e) {
                    return cb.conjunction();
                }
            }

            return cb.like(cb.lower(path.as(String.class)),
                    "%" + filter.getValue().toLowerCase() + "%");
        };
    }
}
