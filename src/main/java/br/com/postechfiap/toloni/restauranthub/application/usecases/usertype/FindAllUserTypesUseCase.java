package br.com.postechfiap.toloni.restauranthub.application.usecases.usertype;

import br.com.postechfiap.toloni.restauranthub.domain.shared.pagination.Page;
import br.com.postechfiap.toloni.restauranthub.domain.shared.pagination.PageRequest;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.UserRole;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.UserTypeGateway;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.valueobject.UserTypeId;

/// Use case responsible for retrieving a paginated list of [UserType] instances.
///
/// ## Flow
/// 1. Receives a [PageRequest] with pagination, filter, and sort parameters
/// 2. Retrieves a paginated list of [UserType] instances from the persistence layer
/// 3. Maps and returns them as a [Page] of [Output]
public class FindAllUserTypesUseCase {

    private final UserTypeGateway userTypeGateway;

    /// @param userTypeGateway the gateway for [UserType] persistence operations
    public FindAllUserTypesUseCase(UserTypeGateway userTypeGateway) {
        this.userTypeGateway = userTypeGateway;
    }

    /// Input data required to retrieve a paginated list of [UserType] instances.
    ///
    /// @param pageRequest the [PageRequest] carrying page number, size, filters, and sorting
    public record Input(PageRequest pageRequest) {
    }

    /// Output data returned for each [UserType].
    ///
    /// @param id          the [UserTypeId] of the user type
    /// @param name        the name of the user type
    /// @param description the description of the user type
    /// @param role        the [UserRole] of the user type
    public record Output(UserTypeId id, String name, String description, UserRole role) {
    }

    /// Executes the use case with the given input.
    ///
    /// @param input the [Input] data carrying the [PageRequest]
    /// @return a [Page] of [Output] containing the paginated user types
    public Page<Output> execute(Input input) {
        var page = userTypeGateway.findAll(input.pageRequest());

        var content = page.getContent()
                .stream()
                .map(userType -> new Output(
                        userType.getId(),
                        userType.getName().getValue(),
                        userType.getDescription().getValue(),
                        userType.getRole()
                ))
                .toList();

        return Page.of(content, page.getPageNumber(), page.getPageSize(), page.getTotalElements());
    }
}
