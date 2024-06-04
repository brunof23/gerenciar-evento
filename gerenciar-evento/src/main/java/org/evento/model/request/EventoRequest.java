package org.evento.model.request;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class EventoRequest {
    private String nome;
    private String localizacao;
    private LocalDate data;
    private int maxParticipantes;
}
