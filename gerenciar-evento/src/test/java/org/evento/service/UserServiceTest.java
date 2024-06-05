package org.evento.service;

import org.evento.model.Usuario;
import org.evento.model.UsuarioRole;
import org.evento.model.request.RegistroRequest;
import org.evento.model.response.UsuarioResponse;
import org.evento.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @InjectMocks
    private UsuarioService usuarioService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveUser() {
        RegistroRequest registerRequest = RegistroRequest.builder()
                .username("user")
                .password("password")
                .role(UsuarioRole.ADMIN)
                .build();

        Usuario user = new Usuario(registerRequest.getUsername(), passwordEncoder.encode(registerRequest.getPassword()), UsuarioRole.USER);
        user.setId("1");

        when(usuarioRepository.save(any(Usuario.class))).thenReturn(user);
        when(passwordEncoder.encode(any(CharSequence.class))).thenReturn("encodedPassword");

        UsuarioResponse userResponse = usuarioService.saveUser(registerRequest);

        assertNotNull(userResponse);
        assertEquals(user.getId(), userResponse.getId());
        assertEquals(user.getUsername(), userResponse.getUsername());
    }

    @Test
    void testFindById() {
        String userId = "1";
        Usuario user = new Usuario();
        user.setId(userId);
        user.setUsername("user");
        user.setRole(UsuarioRole.USER);

        when(usuarioRepository.findById(userId)).thenReturn(Optional.of(user));

        Optional<UsuarioResponse> userResponse = usuarioService.findById(userId);

        assertTrue(userResponse.isPresent());
        assertEquals(userId, userResponse.get().getId());
    }

    @Test
    void testFindAll() {
        Usuario user = new Usuario();
        user.setId("1");
        user.setPassword("password");
        user.setRole(UsuarioRole.USER);
        user.setUsername("user");
        when(usuarioRepository.findAll()).thenReturn(Collections.singletonList(user));

        var users = usuarioService.findAll();

        assertFalse(users.isEmpty());
        assertEquals(1, users.size());
    }
}