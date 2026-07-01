package br.com.postechfiap.toloni.restauranthub.adapters.controllers;

import br.com.postechfiap.toloni.restauranthub.adapters.presenters.usertype.UserTypePresenter;
import br.com.postechfiap.toloni.restauranthub.adapters.presenters.usertype.UserTypeViewModel;
import br.com.postechfiap.toloni.restauranthub.application.pagination.Page;
import br.com.postechfiap.toloni.restauranthub.application.pagination.PageRequest;
import br.com.postechfiap.toloni.restauranthub.application.usecases.usertype.*;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.UserRole;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.valueobject.UserTypeDescription;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.valueobject.UserTypeId;
import br.com.postechfiap.toloni.restauranthub.domain.usertype.valueobject.UserTypeName;
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
class UserTypeControllerTest {

    @Mock
    private CreateUserTypeUseCase createUserTypeUseCase;

    @Mock
    private UpdateUserTypeUseCase updateUserTypeUseCase;

    @Mock
    private DeleteUserTypeUseCase deleteUserTypeUseCase;

    @Mock
    private FindUserTypeByIdUseCase findUserTypeByIdUseCase;

    @Mock
    private FindAllUserTypesUseCase findAllUserTypesUseCase;

    @Mock
    private UserTypePresenter userTypePresenter;

    @InjectMocks
    private UserTypeController controller;

    private UserTypeId id;

    @BeforeEach
    void setUp() {
        id = UserTypeId.generate();
    }

    // -------------------------------------------------------------------------
    // create
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should delegate create to CreateUserTypeUseCase and present result")
    void shouldDelegateCreateToCreateUserTypeUseCase() {
        var input = new CreateUserTypeUseCase.Input("Restaurant Owner", "Owns and manages a restaurant", UserRole.RESTAURANT_OWNER);
        var output = new CreateUserTypeUseCase.Output(id, UserTypeName.of("Restaurant Owner"), UserTypeDescription.of("Owns and manages a restaurant"), UserRole.RESTAURANT_OWNER);
        var viewModel = new UserTypeViewModel(id.getValue(), "Restaurant Owner", "Owns and manages a restaurant", UserRole.RESTAURANT_OWNER);

        when(createUserTypeUseCase.execute(input)).thenReturn(output);
        when(userTypePresenter.present(output)).thenReturn(viewModel);

        var result = controller.create(input);

        assertThat(result).isEqualTo(viewModel);
        verify(createUserTypeUseCase, times(1)).execute(input);
        verify(userTypePresenter, times(1)).present(output);
    }

    // -------------------------------------------------------------------------
    // update
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should delegate update to UpdateUserTypeUseCase and present result")
    void shouldDelegateUpdateToUpdateUserTypeUseCase() {
        var input = new UpdateUserTypeUseCase.Input(id, "New Name", null, null);
        var output = new UpdateUserTypeUseCase.Output(id, UserTypeName.of("New Name"), UserTypeDescription.of("Owns and manages a restaurant"), UserRole.RESTAURANT_OWNER);
        var viewModel = new UserTypeViewModel(id.getValue(), "New Name", "Owns and manages a restaurant", UserRole.RESTAURANT_OWNER);

        when(updateUserTypeUseCase.execute(input)).thenReturn(output);
        when(userTypePresenter.present(output)).thenReturn(viewModel);

        var result = controller.update(input);

        assertThat(result).isEqualTo(viewModel);
        verify(updateUserTypeUseCase, times(1)).execute(input);
        verify(userTypePresenter, times(1)).present(output);
    }

    // -------------------------------------------------------------------------
    // delete
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should delegate delete to DeleteUserTypeUseCase")
    void shouldDelegateDeleteToDeleteUserTypeUseCase() {
        var input = new DeleteUserTypeUseCase.Input(id);

        doNothing().when(deleteUserTypeUseCase).execute(input);

        controller.delete(input);

        verify(deleteUserTypeUseCase, times(1)).execute(input);
    }

    // -------------------------------------------------------------------------
    // findById
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should delegate findById to FindUserTypeByIdUseCase and present result")
    void shouldDelegateFindByIdToFindUserTypeByIdUseCase() {
        var input = new FindUserTypeByIdUseCase.Input(id);
        var output = new FindUserTypeByIdUseCase.Output(id, UserTypeName.of("Restaurant Owner"), UserTypeDescription.of("Owns and manages a restaurant"), UserRole.RESTAURANT_OWNER);
        var viewModel = new UserTypeViewModel(id.getValue(), "Restaurant Owner", "Owns and manages a restaurant", UserRole.RESTAURANT_OWNER);

        when(findUserTypeByIdUseCase.execute(input)).thenReturn(output);
        when(userTypePresenter.present(output)).thenReturn(viewModel);

        var result = controller.findById(input);

        assertThat(result).isEqualTo(viewModel);
        verify(findUserTypeByIdUseCase, times(1)).execute(input);
        verify(userTypePresenter, times(1)).present(output);
    }

    // -------------------------------------------------------------------------
    // findAll
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Should delegate findAll to FindAllUserTypesUseCase and present results")
    void shouldDelegateFindAllToFindAllUserTypesUseCase() {
        var pageRequest = PageRequest.of(0, 10);
        var input = new FindAllUserTypesUseCase.Input(pageRequest);
        var output = new FindAllUserTypesUseCase.Output(id, UserTypeName.of("Restaurant Owner"), UserTypeDescription.of("Owns and manages a restaurant"), UserRole.RESTAURANT_OWNER);
        var viewModel = new UserTypeViewModel(id.getValue(), "Restaurant Owner", "Owns and manages a restaurant", UserRole.RESTAURANT_OWNER);
        var useCasePage = Page.of(List.of(output), 0, 10, 1L);
        var expectedPage = Page.of(List.of(viewModel), 0, 10, 1L);

        when(findAllUserTypesUseCase.execute(input)).thenReturn(useCasePage);
        when(userTypePresenter.present(output)).thenReturn(viewModel);

        var result = controller.findAll(input);

        assertThat(result).isEqualTo(expectedPage);
        verify(findAllUserTypesUseCase, times(1)).execute(input);
        verify(userTypePresenter, times(1)).present(output);
    }
}
