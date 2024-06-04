package org.evento.model.response;

import org.evento.model.UsuarioDTO;
import jakarta.persistence.ElementCollection;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class EventoResponse {
    private String id;
    private String nome;
    private String localizacao;
    private LocalDate data;
    private int maxParticipantes;
    @ElementCollection
    private List<UsuarioDTO> participantes;

}
