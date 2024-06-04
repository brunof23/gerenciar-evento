package org.evento.service;

import org.evento.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.evento.model.UsuarioRole;
import org.evento.model.request.RegistroRequest;
import org.evento.model.response.UsuarioResponse;
import org.evento.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UsuarioResponse saveUser(RegistroRequest registroRequest) {
        Usuario usuario = new Usuario(
                registroRequest.getUsername(),
                passwordEncoder.encode(registroRequest.getPassword()),
                UsuarioRole.valueOf(String.valueOf(registroRequest.getRole()))
        );
        Usuario salvarUsuario = usuarioRepository.save(usuario);
        return convertToDTO(salvarUsuario);

    }

    public Optional<UsuarioResponse> findById(String id) {
        return usuarioRepository.findById(id)
                .map(this::convertToDTO);
    }

    public List<UsuarioResponse> findAll() {
        return usuarioRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private UsuarioResponse convertToDTO(Usuario usuario) {
        return UsuarioResponse.builder()
                .id(usuario.getId())
                .username(usuario.getUsername())
                .role(usuario.getRole().name())
                .build();
    }
}
