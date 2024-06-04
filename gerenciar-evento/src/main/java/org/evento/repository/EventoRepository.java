package org.evento.repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EventoRepository extends JpaRepository<org.evento.model.Evento, String> {
}
