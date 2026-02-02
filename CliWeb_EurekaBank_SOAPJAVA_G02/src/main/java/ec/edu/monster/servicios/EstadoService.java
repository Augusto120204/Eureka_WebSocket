package ec.edu.monster.servicios;

import ec.edu.monster.dto.EstadoMensaje;
import ec.edu.monster.dto.EstadoMensaje.TipoMensaje;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class EstadoService {

    private final SimpMessagingTemplate messagingTemplate;
    
    // Delay en milisegundos antes de limpiar bloqueos cuando contador llega a 0
    // Esto da tiempo para que la nueva página se conecte durante la navegación
    private static final long CLEANUP_DELAY_MS = 3000;
    
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    
    private final Map<String, String> cajerosOcupados = new ConcurrentHashMap<>();
    private final Map<String, String> cajeroNombres = new ConcurrentHashMap<>();
    private final Map<String, Map<String, String>> operacionesBloqueadas = new ConcurrentHashMap<>();
    private final Map<String, String> sessionToCajero = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> sessionToBloqueos = new ConcurrentHashMap<>();
    private final Map<String, String> wsToClientSession = new ConcurrentHashMap<>();
    private final Map<String, AtomicInteger> clientConnectionCount = new ConcurrentHashMap<>();
    private final Map<String, ScheduledFuture<?>> pendingCleanups = new ConcurrentHashMap<>();

    public EstadoService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public synchronized void registrarSesion(String wsSessionId, String clientSessionId) {
        String oldClientId = wsToClientSession.get(wsSessionId);
        if (oldClientId != null && oldClientId.equals(clientSessionId)) {
            return;
        }
        
        // Cancelar cualquier limpieza pendiente para este cliente
        ScheduledFuture<?> pendingCleanup = pendingCleanups.remove(clientSessionId);
        if (pendingCleanup != null) {
            pendingCleanup.cancel(false);
            System.out.println("Limpieza cancelada para: " + clientSessionId + " (nueva conexion)");
        }
        
        wsToClientSession.put(wsSessionId, clientSessionId);
        clientConnectionCount.computeIfAbsent(clientSessionId, k -> new AtomicInteger(0)).incrementAndGet();
        int count = clientConnectionCount.get(clientSessionId).get();
        System.out.println("Sesion registrada: WS=" + wsSessionId + " -> Client=" + clientSessionId + " (conexiones: " + count + ")");
    }

    public synchronized boolean ocuparCajero(String cajeroId, String cajeroNombre, String clientSessionId) {
        if (cajerosOcupados.containsKey(cajeroId)) {
            String owner = cajerosOcupados.get(cajeroId);
            if (clientSessionId.equals(owner)) {
                return true;
            }
            return false;
        }
        cajerosOcupados.put(cajeroId, clientSessionId);
        cajeroNombres.put(cajeroId, cajeroNombre);
        sessionToCajero.put(clientSessionId, cajeroId);
        
        EstadoMensaje mensaje = new EstadoMensaje(TipoMensaje.CAJERO_OCUPADO);
        mensaje.setCajeroId(cajeroId);
        mensaje.setCajeroNombre(cajeroNombre);
        mensaje.setSessionId(clientSessionId);
        messagingTemplate.convertAndSend("/topic/estado", mensaje);
        System.out.println("Cajero ocupado: " + cajeroId + " por " + clientSessionId);
        return true;
    }

    public synchronized void liberarCajero(String cajeroId) {
        String clientSessionId = cajerosOcupados.remove(cajeroId);
        String cajeroNombre = cajeroNombres.remove(cajeroId);
        if (clientSessionId != null) {
            sessionToCajero.remove(clientSessionId);
            EstadoMensaje mensaje = new EstadoMensaje(TipoMensaje.CAJERO_LIBERADO);
            mensaje.setCajeroId(cajeroId);
            mensaje.setCajeroNombre(cajeroNombre);
            messagingTemplate.convertAndSend("/topic/estado", mensaje);
            System.out.println("Cajero liberado: " + cajeroId);
        }
    }

    public boolean estaCajeroOcupado(String cajeroId) {
        return cajerosOcupados.containsKey(cajeroId);
    }

    public synchronized boolean bloquearOperacion(String cuenta, String operacion, String clientSessionId) {
        Map<String, String> opsCuenta = operacionesBloqueadas.computeIfAbsent(cuenta, k -> new ConcurrentHashMap<>());
        if (opsCuenta.containsKey(operacion)) {
            String owner = opsCuenta.get(operacion);
            if (clientSessionId.equals(owner)) {
                return true;
            }
            return false;
        }
        opsCuenta.put(operacion, clientSessionId);
        sessionToBloqueos.computeIfAbsent(clientSessionId, k -> ConcurrentHashMap.newKeySet()).add(cuenta + ":" + operacion);
        
        EstadoMensaje mensaje = new EstadoMensaje(TipoMensaje.OPERACION_BLOQUEADA);
        mensaje.setCuenta(cuenta);
        mensaje.setOperacion(operacion);
        mensaje.setSessionId(clientSessionId);
        messagingTemplate.convertAndSend("/topic/estado", mensaje);
        System.out.println("Operacion bloqueada: " + cuenta + ":" + operacion + " por " + clientSessionId);
        return true;
    }

    public synchronized void liberarOperacion(String cuenta, String operacion, String clientSessionId) {
        Map<String, String> opsCuenta = operacionesBloqueadas.get(cuenta);
        if (opsCuenta != null) {
            String owner = opsCuenta.get(operacion);
            if (clientSessionId.equals(owner)) {
                opsCuenta.remove(operacion);
                Set<String> bloqueos = sessionToBloqueos.get(clientSessionId);
                if (bloqueos != null) {
                    bloqueos.remove(cuenta + ":" + operacion);
                }
                EstadoMensaje mensaje = new EstadoMensaje(TipoMensaje.OPERACION_LIBERADA);
                mensaje.setCuenta(cuenta);
                mensaje.setOperacion(operacion);
                messagingTemplate.convertAndSend("/topic/estado", mensaje);
                System.out.println("Operacion liberada: " + cuenta + ":" + operacion);
            }
        }
    }

    public boolean estaOperacionBloqueada(String cuenta, String operacion) {
        Map<String, String> opsCuenta = operacionesBloqueadas.get(cuenta);
        return opsCuenta != null && opsCuenta.containsKey(operacion);
    }

    public boolean estaOperacionBloqueadaPorOtro(String cuenta, String operacion, String clientSessionId) {
        Map<String, String> opsCuenta = operacionesBloqueadas.get(cuenta);
        if (opsCuenta == null) return false;
        String owner = opsCuenta.get(operacion);
        return owner != null && !owner.equals(clientSessionId);
    }

    public synchronized void limpiarSesion(String clientSessionId) {
        limpiarBloqueosSesion(clientSessionId);
    }

    public synchronized void limpiarSesionWebSocket(String wsSessionId) {
        String clientSessionId = wsToClientSession.remove(wsSessionId);
        if (clientSessionId != null) {
            AtomicInteger count = clientConnectionCount.get(clientSessionId);
            if (count != null) {
                int remaining = count.decrementAndGet();
                System.out.println("Desconexion WS: " + wsSessionId + " (cliente: " + clientSessionId + ", restantes: " + remaining + ")");
                if (remaining <= 0) {
                    clientConnectionCount.remove(clientSessionId);
                    // En lugar de limpiar inmediatamente, programar la limpieza con delay
                    // Esto permite que la nueva página se conecte durante la navegación
                    System.out.println("Programando limpieza en " + CLEANUP_DELAY_MS + "ms para: " + clientSessionId);
                    final String sessionToClean = clientSessionId;
                    ScheduledFuture<?> future = scheduler.schedule(() -> {
                        ejecutarLimpiezaDiferida(sessionToClean);
                    }, CLEANUP_DELAY_MS, TimeUnit.MILLISECONDS);
                    pendingCleanups.put(clientSessionId, future);
                }
            }
        }
    }
    
    private synchronized void ejecutarLimpiezaDiferida(String clientSessionId) {
        // Verificar si hay nuevas conexiones (la limpieza fue cancelada)
        if (!pendingCleanups.containsKey(clientSessionId)) {
            System.out.println("Limpieza ya cancelada para: " + clientSessionId);
            return;
        }
        pendingCleanups.remove(clientSessionId);
        
        // Verificar si el cliente se reconecto
        AtomicInteger count = clientConnectionCount.get(clientSessionId);
        if (count != null && count.get() > 0) {
            System.out.println("Cliente reconectado, no limpiamos: " + clientSessionId);
            return;
        }
        
        System.out.println("Ejecutando limpieza diferida para: " + clientSessionId);
        limpiarBloqueosSesion(clientSessionId);
    }

    private void limpiarBloqueosSesion(String clientSessionId) {
        String cajeroId = sessionToCajero.remove(clientSessionId);
        if (cajeroId != null) {
            String cajeroNombre = cajeroNombres.remove(cajeroId);
            cajerosOcupados.remove(cajeroId);
            EstadoMensaje mensaje = new EstadoMensaje(TipoMensaje.CAJERO_LIBERADO);
            mensaje.setCajeroId(cajeroId);
            mensaje.setCajeroNombre(cajeroNombre);
            messagingTemplate.convertAndSend("/topic/estado", mensaje);
            System.out.println("Cajero liberado por limpieza: " + cajeroId);
        }
        Set<String> bloqueos = sessionToBloqueos.remove(clientSessionId);
        if (bloqueos != null) {
            for (String bloqueo : bloqueos) {
                String[] partes = bloqueo.split(":");
                if (partes.length == 2) {
                    String cuenta = partes[0];
                    String operacion = partes[1];
                    Map<String, String> opsCuenta = operacionesBloqueadas.get(cuenta);
                    if (opsCuenta != null) {
                        opsCuenta.remove(operacion);
                        EstadoMensaje mensaje = new EstadoMensaje(TipoMensaje.OPERACION_LIBERADA);
                        mensaje.setCuenta(cuenta);
                        mensaje.setOperacion(operacion);
                        messagingTemplate.convertAndSend("/topic/estado", mensaje);
                    }
                }
            }
        }
    }

    public EstadoMensaje obtenerEstadoCompleto() {
        EstadoMensaje estado = new EstadoMensaje(TipoMensaje.ESTADO_COMPLETO);
        estado.setCajerosOcupados(Collections.unmodifiableSet(new HashSet<>(cajerosOcupados.keySet())));
        Map<String, Set<String>> opsBloqueadas = new ConcurrentHashMap<>();
        for (Map.Entry<String, Map<String, String>> entry : operacionesBloqueadas.entrySet()) {
            opsBloqueadas.put(entry.getKey(), new HashSet<>(entry.getValue().keySet()));
        }
        estado.setOperacionesBloqueadas(opsBloqueadas);
        return estado;
    }

    public Set<String> getCajerosOcupados() {
        return Collections.unmodifiableSet(new HashSet<>(cajerosOcupados.keySet()));
    }

    public Set<String> getOperacionesBloqueadas(String cuenta) {
        Map<String, String> opsCuenta = operacionesBloqueadas.get(cuenta);
        if (opsCuenta == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(new HashSet<>(opsCuenta.keySet()));
    }
}