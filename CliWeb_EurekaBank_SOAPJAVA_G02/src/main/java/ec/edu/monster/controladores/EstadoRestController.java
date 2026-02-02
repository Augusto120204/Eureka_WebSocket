package ec.edu.monster.controladores;

import ec.edu.monster.dto.EstadoMensaje;
import ec.edu.monster.servicios.EstadoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Controlador REST para manejar acciones de bloqueo/desbloqueo.
 * 
 * @author Sistema EurekaBank
 */
@RestController
@RequestMapping("/api/estado")
public class EstadoRestController {

    private final EstadoService estadoService;

    public EstadoRestController(EstadoService estadoService) {
        this.estadoService = estadoService;
    }

    /**
     * Obtiene el estado completo actual.
     */
    @GetMapping
    public ResponseEntity<EstadoMensaje> obtenerEstado() {
        return ResponseEntity.ok(estadoService.obtenerEstadoCompleto());
    }

    /**
     * Registra la asociación entre wsSessionId y clientSessionId.
     */
    @PostMapping("/registrar-sesion")
    public ResponseEntity<Map<String, Object>> registrarSesion(
            @RequestParam("wsSessionId") String wsSessionId,
            @RequestParam("clientSessionId") String clientSessionId) {
        
        Map<String, Object> response = new HashMap<>();
        
        estadoService.registrarSesion(wsSessionId, clientSessionId);
        
        response.put("exito", true);
        response.put("mensaje", "Sesión registrada exitosamente");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Intenta ocupar un cajero.
     */
    @PostMapping("/cajero/ocupar")
    public ResponseEntity<Map<String, Object>> ocuparCajero(
            @RequestParam("cajeroId") String cajeroId,
            @RequestParam("cajeroNombre") String cajeroNombre,
            @RequestParam("sessionId") String sessionId,
            HttpSession httpSession) {
        
        Map<String, Object> response = new HashMap<>();
        
        boolean exito = estadoService.ocuparCajero(cajeroId, cajeroNombre, sessionId);
        
        response.put("exito", exito);
        response.put("mensaje", exito ? "Cajero ocupado exitosamente" : "El cajero ya está ocupado por otro usuario");
        
        if (exito) {
            // Guardar en la sesión HTTP también
            httpSession.setAttribute("wsSessionId", sessionId);
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * Libera un cajero.
     */
    @PostMapping("/cajero/liberar")
    public ResponseEntity<Map<String, Object>> liberarCajero(
            @RequestParam("cajeroId") String cajeroId) {
        
        Map<String, Object> response = new HashMap<>();
        
        estadoService.liberarCajero(cajeroId);
        
        response.put("exito", true);
        response.put("mensaje", "Cajero liberado exitosamente");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Verifica si un cajero está ocupado.
     */
    @GetMapping("/cajero/{cajeroId}/ocupado")
    public ResponseEntity<Map<String, Object>> verificarCajeroOcupado(
            @PathVariable("cajeroId") String cajeroId) {
        
        Map<String, Object> response = new HashMap<>();
        response.put("ocupado", estadoService.estaCajeroOcupado(cajeroId));
        
        return ResponseEntity.ok(response);
    }

    /**
     * Intenta bloquear una operación para una cuenta.
     */
    @PostMapping("/operacion/bloquear")
    public ResponseEntity<Map<String, Object>> bloquearOperacion(
            @RequestParam("cuenta") String cuenta,
            @RequestParam("operacion") String operacion,
            @RequestParam("sessionId") String sessionId) {
        
        Map<String, Object> response = new HashMap<>();
        
        boolean exito = estadoService.bloquearOperacion(cuenta, operacion, sessionId);
        
        response.put("exito", exito);
        response.put("mensaje", exito ? "Operación bloqueada exitosamente" : "La operación ya está en uso por otro cajero");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Libera una operación para una cuenta.
     */
    @PostMapping("/operacion/liberar")
    public ResponseEntity<Map<String, Object>> liberarOperacion(
            @RequestParam("cuenta") String cuenta,
            @RequestParam("operacion") String operacion,
            @RequestParam("sessionId") String sessionId) {
        
        Map<String, Object> response = new HashMap<>();
        
        estadoService.liberarOperacion(cuenta, operacion, sessionId);
        
        response.put("exito", true);
        response.put("mensaje", "Operación liberada exitosamente");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Verifica si una operación está bloqueada para una cuenta.
     */
    @GetMapping("/operacion/{cuenta}/{operacion}/bloqueada")
    public ResponseEntity<Map<String, Object>> verificarOperacionBloqueada(
            @PathVariable("cuenta") String cuenta,
            @PathVariable("operacion") String operacion,
            @RequestParam(value = "sessionId", required = false) String sessionId) {
        
        Map<String, Object> response = new HashMap<>();
        
        if (sessionId != null) {
            response.put("bloqueada", estadoService.estaOperacionBloqueadaPorOtro(cuenta, operacion, sessionId));
        } else {
            response.put("bloqueada", estadoService.estaOperacionBloqueada(cuenta, operacion));
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene las operaciones bloqueadas para una cuenta.
     */
    @GetMapping("/cuenta/{cuenta}/operaciones-bloqueadas")
    public ResponseEntity<Set<String>> obtenerOperacionesBloqueadas(
            @PathVariable("cuenta") String cuenta) {
        return ResponseEntity.ok(estadoService.getOperacionesBloqueadas(cuenta));
    }

    /**
     * Obtiene los cajeros ocupados.
     */
    @GetMapping("/cajeros-ocupados")
    public ResponseEntity<Set<String>> obtenerCajerosOcupados() {
        return ResponseEntity.ok(estadoService.getCajerosOcupados());
    }
}
