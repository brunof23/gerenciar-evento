package org.evento.controller;

import org.evento.model.request.RegistroRequest;
import org.evento.model.UsuarioRole;
import org.evento.model.response.UsuarioResponse;
import org.evento.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsuarioControllerTest {

    @InjectMocks
    private UsuarioController usuarioController;

    @Mock
    private UsuarioService usuarioService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testRegisterUser() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        RegistroRequest registerRequest = RegistroRequest.builder()
                .username("user")
                .password("password")
                .role(UsuarioRole.ADMIN)
                .build();
        UsuarioResponse userResponse = UsuarioResponse.builder()
                .id("1")
                .username("user")
                .role("USER")
                .build();

        when(usuarioService.saveUser(any(RegistroRequest.class))).thenReturn(userResponse);

        ResponseEntity<Void> response = usuarioController.login(registerRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        URI location = response.getHeaders().getLocation();
        assertNotNull(location);
        assertTrue(location.toString().contains(userResponse.getId()));

        verify(usuarioService, times(1)).saveUser(any(RegistroRequest.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void testGetUserById() {
        UsuarioResponse userResponse = UsuarioResponse.builder().id("1").username("user").role("USER").build();

        when(usuarioService.findById("1")).thenReturn(Optional.of(userResponse));

        ResponseEntity<UsuarioResponse> response = usuarioController.getEventById("1");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(userResponse.getId(), response.getBody().getId());

        verify(usuarioService, times(1)).findById("1");
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void testGetUserByIdNotFound() {
        when(usuarioService.findById("1")).thenReturn(Optional.empty());

        ResponseEntity<UsuarioResponse> response = usuarioController.getEventById("1");

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        verify(usuarioService, times(1)).findById("1");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetUsers() {
        UsuarioResponse user1 = UsuarioResponse.builder().id("1").username("user1").role("USER").build();
        UsuarioResponse user2 = UsuarioResponse.builder().id("2").username("user2").role("USER").build();
        List<UsuarioResponse> userList = Arrays.asList(user1, user2);

        when(usuarioService.findAll()).thenReturn(userList);

        ResponseEntity<List<UsuarioResponse>> response = usuarioController.getUsers();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());

        verify(usuarioService, times(1)).findAll();
    }
}