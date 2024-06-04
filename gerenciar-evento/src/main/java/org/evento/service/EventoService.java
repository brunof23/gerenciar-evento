package org.evento.service;

import org.evento.config.exceptions.EventFullException;
import org.evento.config.exceptions.EventNotFoundException;
import org.evento.model.Evento;
import org.evento.model.Usuario;
import org.evento.model.UsuarioDTO;
import org.evento.model.request.EventoRequest;
import org.evento.model.response.EventoResponse;
import org.evento.repository.EventoRepository;
import lombok.RequiredArgsConstructor;
import org.evento.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventoService {
    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private UsuarioRepository userRepository;

    public EventoResponse saveEvento(EventoRequest eventoCreateDTO) {
        Evento evento = new Evento();
        evento.setNome(eventoCreateDTO.getName());
        evento.setDate(eventoCreateDTO.getDate());
        evento.setLocation(eventoCreateDTO.getLocation());
        evento.setMaxParticipants(eventoCreateDTO.getMaxParticipants());
        Evento eventoSalvo = eventoRepository.save(evento);
        return convertToDTO(eventoSalvo);
    }

    public List<EventoResponse> findAll() {
        return eventoRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<EventoResponse> findById(String id) {
        return Optional.ofNullable(eventoRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new EventNotFoundException("Event not found with id: " + id)));
    }

    public Optional<EventoResponse> updateEvent(String id, EventoRequest eventUpdateDTO) {
        return Optional.ofNullable(eventoRepository.findById(id)
                .map(existingEvent -> {
                    existingEvent.setName(eventUpdateDTO.getName());
                    existingEvent.setDate(eventUpdateDTO.getDate());
                    existingEvent.setLocation(eventUpdateDTO.getLocation());
                    existingEvent.setMaxParticipants(eventUpdateDTO.getMaxParticipants());
                    Evento updatedEvent = eventoRepository.save(existingEvent);
                    return convertToDTO(updatedEvent);
                }).orElseThrow(() -> new EventNotFoundException("Event not found with id: " + id)));
    }

    public void deleteById(String id) {
        if (eventoRepository.existsById(id)) {
            eventoRepository.deleteById(id);
        } else {
            throw new EventNotFoundException("Event not found with id: " + id);
        }
    }

    private EventoResponse converterDTO(Evento event) {
        List<UsuarioDTO> participantes = Optional.ofNullable(event.getParticipants())
                .orElse(Collections.emptyList())
                .stream()
                .map(this::convertToUserDTO)
                .collect(Collectors.toList());

        return EventoResponse.builder()
                .id(event.getId())
                .name(event.getName())
                .date(event.getDate())
                .location(event.getLocation())
                .participants(participantes)
                .maxParticipants(event.getMaxParticipants())
                .build();
    }

    public void registerForEvent(String eventId, String userId) {
        Evento evento = eventoRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found with id: " + eventId));
        if (evento.isFull()) {
            throw new EventFullException("Event has reached maximum capacity of participants.");
        }

        Usuario usuario = userRepository.findById(userId)
                .orElseThrow(() -> new EventNotFoundException("User not found with id: " + userId));

        evento.getParticipants().add(usuario);
        eventoRepository.save(evento);
    }

    public void unregisterFromEvent(String eventId, String userId) {
        Evento evento = eventoRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found with id: " + eventId));

        Usuario user = userRepository.findById(userId)
                .orElseThrow(() -> new EventNotFoundException("User not found with id: " + userId));

        if (!evento.getParticipants().contains(user)) {
            throw new EventNotFoundException("User is not registered for the evento with id: " + eventId);
        }

        evento.getParticipants().remove(user);
        eventoRepository.save(evento);
    }

    private EventoResponse converterDTO(Evento event) {
        List<UsuarioDTO> participantes = Optional.ofNullable(event.getParticipants())
                .orElse(Collections.emptyList())
                .stream()
                .map(this::convertToUserDTO)
                .collect(Collectors.toList());

        return EventoResponse.builder()
                .id(event.getId())
                .name(event.getName())
                .date(event.getDate())
                .location(event.getLocation())
                .participants(participantes)
                .maxParticipants(event.getMaxParticipants())
                .build();
    }

    private UsuarioDTO convertToUserDTO(Usuario usuario) {
        return new UsuarioDTO(usuario.getId(), usuario.getUsername());
    }
}