package org.evento.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "eventos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private LocalDate data;
    private String nome;
    private String localizacao;

    @ManyToMany
    private List<Usuario> participantes;

    private int maxParticipantes;
    public boolean isFull() {
        return participantes.size() >= maxParticipantes;
    }
}