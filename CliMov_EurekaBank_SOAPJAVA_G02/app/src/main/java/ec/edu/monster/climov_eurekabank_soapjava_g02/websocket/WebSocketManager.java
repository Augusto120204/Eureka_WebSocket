package ec.edu.monster.climov_eurekabank_soapjava_g02.websocket;

import android.util.Log;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ec.edu.monster.climov_eurekabank_soapjava_g02.model.dto.EstadoMensaje;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.LifecycleEvent;

/**
 * Singleton que maneja la conexion WebSocket STOMP con el servidor.
 */
public class WebSocketManager {
    private static final String TAG = "WebSocketManager";
    private static WebSocketManager instance;
    
    private StompClient stompClient;
    private CompositeDisposable compositeDisposable;
    private Gson gson;
    private String sessionId;
    private boolean isConnected = false;
    
    private Set<String> cajerosOcupados = new HashSet<>();
    private Map<String, Set<String>> operacionesBloqueadas = new HashMap<>();
    
    private WebSocketListener listener;
    
    public interface WebSocketListener {
        void onConnected();
        void onDisconnected();
        void onError(String error);
        void onEstadoCompleto(Set<String> cajerosOcupados, Map<String, Set<String>> operacionesBloqueadas);
        void onCajeroOcupado(String cajeroId);
        void onCajeroLiberado(String cajeroId);
        void onOperacionBloqueada(String cuenta, String operacion);
        void onOperacionLiberada(String cuenta, String operacion);
    }
    
    private WebSocketManager() {
        compositeDisposable = new CompositeDisposable();
        gson = new Gson();
    }
    
    public static synchronized WebSocketManager getInstance() {
        if (instance == null) {
            instance = new WebSocketManager();
        }
        return instance;
    }
    
    public void setListener(WebSocketListener listener) {
        this.listener = listener;
    }
    
    public void connect(String baseUrl, String sessionId) {
        this.sessionId = sessionId;
        
        if (stompClient != null && stompClient.isConnected()) {
            Log.d(TAG, "Ya conectado");
            return;
        }
        
        String wsUrl = baseUrl.replace("http://", "ws://").replace("https://", "wss://");
        if (!wsUrl.endsWith("/")) {
            wsUrl += "/";
        }
        wsUrl += "ws-eureka";
        
        Log.d(TAG, "Conectando a: " + wsUrl);
        
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, wsUrl);
        
        Disposable lifecycleDisposable = stompClient.lifecycle()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(lifecycleEvent -> {
                switch (lifecycleEvent.getType()) {
                    case OPENED:
                        Log.d(TAG, "Conexion abierta");
                        isConnected = true;
                        suscribirseATopic();
                        if (listener != null) listener.onConnected();
                        break;
                    case ERROR:
                        Log.e(TAG, "Error: " + lifecycleEvent.getException());
                        if (listener != null) listener.onError(lifecycleEvent.getException().getMessage());
                        break;
                    case CLOSED:
                        Log.d(TAG, "Conexion cerrada");
                        isConnected = false;
                        if (listener != null) listener.onDisconnected();
                        break;
                    case FAILED_SERVER_HEARTBEAT:
                        Log.w(TAG, "Heartbeat fallido");
                        break;
                }
            }, throwable -> {
                Log.e(TAG, "Error en lifecycle: " + throwable.getMessage());
            });
        
        compositeDisposable.add(lifecycleDisposable);
        stompClient.connect();
    }
    
    private void suscribirseATopic() {
        Disposable topicDisposable = stompClient.topic("/topic/estado")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(stompMessage -> {
                String payload = stompMessage.getPayload();
                Log.d(TAG, "Mensaje recibido: " + payload);
                procesarMensaje(payload);
            }, throwable -> {
                Log.e(TAG, "Error en topic: " + throwable.getMessage());
            });
        
        compositeDisposable.add(topicDisposable);
        
        // Enviar mensaje de registro
        String registroJson = "{\"clientSessionId\":\"" + sessionId + "\"}";
        stompClient.send("/app/estado", registroJson)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(() -> {
                Log.d(TAG, "Registro enviado");
            }, throwable -> {
                Log.e(TAG, "Error enviando registro: " + throwable.getMessage());
            });
    }
    
    private void procesarMensaje(String payload) {
        try {
            EstadoMensaje estado = gson.fromJson(payload, EstadoMensaje.class);
            
            if (estado.getTipo() == null) {
                Log.w(TAG, "Tipo de mensaje nulo");
                return;
            }
            
            switch (estado.getTipo()) {
                case ESTADO_COMPLETO:
                    cajerosOcupados = estado.getCajerosOcupados() != null ? estado.getCajerosOcupados() : new HashSet<>();
                    operacionesBloqueadas = estado.getOperacionesBloqueadas();
                    if (listener != null) {
                        listener.onEstadoCompleto(cajerosOcupados, operacionesBloqueadas);
                    }
                    break;
                    
                case CAJERO_OCUPADO:
                    cajerosOcupados.add(estado.getCajeroId());
                    if (listener != null) listener.onCajeroOcupado(estado.getCajeroId());
                    break;
                    
                case CAJERO_LIBERADO:
                    cajerosOcupados.remove(estado.getCajeroId());
                    if (listener != null) listener.onCajeroLiberado(estado.getCajeroId());
                    break;
                    
                case OPERACION_BLOQUEADA:
                    // Actualizar el mapa interno
                    if (operacionesBloqueadas != null && estado.getCuenta() != null && estado.getOperacion() != null) {
                        Set<String> operaciones = operacionesBloqueadas.get(estado.getCuenta());
                        if (operaciones == null) {
                            operaciones = new HashSet<>();
                            operacionesBloqueadas.put(estado.getCuenta(), operaciones);
                        }
                        operaciones.add(estado.getOperacion());
                        Log.d(TAG, "Operación bloqueada actualizada: " + estado.getCuenta() + " - " + estado.getOperacion());
                    }
                    if (listener != null) listener.onOperacionBloqueada(estado.getCuenta(), estado.getOperacion());
                    break;
                    
                case OPERACION_LIBERADA:
                    // Actualizar el mapa interno
                    if (operacionesBloqueadas != null && estado.getCuenta() != null && estado.getOperacion() != null) {
                        Set<String> operaciones = operacionesBloqueadas.get(estado.getCuenta());
                        if (operaciones != null) {
                            operaciones.remove(estado.getOperacion());
                            Log.d(TAG, "Operación liberada actualizada: " + estado.getCuenta() + " - " + estado.getOperacion());
                        }
                    }
                    if (listener != null) listener.onOperacionLiberada(estado.getCuenta(), estado.getOperacion());
                    break;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error procesando mensaje: " + e.getMessage());
        }
    }
    
    public boolean isCajeroOcupado(String cajeroId) {
        return cajerosOcupados.contains(cajeroId);
    }
    
    public Set<String> getCajerosOcupados() {
        return cajerosOcupados;
    }
    
    public boolean isOperacionBloqueada(String cuenta, String operacion) {
        if (operacionesBloqueadas == null || cuenta == null || operacion == null) {
            return false;
        }
        Set<String> operaciones = operacionesBloqueadas.get(cuenta);
        return operaciones != null && operaciones.contains(operacion);
    }
    
    public boolean isConnected() {
        return isConnected && stompClient != null && stompClient.isConnected();
    }
    
    public void disconnect() {
        if (stompClient != null) {
            stompClient.disconnect();
        }
        if (compositeDisposable != null) {
            compositeDisposable.clear();
        }
        isConnected = false;
    }
    
    public void destroy() {
        disconnect();
        compositeDisposable.dispose();
        instance = null;
    }
}
