package org.evento.model;

import org.springframework.security.core.GrantedAuthority;

public enum UsuarioRole implements GrantedAuthority {
    ADMIN,
    USER;

    @Override
    public String getAuthority() {
        return this.name();
    }
}
