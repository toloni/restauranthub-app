package br.com.postechfiap.toloni.restauranthub.domain.shared.pagination;

import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.DomainException;

import java.util.Objects;

/// Represents a sort instruction for a paginated query.
///
/// This is an immutable value object that combines a field name
/// and a [SortDirection] to define how results should be ordered.
///
/// ## Example
/// ```java
/// var sort = PageSort.of("name", SortDirection.ASC);
/// ```
public final class PageSort {

    private final String field;
    private final SortDirection direction;

    private PageSort(String field, SortDirection direction) {
        if (field == null || field.isBlank())
            throw new DomainException("Sort field is required.");
        if (direction == null)
            throw new DomainException("Sort direction is required.");
        this.field = field;
        this.direction = direction;
    }

    /// Creates a `PageSort` with the given field and direction.
    ///
    /// @param field     the field name to sort by
    /// @param direction the [SortDirection] of the sort
    /// @return a new `PageSort`
    /// @throws DomainException if `field` is null or blank, or `direction` is null
    public static PageSort of(String field, SortDirection direction) {
        return new PageSort(field, direction);
    }

    /// Creates a `PageSort` with ascending direction.
    ///
    /// @param field the field name to sort by
    /// @return a new `PageSort` with [SortDirection#ASC]
    public static PageSort asc(String field) {
        return new PageSort(field, SortDirection.ASC);
    }

    /// Creates a `PageSort` with descending direction.
    ///
    /// @param field the field name to sort by
    /// @return a new `PageSort` with [SortDirection#DESC]
    public static PageSort desc(String field) {
        return new PageSort(field, SortDirection.DESC);
    }

    /// @return the field name to sort by
    public String getField() {
        return field;
    }

    /// @return the [SortDirection] of this sort
    public SortDirection getDirection() {
        return direction;
    }

    /// @return `true` if the direction is [SortDirection#ASC]
    public boolean isAscending() {
        return direction == SortDirection.ASC;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PageSort other)) return false;
        return Objects.equals(field, other.field)
                && direction == other.direction;
    }

    @Override
    public int hashCode() {
        return Objects.hash(field, direction);
    }

    @Override
    public String toString() {
        return field + " " + direction.name();
    }
}
