package org.evento.controller;

import org.evento.model.request.EventoRequest;
import org.evento.model.response.EventoResponse;
import org.evento.service.EventoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/events")
public class EventoController {

    @Autowired
    private EventoService eventoService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EventoResponse> createEvent(@RequestBody EventoRequest eventoRequest) {
        EventoResponse eventoSalvo = eventoService.saveEvento(eventoRequest);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(eventoSalvo.getId())
                .toUri();

        return ResponseEntity.created(location).body(eventoSalvo);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<EventoResponse>> getAllEvents() {
        List<EventoResponse> events = eventoService.findAll();
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<EventoResponse> getEventById(@PathVariable String id) {
        return eventoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EventoResponse> updateEvent(@PathVariable String id, @RequestBody EventoRequest eventoRequest) {
        return eventoService.updateEvent(id, eventoRequest)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/register")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Void> registerForEvent(@PathVariable String id, @RequestParam String userId) {
        eventoService.registerForEvent(id, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEvent(@PathVariable String id) {
        eventoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/unregister")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> unregisterFromEvent(@PathVariable String id, @RequestParam String userId) {
        eventoService.unregisterFromEvent(id, userId);
        return ResponseEntity.ok().build();
    }
}
