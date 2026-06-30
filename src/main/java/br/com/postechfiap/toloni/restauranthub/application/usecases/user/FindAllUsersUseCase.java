package br.com.postechfiap.toloni.restauranthub.application.usecases.user;

import br.com.postechfiap.toloni.restauranthub.domain.shared.pagination.Page;
import br.com.postechfiap.toloni.restauranthub.domain.shared.pagination.PageRequest;
import br.com.postechfiap.toloni.restauranthub.domain.user.UserGateway;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserId;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.valueobject.UserTypeId;

/// Use case responsible for retrieving a paginated list of [User] instances.
///
/// ## Flow
/// 1. Receives a [PageRequest] with pagination, filter, and sort parameters
/// 2. Retrieves a paginated list of [User] instances from the persistence layer
/// 3. Maps and returns them as a [Page] of [Output]
public class FindAllUsersUseCase {

    private final UserGateway userGateway;

    /// @param userGateway the gateway for [User] persistence operations
    public FindAllUsersUseCase(UserGateway userGateway) {
        this.userGateway = userGateway;
    }

    /// Input data required to retrieve a paginated list of [User] instances.
    ///
    /// @param pageRequest the [PageRequest] carrying page number, size, filters, and sorting
    public record Input(PageRequest pageRequest) {
    }

    /// Output data returned for each [User].
    ///
    /// @param id         the [UserId] of the user
    /// @param name       the name of the user
    /// @param email      the email of the user
    /// @param userTypeId the [UserTypeId] of the user
    public record Output(UserId id, String name, String email, UserTypeId userTypeId, String userTypeName) {}


    /// Executes the use case with the given input.
    ///
    /// @param input the [Input] data carrying the [PageRequest]
    /// @return a [Page] of [Output] containing the paginated users
    public Page<Output> execute(Input input) {
        var page = userGateway.findAllWithUserTypeName(input.pageRequest());

        var content = page.getContent()
                .stream()
                .map(enriched -> new Output(
                        enriched.user().getId(),
                        enriched.user().getName().getValue(),
                        enriched.user().getEmail().getValue(),
                        enriched.user().getUserTypeId(),
                        enriched.userTypeName()
                ))
                .toList();

        return Page.of(content, page.getPageNumber(), page.getPageSize(), page.getTotalElements());
    }
}
