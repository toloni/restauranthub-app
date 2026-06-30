package br.com.postechfiap.toloni.restauranthub.domain.shared.pagination;

import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.DomainException;

import java.util.Objects;

/// Represents a filter instruction for a paginated query.
///
/// This is an immutable value object that combines a field name
/// and a value to narrow down query results.
///
/// ## Example
/// ```java
/// var filter = PageFilter.of("name", "pizza");
/// ```
public final class PageFilter {

    private final String field;
    private final String value;

    private PageFilter(String field, String value) {
        if (field == null || field.isBlank())
            throw new DomainException("Filter field is required.");
        if (value == null || value.isBlank())
            throw new DomainException("Filter value is required.");
        this.field = field;
        this.value = value;
    }

    /// Creates a `PageFilter` with the given field and value.
    ///
    /// @param field the field name to filter by
    /// @param value the value to filter with
    /// @return a new `PageFilter`
    /// @throws DomainException if `field` or `value` is null or blank
    public static PageFilter of(String field, String value) {
        return new PageFilter(field, value);
    }

    /// @return the field name to filter by
    public String getField() {
        return field;
    }

    /// @return the filter value
    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PageFilter other)) return false;
        return Objects.equals(field, other.field)
                && Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(field, value);
    }

    @Override
    public String toString() {
        return field + "=" + value;
    }
}
