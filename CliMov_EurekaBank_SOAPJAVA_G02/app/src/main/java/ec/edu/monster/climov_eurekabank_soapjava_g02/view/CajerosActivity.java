package ec.edu.monster.climov_eurekabank_soapjava_g02.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;
import java.util.Set;

import ec.edu.monster.climov_eurekabank_soapjava_g02.R;
import ec.edu.monster.climov_eurekabank_soapjava_g02.adapter.CajeroAdapter;
import ec.edu.monster.climov_eurekabank_soapjava_g02.model.data.api.SoapApiService;
import ec.edu.monster.climov_eurekabank_soapjava_g02.model.data.rest.IRestApiService;
import ec.edu.monster.climov_eurekabank_soapjava_g02.model.data.rest.RestApiClient;
import ec.edu.monster.climov_eurekabank_soapjava_g02.model.dto.ApiResponse;
import ec.edu.monster.climov_eurekabank_soapjava_g02.model.pojo.methods.EmpleadosResponseEnvelope;
import ec.edu.monster.climov_eurekabank_soapjava_g02.utils.SessionManager;
import ec.edu.monster.climov_eurekabank_soapjava_g02.websocket.WebSocketManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CajerosActivity extends AppCompatActivity implements WebSocketManager.WebSocketListener {

    private SessionManager sessionManager;
    private SoapApiService soapApiService;
    private WebSocketManager webSocketManager;
    
    private TextView tvWsStatus;
    private TextView tvUsuario;
    private TextView tvError;
    private ProgressBar progressBar;
    private RecyclerView rvCajeros;
    private Button btnLogout;
    
    private CajeroAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cajeros);

        sessionManager = new SessionManager(this);
        soapApiService = new SoapApiService();
        webSocketManager = WebSocketManager.getInstance();
        
        initViews();
        setupRecyclerView();
        setupListeners();
        
        // Mostrar usuario
        String username = sessionManager.getUsername();
        tvUsuario.setText("Hola, " + (username != null ? username : "Usuario"));
        
        // Conectar WebSocket
        conectarWebSocket();
        
        // Cargar cajeros
        cargarCajeros();
    }
    
    private void initViews() {
        tvWsStatus = findViewById(R.id.tv_ws_status);
        tvUsuario = findViewById(R.id.tv_usuario);
        tvError = findViewById(R.id.tv_error);
        progressBar = findViewById(R.id.progress_bar);
        rvCajeros = findViewById(R.id.rv_cajeros);
        btnLogout = findViewById(R.id.btn_logout);
    }
    
    private void setupRecyclerView() {
        adapter = new CajeroAdapter();
        rvCajeros.setLayoutManager(new GridLayoutManager(this, 2));
        rvCajeros.setAdapter(adapter);
        
        adapter.setOnCajeroClickListener(empleado -> seleccionarCajero(empleado));
    }
    
    private void setupListeners() {
        btnLogout.setOnClickListener(v -> logout());
    }
    
    private void conectarWebSocket() {
        webSocketManager.setListener(this);
        String baseUrl = RestApiClient.getBaseUrl();
        String sessionId = sessionManager.getWsSessionId();
        webSocketManager.connect(baseUrl, sessionId);
    }
    
    private void cargarCajeros() {
        progressBar.setVisibility(View.VISIBLE);
        
        soapApiService.performTraerEmpleados(new SoapApiService.EmpleadosCallBack() {
            @Override
            public void onResult(boolean success, List<EmpleadosResponseEnvelope.Empleado> empleados, String resultMessage) {
                android.util.Log.d("CajerosActivity", "=== RESPUESTA TRAER EMPLEADOS ===");
                android.util.Log.d("CajerosActivity", "Éxito: " + success);
                android.util.Log.d("CajerosActivity", "Mensaje: " + resultMessage);
                if (empleados != null) {
                    android.util.Log.d("CajerosActivity", "Cantidad empleados: " + empleados.size());
                    for (EmpleadosResponseEnvelope.Empleado e : empleados) {
                        android.util.Log.d("CajerosActivity", "  - " + e.getCodigo() + ": " + e.getNombreCompleto());
                    }
                }
                
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    
                    if (success && empleados != null) {
                        // Filtrar el empleado 9999
                        empleados.removeIf(e -> "9999".equals(e.getCodigo()));
                        adapter.setEmpleados(empleados);
                        
                        // Aplicar estado de ocupados del WebSocket
                        Set<String> ocupados = webSocketManager.getCajerosOcupados();
                        adapter.setCajerosOcupados(ocupados);
                    } else {
                        mostrarError("Error cargando cajeros: " + resultMessage);
                    }
                });
            }
        });
    }
    
    private void seleccionarCajero(EmpleadosResponseEnvelope.Empleado empleado) {
        // Verificar si esta ocupado
        if (webSocketManager.isCajeroOcupado(empleado.getCodigo())) {
            mostrarError("Este cajero esta ocupado por otro usuario.");
            return;
        }
        
        // Intentar ocupar el cajero via REST
        IRestApiService restApi = RestApiClient.getApiService();
        String sessionId = sessionManager.getWsSessionId();
        
        progressBar.setVisibility(View.VISIBLE);
        
        restApi.ocuparCajero(empleado.getCodigo(), empleado.getNombreCompleto(), sessionId)
            .enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    android.util.Log.d("CajerosActivity", "=== RESPUESTA OCUPAR CAJERO ===");
                    android.util.Log.d("CajerosActivity", "Código HTTP: " + response.code());
                    android.util.Log.d("CajerosActivity", "Exitoso: " + response.isSuccessful());
                    if (response.body() != null) {
                        android.util.Log.d("CajerosActivity", "Body Éxito: " + response.body().isExito());
                        android.util.Log.d("CajerosActivity", "Body Mensaje: " + response.body().getMensaje());
                    }
                    
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        
                        if (response.isSuccessful() && response.body() != null) {
                            if (response.body().isExito()) {
                                // Guardar cajero en sesion
                                sessionManager.setCajero(empleado.getCodigo(), empleado.getNombreCompleto());
                                
                                // Ir a seleccion de cuentas
                                Intent intent = new Intent(CajerosActivity.this, CuentasActivity.class);
                                startActivity(intent);
                            } else {
                                mostrarError(response.body().getMensaje());
                            }
                        } else {
                            mostrarError("Error de conexion con el servidor.");
                        }
                    });
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {                    android.util.Log.e("CajerosActivity", "=== ERROR OCUPAR CAJERO ===");
                    android.util.Log.e("CajerosActivity", "Error: " + t.getMessage(), t);
                                        runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        mostrarError("Error de conexion: " + t.getMessage());
                    });
                }
            });
    }
    
    private void mostrarError(String mensaje) {
        tvError.setText(mensaje);
        tvError.setVisibility(View.VISIBLE);
        
        // Ocultar despues de 5 segundos
        tvError.postDelayed(() -> tvError.setVisibility(View.GONE), 5000);
    }
    
    private void logout() {
        // Liberar cajero si hay uno seleccionado
        String cajeroId = sessionManager.getCajeroId();
        if (cajeroId != null) {
            RestApiClient.getApiService().liberarCajero(cajeroId).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {}
                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {}
            });
        }
        
        webSocketManager.disconnect();
        sessionManager.logoutUser();
        
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    
    // WebSocket callbacks
    @Override
    public void onConnected() {
        runOnUiThread(() -> {
            tvWsStatus.setText("En linea");
            tvWsStatus.setBackgroundResource(R.drawable.status_connected);
        });
    }
    
    @Override
    public void onDisconnected() {
        runOnUiThread(() -> {
            tvWsStatus.setText("Desconectado");
            tvWsStatus.setBackgroundResource(R.drawable.status_disconnected);
        });
    }
    
    @Override
    public void onError(String error) {
        runOnUiThread(() -> {
            tvWsStatus.setText("Error");
            tvWsStatus.setBackgroundResource(R.drawable.status_disconnected);
        });
    }
    
    @Override
    public void onEstadoCompleto(Set<String> cajerosOcupados, Map<String, Set<String>> operacionesBloqueadas) {
        runOnUiThread(() -> adapter.setCajerosOcupados(cajerosOcupados));
    }
    
    @Override
    public void onCajeroOcupado(String cajeroId) {
        runOnUiThread(() -> adapter.actualizarCajeroOcupado(cajeroId, true));
    }
    
    @Override
    public void onCajeroLiberado(String cajeroId) {
        runOnUiThread(() -> adapter.actualizarCajeroOcupado(cajeroId, false));
    }
    
    @Override
    public void onOperacionBloqueada(String cuenta, String operacion) {
        // No aplica en esta pantalla
    }
    
    @Override
    public void onOperacionLiberada(String cuenta, String operacion) {
        // No aplica en esta pantalla
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        webSocketManager.setListener(this);
        
        // Recargar cajeros y reconectar si es necesario
        if (!webSocketManager.isConnected()) {
            conectarWebSocket();
        }
        cargarCajeros();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // No desconectar el WebSocket aqui, se mantiene activo
    }
}
