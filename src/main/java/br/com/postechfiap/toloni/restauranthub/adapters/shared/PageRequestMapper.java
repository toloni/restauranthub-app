package br.com.postechfiap.toloni.restauranthub.adapters.shared;

import br.com.postechfiap.toloni.restauranthub.domain.shared.pagination.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

/// Utility class for converting domain [PageRequest] to Spring Data [org.springframework.data.domain.PageRequest].
public final class PageRequestMapper {

    private PageRequestMapper() {
    }

    /// Converts a domain [PageRequest] to a Spring Data [org.springframework.data.domain.PageRequest].
    ///
    /// @param pageRequest the domain [PageRequest] to convert
    /// @return a Spring Data [org.springframework.data.domain.PageRequest]
    public static org.springframework.data.domain.PageRequest toPageable(PageRequest pageRequest) {
        var sort = pageRequest.hasSorts()
                ? Sort.by(pageRequest.getSorts().stream()
                .map(s -> s.isAscending() ? Order.asc(s.getField()) : Order.desc(s.getField()))
                .toList())
                : Sort.unsorted();

        return org.springframework.data.domain.PageRequest.of(
                pageRequest.getPageNumber(),
                pageRequest.getPageSize(),
                sort
        );
    }
}

