package ec.edu.monster.config;

import ec.edu.monster.servicios.EstadoService;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

/**
 * Listener para eventos de conexión/desconexión WebSocket.
 * Limpia los bloqueos cuando un cliente se desconecta.
 * 
 * @author Sistema EurekaBank
 */
@Component
public class WebSocketEventListener {

    private final EstadoService estadoService;

    public WebSocketEventListener(EstadoService estadoService) {
        this.estadoService = estadoService;
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String wsSessionId = headerAccessor.getSessionId();
        System.out.println("Nueva conexión WebSocket: " + wsSessionId);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String wsSessionId = headerAccessor.getSessionId();
        
        System.out.println("Desconexión WebSocket: " + wsSessionId);
        
        // Limpiar todos los bloqueos asociados a esta sesión WebSocket
        // Buscamos por wsSessionId en lugar del sessionId del cliente
        estadoService.limpiarSesionWebSocket(wsSessionId);
    }
}
