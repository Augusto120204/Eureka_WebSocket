package ec.edu.monster.climov_eurekabank_soapjava_g02.model.dto;

import java.util.Map;
import java.util.Set;

/**
 * DTO para mensajes de estado enviados por WebSocket.
 * Sincronizado con el DTO del servidor Spring Boot.
 */
public class EstadoMensaje {
    
    public enum TipoMensaje {
        ESTADO_COMPLETO,      
        CAJERO_OCUPADO,       
        CAJERO_LIBERADO,      
        OPERACION_BLOQUEADA,  
        OPERACION_LIBERADA    
    }
    
    private TipoMensaje tipo;
    private Set<String> cajerosOcupados;
    private Map<String, Set<String>> operacionesBloqueadas;
    private String cajeroId;
    private String cajeroNombre;
    private String cuenta;
    private String operacion;
    private String sessionId;
    
    public EstadoMensaje() {}
    
    // Getters y Setters
    public TipoMensaje getTipo() { return tipo; }
    public void setTipo(TipoMensaje tipo) { this.tipo = tipo; }
    
    public Set<String> getCajerosOcupados() { return cajerosOcupados; }
    public void setCajerosOcupados(Set<String> cajerosOcupados) { this.cajerosOcupados = cajerosOcupados; }
    
    public Map<String, Set<String>> getOperacionesBloqueadas() { return operacionesBloqueadas; }
    public void setOperacionesBloqueadas(Map<String, Set<String>> operacionesBloqueadas) { this.operacionesBloqueadas = operacionesBloqueadas; }
    
    public String getCajeroId() { return cajeroId; }
    public void setCajeroId(String cajeroId) { this.cajeroId = cajeroId; }
    
    public String getCajeroNombre() { return cajeroNombre; }
    public void setCajeroNombre(String cajeroNombre) { this.cajeroNombre = cajeroNombre; }
    
    public String getCuenta() { return cuenta; }
    public void setCuenta(String cuenta) { this.cuenta = cuenta; }
    
    public String getOperacion() { return operacion; }
    public void setOperacion(String operacion) { this.operacion = operacion; }
    
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
}
