package br.com.postechfiap.toloni.restauranthub.adapters.controllers;

import br.com.postechfiap.toloni.restauranthub.adapters.presenters.user.UserPresenter;
import br.com.postechfiap.toloni.restauranthub.adapters.presenters.user.UserViewModel;
import br.com.postechfiap.toloni.restauranthub.application.pagination.Page;
import br.com.postechfiap.toloni.restauranthub.application.pagination.PageRequest;
import br.com.postechfiap.toloni.restauranthub.application.usecases.user.*;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserEmail;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserId;
import br.com.postechfiap.toloni.restauranthub.domain.user.valueobject.UserName;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.valueobject.UserTypeId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private CreateUserUseCase createUserUseCase;

    @Mock
    private UpdateUserUseCase updateUserUseCase;

    @Mock
    private DeleteUserUseCase deleteUserUseCase;

    @Mock
    private FindUserByIdUseCase findUserByIdUseCase;

    @Mock
    private FindAllUsersUseCase findAllUsersUseCase;

    @Mock
    private UserPresenter userPresenter;

    @InjectMocks
    private UserController controller;

    private UserId id;
    private UserTypeId userTypeId;

    @BeforeEach
    void setUp() {
        id = UserId.generate();
        userTypeId = UserTypeId.generate();
    }

    // -------------------------------------------------------------------------
    // create
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should delegate create to CreateUserUseCase and present result")
    void shouldDelegateCreateToCreateUserUseCase() {
        var input = new CreateUserUseCase.Input("John Doe", "john@example.com", "password123", userTypeId);
        var output = new CreateUserUseCase.Output(id, UserName.of("John Doe"), UserEmail.of("john@example.com"), userTypeId);
        var viewModel = new UserViewModel(id.getValue(), "John Doe", "john@example.com", userTypeId.getValue(), null);

        when(createUserUseCase.execute(input)).thenReturn(output);
        when(userPresenter.present(output)).thenReturn(viewModel);

        var result = controller.create(input);

        assertThat(result).isEqualTo(viewModel);
        verify(createUserUseCase, times(1)).execute(input);
        verify(userPresenter, times(1)).present(output);
    }

    // -------------------------------------------------------------------------
    // update
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should delegate update to UpdateUserUseCase and present result")
    void shouldDelegateUpdateToUpdateUserUseCase() {
        var input = new UpdateUserUseCase.Input(id, "Jane Doe", null, null, null);
        var output = new UpdateUserUseCase.Output(id, UserName.of("Jane Doe"), UserEmail.of("john@example.com"), userTypeId);
        var viewModel = new UserViewModel(id.getValue(), "Jane Doe", "john@example.com", userTypeId.getValue(), null);

        when(updateUserUseCase.execute(input)).thenReturn(output);
        when(userPresenter.present(output)).thenReturn(viewModel);

        var result = controller.update(input);

        assertThat(result).isEqualTo(viewModel);
        verify(updateUserUseCase, times(1)).execute(input);
        verify(userPresenter, times(1)).present(output);
    }

    // -------------------------------------------------------------------------
    // delete
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should delegate delete to DeleteUserUseCase")
    void shouldDelegateDeleteToDeleteUserUseCase() {
        var input = new DeleteUserUseCase.Input(id);

        doNothing().when(deleteUserUseCase).execute(input);

        controller.delete(input);

        verify(deleteUserUseCase, times(1)).execute(input);
    }

    // -------------------------------------------------------------------------
    // findById
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should delegate findById to FindUserByIdUseCase and present result")
    void shouldDelegateFindByIdToFindUserByIdUseCase() {
        var input = new FindUserByIdUseCase.Input(id);
        var output = new FindUserByIdUseCase.Output(id, UserName.of("John Doe"), UserEmail.of("john@example.com"), userTypeId, "Restaurant Owner");
        var viewModel = new UserViewModel(id.getValue(), "John Doe", "john@example.com", userTypeId.getValue(), "Restaurant Owner");

        when(findUserByIdUseCase.execute(input)).thenReturn(output);
        when(userPresenter.present(output)).thenReturn(viewModel);

        var result = controller.findById(input);

        assertThat(result).isEqualTo(viewModel);
        verify(findUserByIdUseCase, times(1)).execute(input);
        verify(userPresenter, times(1)).present(output);
    }

    // -------------------------------------------------------------------------
    // findAll
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should delegate findAll to FindAllUsersUseCase and present results")
    void shouldDelegateFindAllToFindAllUsersUseCase() {
        var pageRequest = PageRequest.of(0, 10);
        var input = new FindAllUsersUseCase.Input(pageRequest);
        var output = new FindAllUsersUseCase.Output(id, UserName.of("John Doe"), UserEmail.of("john@example.com"), userTypeId, "Restaurant Owner");
        var viewModel = new UserViewModel(id.getValue(), "John Doe", "john@example.com", userTypeId.getValue(), "Restaurant Owner");
        var useCasePage = Page.of(List.of(output), 0, 10, 1L);
        var expectedPage = Page.of(List.of(viewModel), 0, 10, 1L);

        when(findAllUsersUseCase.execute(input)).thenReturn(useCasePage);
        when(userPresenter.present(output)).thenReturn(viewModel);

        var result = controller.findAll(input);

        assertThat(result).isEqualTo(expectedPage);
        verify(findAllUsersUseCase, times(1)).execute(input);
        verify(userPresenter, times(1)).present(output);
    }
}
