package ec.edu.monster.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configuración de WebSocket con STOMP para sincronización en tiempo real.
 * 
 * @author Sistema EurekaBank
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Habilitar un broker simple en memoria para enviar mensajes a clientes
        // /topic: para mensajes de broadcast (todos los clientes)
        // /queue: para mensajes privados (un cliente específico)
        config.enableSimpleBroker("/topic", "/queue");
        
        // Prefijo para mensajes que van al servidor (métodos @MessageMapping)
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint para conexión WebSocket
        registry.addEndpoint("/ws-eureka")
                .setAllowedOriginPatterns("*");
    }
}
