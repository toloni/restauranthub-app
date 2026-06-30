package br.com.postechfiap.toloni.restauranthub.domain.shared.pagination;

import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.DomainException;

import java.util.List;
import java.util.Objects;

/// Represents a pagination request carrying page number, size, filters, and sorting.
///
/// This is an immutable value object used to parameterize paginated queries
/// in gateways and use cases, without any dependency on a specific framework.
///
/// ## Example
/// ```java
/// var request = PageRequest.of(0, 10,
///     List.of(PageFilter.of("name", "pizza")),
///     List.of(PageSort.asc("name"))
/// );
/// ```
public final class PageRequest {

    private final int pageNumber;
    private final int pageSize;
    private final List<PageFilter> filters;
    private final List<PageSort> sorts;

    private PageRequest(int pageNumber, int pageSize, List<PageFilter> filters, List<PageSort> sorts) {
        if (pageNumber < 0)
            throw new DomainException("Page number must not be negative.");
        if (pageSize < 1)
            throw new DomainException("Page size must be at least 1.");
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.filters = filters != null ? List.copyOf(filters) : List.of();
        this.sorts = sorts != null ? List.copyOf(sorts) : List.of();
    }

    /// Creates a `PageRequest` with filters and sorting.
    ///
    /// @param pageNumber the zero-based page number
    /// @param pageSize   the number of elements per page
    /// @param filters    the list of [PageFilter] to apply, or `null` for none
    /// @param sorts      the list of [PageSort] to apply, or `null` for none
    /// @return a new `PageRequest`
    /// @throws DomainException if `pageNumber` is negative or `pageSize` is less than 1
    public static PageRequest of(int pageNumber, int pageSize, List<PageFilter> filters, List<PageSort> sorts) {
        return new PageRequest(pageNumber, pageSize, filters, sorts);
    }

    /// Creates a `PageRequest` without filters or sorting.
    ///
    /// @param pageNumber the zero-based page number
    /// @param pageSize   the number of elements per page
    /// @return a new `PageRequest`
    /// @throws DomainException if `pageNumber` is negative or `pageSize` is less than 1
    public static PageRequest of(int pageNumber, int pageSize) {
        return new PageRequest(pageNumber, pageSize, null, null);
    }

    /// @return the zero-based page number
    public int getPageNumber() {
        return pageNumber;
    }

    /// @return the number of elements per page
    public int getPageSize() {
        return pageSize;
    }

    /// @return an unmodifiable list of [PageFilter] applied to this request
    public List<PageFilter> getFilters() {
        return filters;
    }

    /// @return an unmodifiable list of [PageSort] applied to this request
    public List<PageSort> getSorts() {
        return sorts;
    }

    /// @return `true` if this request has at least one filter
    public boolean hasFilters() {
        return !filters.isEmpty();
    }

    /// @return `true` if this request has at least one sort
    public boolean hasSorts() {
        return !sorts.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PageRequest other)) return false;
        return pageNumber == other.pageNumber
                && pageSize == other.pageSize
                && Objects.equals(filters, other.filters)
                && Objects.equals(sorts, other.sorts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pageNumber, pageSize, filters, sorts);
    }

    @Override
    public String toString() {
        return "PageRequest{page=" + pageNumber + ", size=" + pageSize
                + ", filters=" + filters + ", sorts=" + sorts + "}";
    }
}