package org.evento.controller;

import org.evento.model.request.EventoRequest;
import org.evento.model.response.EventoResponse;
import org.evento.service.EventoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.URI;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventoControllerTest {

    @InjectMocks
    private EventoController eventoController;

    @Mock
    private EventoService eventoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateEvent() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        EventoRequest eventRequest = EventoRequest.builder()
                .nome("Conference")
                .data(LocalDate.now())
                .localizacao("Conference Hall")
                .maxParticipantes(100)
                .build();
        EventoResponse eventResponse = EventoResponse.builder()
                .id("1")
                .nome("Conference")
                .data(LocalDate.now())
                .localizacao("Conference Hall")
                .maxParticipantes(100)
                .build();

        when(eventoService.saveEvento(any(EventoRequest.class))).thenReturn(eventResponse);

        ResponseEntity<EventoResponse> response = eventoController.createEvent(eventRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(eventResponse.getId(), response.getBody().getId());

        URI location = response.getHeaders().getLocation();
        assertNotNull(location);
        assertTrue(location.toString().contains(eventResponse.getId()));

        verify(eventoService, times(1)).saveEvento(any(EventoRequest.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void testGetAllEvents() {
        EventoResponse event1 = EventoResponse.builder().id("1").nome("Conference").build();
        EventoResponse event2 = EventoResponse.builder().id("2").nome("Workshop").build();
        List<EventoResponse> eventList = Arrays.asList(event1, event2);

        when(eventoService.findAll()).thenReturn(eventList);

        ResponseEntity<List<EventoResponse>> response = eventoController.getAllEvents();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());

        verify(eventoService, times(1)).findAll();
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void testGetEventById() {
        EventoResponse eventResponse = EventoResponse.builder().id("1").nome("Conference").build();

        when(eventoService.findById("1")).thenReturn(Optional.of(eventResponse));

        ResponseEntity<EventoResponse> response = eventoController.getEventById("1");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(eventResponse.getId(), response.getBody().getId());

        verify(eventoService, times(1)).findById("1");
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void testGetEventByIdNotFound() {
        when(eventoService.findById("1")).thenReturn(Optional.empty());

        ResponseEntity<EventoResponse> response = eventoController.getEventById("1");

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        verify(eventoService, times(1)).findById("1");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateEvent() {
        EventoRequest eventRequest = EventoRequest.builder()
                .nome("Updated Conference")
                .data(LocalDate.now())
                .localizacao("Main Hall")
                .maxParticipantes(150)
                .build();
        EventoResponse eventResponse = EventoResponse.builder()
                .id("1")
                .nome("Updated Conference")
                .data(LocalDate.now())
                .localizacao("Main Hall")
                .maxParticipantes(150)
                .build();

        when(eventoService.updateEvent(eq("1"), any(EventoRequest.class))).thenReturn(Optional.of(eventResponse));

        ResponseEntity<EventoResponse> response = eventoController.updateEvent("1", eventRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(eventResponse.getId(), response.getBody().getId());

        verify(eventoService, times(1)).updateEvent(eq("1"), any(EventoRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateEventNotFound() {
        EventoRequest eventRequest = EventoRequest.builder()
                .nome("Updated Conference")
                .data(LocalDate.now())
                .localizacao("Main Hall")
                .maxParticipantes(150)
                .build();

        when(eventoService.updateEvent(eq("1"), any(EventoRequest.class))).thenReturn(Optional.empty());

        ResponseEntity<EventoResponse> response = eventoController.updateEvent("1", eventRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        verify(eventoService, times(1)).updateEvent(eq("1"), any(EventoRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteEvent() {
        doNothing().when(eventoService).deleteById("1");

        ResponseEntity<Void> response = eventoController.deleteEvent("1");

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        verify(eventoService, times(1)).deleteById("1");
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void testRegisterForEvent() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        String eventId = "1";
        String userId = "1";

        doNothing().when(eventoService).registerForEvent(eventId, userId);

        ResponseEntity<Void> response = eventoController.registerForEvent(eventId, userId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(eventoService, times(1)).registerForEvent(eventId, userId);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testUnregisterFromEvent() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        String eventId = "1";
        String userId = "1";

        doNothing().when(eventoService).unregisterFromEvent(eventId, userId);

        ResponseEntity<Void> response = eventoController.unregisterFromEvent(eventId, userId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(eventoService, times(1)).unregisterFromEvent(eventId, userId);
    }
}