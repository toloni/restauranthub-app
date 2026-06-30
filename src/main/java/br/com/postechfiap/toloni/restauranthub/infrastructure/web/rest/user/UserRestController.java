package br.com.postechfiap.toloni.restauranthub.infrastructure.web.rest.user;

import br.com.postechfiap.toloni.restauranthub.adapters.controllers.UserController;
import br.com.postechfiap.toloni.restauranthub.application.usecases.user.DeleteUserUseCase;
import br.com.postechfiap.toloni.restauranthub.application.usecases.user.FindAllUsersUseCase;
import br.com.postechfiap.toloni.restauranthub.application.usecases.user.FindUserByIdUseCase;
import br.com.postechfiap.toloni.restauranthub.domain.shared.pagination.*;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserId;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/// REST controller that implements [UserApi] for [User] operations.
///
/// Receives HTTP requests, delegates to [UserController], and maps
/// the results to [UserResponse].
@RestController
public class UserRestController implements UserApi {

    private final UserController userController;

    /// @param userController the [UserController] adapter
    public UserRestController(UserController userController) {
        this.userController = userController;
    }

    @Override
    public UserResponse create(@RequestBody UserRequest request) {
        return UserResponse.from(userController.create(request.toCreateInput()));
    }

    @Override
    public UserResponse update(@PathVariable String id, @RequestBody UserRequest request) {
        return UserResponse.from(userController.update(request.toUpdateInput(UserId.of(id))));
    }

    @Override
    public void delete(@PathVariable String id) {
        userController.delete(new DeleteUserUseCase.Input(UserId.of(id)));
    }

    @Override
    public UserResponse findById(@PathVariable String id) {
        return UserResponse.from(userController.findById(
                new FindUserByIdUseCase.Input(UserId.of(id))
        ));
    }

    @Override
    public Page<UserResponse> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String direction,
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) String filterValue) {

        var sorts = sort != null
                ? List.<PageSort>of(PageSort.of(sort, direction != null
                ? SortDirection.valueOf(direction.toUpperCase())
                : SortDirection.ASC))
                : List.<PageSort>of();

        var filters = filter != null && filterValue != null
                ? List.<PageFilter>of(PageFilter.of(filter, filterValue))
                : List.<PageFilter>of();

        var output = userController.findAll(
                new FindAllUsersUseCase.Input(PageRequest.of(page, size, filters, sorts))
        );

        var content = output.getContent().stream()
                .map(UserResponse::from)
                .toList();

        return Page.of(content, output.getPageNumber(), output.getPageSize(), output.getTotalElements());
    }
}
