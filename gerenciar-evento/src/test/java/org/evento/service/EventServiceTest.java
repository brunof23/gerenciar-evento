package org.evento.service;

import org.evento.config.exceptions.EventFullException;
import org.evento.config.exceptions.EventNotFoundException;
import org.evento.model.Evento;
import org.evento.model.Usuario;
import org.evento.model.UsuarioRole;
import org.evento.model.request.EventoRequest;
import org.evento.model.response.EventoResponse;
import org.evento.repository.EventoRepository;
import org.evento.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventServiceTest {

    @InjectMocks
    private EventoService eventoService;

    @Mock
    private EventoRepository eventoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveEvent() {
        EventoRequest eventRequest = EventoRequest.builder()
                .nome("Conference")
                .data(LocalDate.now())
                .localizacao("Conference Hall")
                .maxParticipantes(100)
                .build();

        Evento event = new Evento();
        event.setId("1");
        event.setNome(eventRequest.getNome());
        event.setData(eventRequest.getData());
        event.setLocalizacao(eventRequest.getLocalizacao());
        event.setMaxParticipantes(eventRequest.getMaxParticipantes());

        when(eventoRepository.save(any(Evento.class))).thenReturn(event);

        EventoResponse eventResponse = eventoService.saveEvento(eventRequest);

        assertNotNull(eventResponse);
        assertEquals(event.getId(), eventResponse.getId());
        assertEquals(event.getNome(), eventResponse.getNome());
    }

    @Test
    void testFindAll() {
        Evento event = new Evento();
        event.setId("1");
        when(eventoRepository.findAll()).thenReturn(Collections.singletonList(event));

        var events = eventoService.findAll();

        assertFalse(events.isEmpty());
        assertEquals(1, events.size());
    }

    @Test
    void testFindById() {
        String eventId = "1";
        Evento event = new Evento();
        event.setId(eventId);

        when(eventoRepository.findById(eventId)).thenReturn(Optional.of(event));

        Optional<EventoResponse> eventResponse = eventoService.findById(eventId);

        assertTrue(eventResponse.isPresent());
        assertEquals(eventId, eventResponse.get().getId());
    }

    @Test
    void testFindByIdNotFound() {
        String eventId = "1";
        when(eventoRepository.findById(eventId)).thenReturn(Optional.empty());

        assertThrows(EventNotFoundException.class, () -> eventoService.findById(eventId));
    }

    @Test
    void testUpdateEvent() {
        String eventId = "1";
        EventoRequest eventRequest = EventoRequest.builder()
                .nome("Updated Conference")
                .data(LocalDate.now())
                .localizacao("Main Hall")
                .maxParticipantes(150)
                .build();

        Evento event = new Evento();
        event.setId(eventId);
        when(eventoRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(eventoRepository.save(any(Evento.class))).thenReturn(event);

        Optional<EventoResponse> eventResponse = eventoService.updateEvent(eventId, eventRequest);

        assertTrue(eventResponse.isPresent());
        assertEquals(eventRequest.getNome(), eventResponse.get().getNome());
    }

    @Test
    void testUpdateEventNotFound() {
        String eventId = "1";
        EventoRequest eventRequest = EventoRequest.builder()
                .nome("Updated Conference")
                .data(LocalDate.now())
                .localizacao("Main Hall")
                .maxParticipantes(150)
                .build();

        when(eventoRepository.findById(eventId)).thenReturn(Optional.empty());

        assertThrows(EventNotFoundException.class, () -> eventoService.updateEvent(eventId, eventRequest));
    }

    @Test
    void testDeleteById() {
        String eventId = "1";
        when(eventoRepository.existsById(eventId)).thenReturn(true);
        doNothing().when(eventoRepository).deleteById(eventId);

        eventoService.deleteById(eventId);

        verify(eventoRepository, times(1)).deleteById(eventId);
    }

    @Test
    void testDeleteByIdNotFound() {
        String eventId = "1";
        when(eventoRepository.existsById(eventId)).thenReturn(false);

        assertThrows(EventNotFoundException.class, () -> eventoService.deleteById(eventId));
    }

    @Test
    void testRegisterForEvent() {
        String eventId = "1";
        String userId = "1";
        String username = "user";

        Usuario user = new Usuario();
        user.setPassword("password");
        user.setUsername(username);
        user.setId(userId);
        user.setRole(UsuarioRole.ADMIN);

        Evento event = new Evento();
        event.setNome("Conference");
        event.setId(eventId);
        event.setMaxParticipantes(10);
        event.setParticipantes(new ArrayList<>(Collections.singletonList(user)));

        when(eventoRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(usuarioRepository.findById(userId)).thenReturn(Optional.of(user));
        when(eventoRepository.save(any(Evento.class))).thenReturn(event);

        eventoService.registerForEvent(eventId, userId);

        verify(eventoRepository, times(1)).save(event);
    }

    @Test
    void testRegisterForEventFull() {
        String eventId = "1";
        String userId = "1";
        String username = "user";

        Usuario user = new Usuario();
        user.setPassword("password");
        user.setUsername(username);
        user.setId(userId);
        user.setRole(UsuarioRole.ADMIN);

        Evento event = new Evento();
        event.setId(eventId);
        event.setMaxParticipantes(1);
        event.setParticipantes(Collections.singletonList(user));

        when(eventoRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(usuarioRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(EventFullException.class, () -> eventoService.registerForEvent(eventId, userId));
    }

    @Test
    void testRegisterForEventNotFound() {
        String eventId = "1";
        String userId = "1";

        when(eventoRepository.findById(eventId)).thenReturn(Optional.empty());
        when(usuarioRepository.findById(userId)).thenReturn(Optional.empty());


        assertThrows(EventNotFoundException.class, () -> eventoService.registerForEvent(eventId, userId));
    }

    @Test
    void testUnregisterFromEvent() {
        String eventId = "1";
        String userId = "1";
        Evento event = new Evento();
        event.setId(eventId);

        Usuario user = new Usuario();
        user.setId(userId);

        List<Usuario> users = new ArrayList<>();
        users.add(user);
        event.setParticipantes(users);

        when(eventoRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(usuarioRepository.findById(userId)).thenReturn(Optional.of(user));

        eventoService.unregisterFromEvent(eventId, userId);

        verify(eventoRepository, times(1)).save(event);
    }


    @Test
    void testUnregisterFromEvent_UserNotFound() {
        String eventId = "1";
        String userId = "1";
        Evento event = new Evento();
        event.setId(eventId);

        when(eventoRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(usuarioRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EventNotFoundException.class, () -> eventoService.unregisterFromEvent(eventId, userId));
    }


}