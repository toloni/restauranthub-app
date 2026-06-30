package br.com.postechfiap.toloni.restauranthub.domain.shared.pagination;

import java.util.List;
import java.util.Objects;

/// Represents a paginated result set.
///
/// This is an immutable value object that wraps a list of content
/// along with pagination metadata, without any dependency on a specific framework.
///
/// ## Example
/// ```java
/// var page = Page.of(List.of(userType1, userType2), 0, 10, 25L);
/// ```
///
/// @param <T> the type of elements in this page
public final class Page<T> {

    private final List<T> content;
    private final int pageNumber;
    private final int pageSize;
    private final long totalElements;
    private final int totalPages;

    private Page(List<T> content, int pageNumber, int pageSize, long totalElements) {
        this.content = content != null ? List.copyOf(content) : List.of();
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = pageSize > 0 ? (int) Math.ceil((double) totalElements / pageSize) : 0;
    }

    /// Creates a `Page` with the given content and pagination metadata.
    ///
    /// @param content       the list of elements in this page
    /// @param pageNumber    the zero-based page number
    /// @param pageSize      the number of elements per page
    /// @param totalElements the total number of elements across all pages
    /// @param <T>           the type of elements
    /// @return a new `Page`
    public static <T> Page<T> of(List<T> content, int pageNumber, int pageSize, long totalElements) {
        return new Page<>(content, pageNumber, pageSize, totalElements);
    }

    /// Creates an empty `Page`.
    ///
    /// @param <T> the type of elements
    /// @return a new empty `Page`
    public static <T> Page<T> empty() {
        return new Page<>(List.of(), 0, 0, 0L);
    }

    /// @return an unmodifiable list of elements in this page
    public List<T> getContent() {
        return content;
    }

    /// @return the zero-based page number
    public int getPageNumber() {
        return pageNumber;
    }

    /// @return the number of elements per page
    public int getPageSize() {
        return pageSize;
    }

    /// @return the total number of elements across all pages
    public long getTotalElements() {
        return totalElements;
    }

    /// @return the total number of pages
    public int getTotalPages() {
        return totalPages;
    }

    /// @return `true` if this page has no elements
    public boolean isEmpty() {
        return content.isEmpty();
    }

    /// @return `true` if this is the first page
    public boolean isFirst() {
        return pageNumber == 0;
    }

    /// @return `true` if this is the last page
    public boolean isLast() {
        return pageNumber >= totalPages - 1;
    }

    /// @return `true` if there is a next page
    public boolean hasNext() {
        return pageNumber < totalPages - 1;
    }

    /// @return `true` if there is a previous page
    public boolean hasPrevious() {
        return pageNumber > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Page<?> other)) return false;
        return pageNumber == other.pageNumber
                && pageSize == other.pageSize
                && totalElements == other.totalElements
                && Objects.equals(content, other.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content, pageNumber, pageSize, totalElements);
    }

    @Override
    public String toString() {
        return "Page{pageNumber=" + pageNumber + ", pageSize=" + pageSize
                + ", totalElements=" + totalElements + ", totalPages=" + totalPages
                + ", content=" + content + "}";
    }
}
