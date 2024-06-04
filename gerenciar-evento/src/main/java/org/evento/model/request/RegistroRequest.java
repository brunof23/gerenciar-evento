package org.evento.model.request;

import org.evento.model.UsuarioRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegistroRequest {
    private String username;
    private String password;
    private  UsuarioRole role;
}
