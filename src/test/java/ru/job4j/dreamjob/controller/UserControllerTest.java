package ru.job4j.dreamjob.controller;

import org.apache.catalina.connector.Connector;
import org.apache.catalina.connector.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class UserControllerTest {

    private UserService userService;

    private UserController userController;

    private HttpSession session;

    @BeforeEach
    public void initServices() {
        userService = mock(UserService.class);
        userController = new UserController(userService);
        session = mock(HttpSession.class);
    }

    @Test
    public void whenGetRegistrationPageThenGetSameDataAndPage() {
        var user = new User("email1", "name1", "password1");
        var stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        when(session.getAttribute(stringArgumentCaptor.capture())).thenReturn(user);
        var model = new ConcurrentModel();

        var view = userController.getRegistrationPage(model, session);
        var actualName = stringArgumentCaptor.getValue();
        var actualUser = model.getAttribute("user");

        assertThat(actualName).isEqualTo("user");
        assertThat(actualUser).isEqualTo(user);
        assertThat(view).isEqualTo("users/register");
    }

    @Test
    public void whenGetRegistrationPageUnsuccessfulThenGetSameDataAndPage() {
        var user = new User();
        user.setName("Гость");
        when(session.getAttribute(any())).thenReturn(null);
        var model = new ConcurrentModel();

        var view = userController.getRegistrationPage(model, session);
        var actualUser = model.getAttribute("user");

        assertThat(actualUser).isEqualTo(user);
        assertThat(view).isEqualTo("users/register");
    }

    @Test
    public void whenRegisterThenGetSameDataAndPage() {
        var user = new User("email1", "name1", "password1");
        var userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        when(userService.save(userArgumentCaptor.capture())).thenReturn(Optional.of(user));
        var stringArgumentCaptorSession = ArgumentCaptor.forClass(String.class);
        var userArgumentCaptorSession = ArgumentCaptor.forClass(User.class);
        doNothing().when(session).setAttribute(stringArgumentCaptorSession.capture(), userArgumentCaptorSession.capture());
        var model = new ConcurrentModel();

        var view = userController.register(model, user, session);
        var actualUserArgument = userArgumentCaptor.getValue();
        var actualAttributeName = stringArgumentCaptorSession.getValue();
        var actualUserSession = userArgumentCaptorSession.getValue();

        assertThat(actualUserArgument).isEqualTo(user);
        assertThat(actualAttributeName).isEqualTo("user");
        assertThat(actualUserSession).isEqualTo(user);
        assertThat(view).isEqualTo("redirect:/vacancies");
    }

    @Test
    public void whenRegisterUnsuccessfulThenGetSameDataAndPage() {
        when(userService.save(any())).thenReturn(Optional.empty());
        var model = new ConcurrentModel();

        var view = userController.register(model, new User(), session);
        var actualMessage = model.getAttribute("message");

        assertThat(actualMessage).isEqualTo("Пользователь с такой почтой уже существует");
        assertThat(view).isEqualTo("errors/404");
    }

    @Test
    public void whenGetLoginPageThenGetPage() {
        var view = userController.getLoginPage();

        assertThat(view).isEqualTo("users/login");
    }

    @Test
    public void whenLoginUserThenGetSameDataAndPage() {
        var user = new User("email1", "name1", "password1");
        var stringEmailArgumentCaptor = ArgumentCaptor.forClass(String.class);
        var stringPasswordArgumentCaptor = ArgumentCaptor.forClass(String.class);
        when(userService
                .findByEmailAndPassword(stringEmailArgumentCaptor.capture(), stringPasswordArgumentCaptor.capture()))
                .thenReturn(Optional.of(user));
        var request = mock(HttpServletRequest.class);
        when(request.getSession()).thenReturn(session);
        var stringArgumentCaptorSession = ArgumentCaptor.forClass(String.class);
        var userArgumentCaptorSession = ArgumentCaptor.forClass(User.class);
        doNothing().when(session).setAttribute(stringArgumentCaptorSession.capture(), userArgumentCaptorSession.capture());
        var model = new ConcurrentModel();

        var view = userController.loginUser(user, model, request);
        var actualEmail = stringEmailArgumentCaptor.getValue();
        var actualPassword = stringPasswordArgumentCaptor.getValue();
        var actualAttributeName = stringArgumentCaptorSession.getValue();
        var actualUserArgument = userArgumentCaptorSession.getValue();

        assertThat(actualEmail).isEqualTo(user.getEmail());
        assertThat(actualPassword).isEqualTo(user.getPassword());
        assertThat(actualAttributeName).isEqualTo("user");
        assertThat(actualUserArgument).isEqualTo(user);
        assertThat(view).isEqualTo("redirect:/vacancies");
    }

    @Test
    public void whenLoginUserUnsuccessfulThenGetSameDataAndPage() {
        when(userService.findByEmailAndPassword(any(), any())).thenReturn(Optional.empty());
        var model = new ConcurrentModel();

        var view = userController.loginUser(new User(), model, new Request(new Connector()));
        var actualError = model.getAttribute("error");

        assertThat(actualError).isEqualTo("Почта или пароль введены неверно");
        assertThat(view).isEqualTo("users/login");
    }

    @Test
    public void whenLogoutThenGetSameDataAndPage() {
        var view = userController.logout(session);

        assertDoesNotThrow(() -> verify(session).invalidate());
        assertThat(view).isEqualTo("redirect:/users/login");
    }
}