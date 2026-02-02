package ec.edu.monster.websocket;

import ec.edu.monster.dto.EstadoMensaje;
import ec.edu.monster.servicios.EstadoService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.util.Map;

/**
 * Controlador WebSocket para manejar mensajes STOMP.
 * 
 * @author Sistema EurekaBank
 */
@Controller
public class EstadoController {

    private final EstadoService estadoService;

    public EstadoController(EstadoService estadoService) {
        this.estadoService = estadoService;
    }

    /**
     * Endpoint para solicitar el estado completo actual.
     * El cliente envía un mensaje a /app/estado y recibe el estado en /topic/estado
     */
    @MessageMapping("/estado")
    @SendTo("/topic/estado")
    public EstadoMensaje obtenerEstado(@Payload(required = false) Map<String, String> payload,
                                        SimpMessageHeaderAccessor headerAccessor) {
        // Registrar la asociación wsSessionId <-> clientSessionId si viene en el payload
        if (payload != null && payload.containsKey("clientSessionId")) {
            String wsSessionId = headerAccessor.getSessionId();
            String clientSessionId = payload.get("clientSessionId");
            estadoService.registrarSesion(wsSessionId, clientSessionId);
        }
        return estadoService.obtenerEstadoCompleto();
    }

    /**
     * Endpoint para solicitar estado privado (solo para el usuario que solicita).
     */
    @MessageMapping("/mi-estado")
    @SendToUser("/queue/estado")
    public EstadoMensaje obtenerMiEstado() {
        return estadoService.obtenerEstadoCompleto();
    }
    
    /**
     * Endpoint para registrar la sesión del cliente.
     */
    @MessageMapping("/registrar")
    public void registrarSesion(@Payload Map<String, String> payload,
                                SimpMessageHeaderAccessor headerAccessor) {
        String wsSessionId = headerAccessor.getSessionId();
        String clientSessionId = payload.get("clientSessionId");
        if (clientSessionId != null) {
            estadoService.registrarSesion(wsSessionId, clientSessionId);
        }
    }
}
