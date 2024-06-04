package org.evento.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UsuarioResponse {
    private String id;
    private String username;
    private String role;
}
