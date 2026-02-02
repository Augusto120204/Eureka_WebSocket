package ec.edu.monster.dto;

import java.util.Map;
import java.util.Set;

/**
 * DTO para mensajes de estado enviados por WebSocket.
 * Contiene información sobre cajeros ocupados y operaciones bloqueadas.
 * 
 * @author Sistema EurekaBank
 */
public class EstadoMensaje {
    
    public enum TipoMensaje {
        ESTADO_COMPLETO,      // Estado inicial con toda la información
        CAJERO_OCUPADO,       // Un cajero fue ocupado
        CAJERO_LIBERADO,      // Un cajero fue liberado
        OPERACION_BLOQUEADA,  // Una operación en una cuenta fue bloqueada
        OPERACION_LIBERADA    // Una operación en una cuenta fue liberada
    }
    
    private TipoMensaje tipo;
    private Set<String> cajerosOcupados;                    // Set de códigos de cajeros ocupados
    private Map<String, Set<String>> operacionesBloqueadas; // cuenta -> set de operaciones bloqueadas
    
    // Para mensajes específicos
    private String cajeroId;
    private String cajeroNombre;
    private String cuenta;
    private String operacion;
    private String sessionId;
    
    public EstadoMensaje() {
    }
    
    public EstadoMensaje(TipoMensaje tipo) {
        this.tipo = tipo;
    }

    // Getters y Setters
    public TipoMensaje getTipo() {
        return tipo;
    }

    public void setTipo(TipoMensaje tipo) {
        this.tipo = tipo;
    }

    public Set<String> getCajerosOcupados() {
        return cajerosOcupados;
    }

    public void setCajerosOcupados(Set<String> cajerosOcupados) {
        this.cajerosOcupados = cajerosOcupados;
    }

    public Map<String, Set<String>> getOperacionesBloqueadas() {
        return operacionesBloqueadas;
    }

    public void setOperacionesBloqueadas(Map<String, Set<String>> operacionesBloqueadas) {
        this.operacionesBloqueadas = operacionesBloqueadas;
    }

    public String getCajeroId() {
        return cajeroId;
    }

    public void setCajeroId(String cajeroId) {
        this.cajeroId = cajeroId;
    }

    public String getCajeroNombre() {
        return cajeroNombre;
    }

    public void setCajeroNombre(String cajeroNombre) {
        this.cajeroNombre = cajeroNombre;
    }

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public String getOperacion() {
        return operacion;
    }

    public void setOperacion(String operacion) {
        this.operacion = operacion;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
