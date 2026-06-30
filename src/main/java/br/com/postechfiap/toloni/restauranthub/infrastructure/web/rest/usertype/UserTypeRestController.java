package br.com.postechfiap.toloni.restauranthub.infrastructure.web.rest.usertype;

import br.com.postechfiap.toloni.restauranthub.adapters.controllers.UserTypeController;
import br.com.postechfiap.toloni.restauranthub.application.usecases.usertype.DeleteUserTypeUseCase;
import br.com.postechfiap.toloni.restauranthub.application.usecases.usertype.FindAllUserTypesUseCase;
import br.com.postechfiap.toloni.restauranthub.application.usecases.usertype.FindUserTypeByIdUseCase;
import br.com.postechfiap.toloni.restauranthub.domain.shared.pagination.*;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.valueobject.UserTypeId;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserTypeRestController implements UserTypeApi {

    private final UserTypeController userTypeController;

    public UserTypeRestController(UserTypeController userTypeController) {
        this.userTypeController = userTypeController;
    }

    @Override
    public UserTypeResponse create(@RequestBody UserTypeRequest request) {
        return UserTypeResponse.from(userTypeController.create(request.toCreateInput()));
    }

    @Override
    public UserTypeResponse update(@PathVariable String id,
                                   @RequestBody UserTypeRequest request) {
        return UserTypeResponse.from(userTypeController.update(request.toUpdateInput(UserTypeId.of(id))));
    }

    @Override
    public void delete(@PathVariable String id) {
        userTypeController.delete(new DeleteUserTypeUseCase.Input(UserTypeId.of(id)));
    }

    @Override
    public UserTypeResponse findById(@PathVariable String id) {
        var output = userTypeController.findById(
                new FindUserTypeByIdUseCase.Input(UserTypeId.of(id)));
        return UserTypeResponse.from(output);
    }

    @Override
    public Page<UserTypeResponse> findAll(
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

        var output = userTypeController.findAll(
                new FindAllUserTypesUseCase.Input(PageRequest.of(page, size, filters, sorts)));

        var content = output.getContent().stream()
                .map(UserTypeResponse::from)
                .toList();

        return Page.of(content, output.getPageNumber(), output.getPageSize(), output.getTotalElements());
    }
}
