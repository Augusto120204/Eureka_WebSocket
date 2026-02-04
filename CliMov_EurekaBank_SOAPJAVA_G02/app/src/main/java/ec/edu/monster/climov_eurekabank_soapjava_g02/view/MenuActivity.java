package ec.edu.monster.climov_eurekabank_soapjava_g02.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.util.Map;
import java.util.Set;

import ec.edu.monster.climov_eurekabank_soapjava_g02.R;
import ec.edu.monster.climov_eurekabank_soapjava_g02.model.data.rest.RestApiClient;
import ec.edu.monster.climov_eurekabank_soapjava_g02.model.dto.ApiResponse;
import ec.edu.monster.climov_eurekabank_soapjava_g02.utils.SessionManager;
import ec.edu.monster.climov_eurekabank_soapjava_g02.websocket.WebSocketManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuActivity extends AppCompatActivity implements WebSocketManager.WebSocketListener {
    private ImageButton btnBack;
    private MaterialButton btnRetiro;
    private MaterialButton btnTransferencia;
    private MaterialButton btnDeposito;
    private MaterialButton btnMovimientos;
    
    private TextView tvCajeroInfo;
    private TextView tvCuentaInfo;
    
    private SessionManager sessionManager;
    private WebSocketManager webSocketManager;
    
    private String cuentaActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        sessionManager = new SessionManager(this);
        webSocketManager = WebSocketManager.getInstance();
        
        cuentaActual = sessionManager.getCuentaSeleccionada();
        
        initViews();
        setupListeners();
        
        // Mostrar información
        mostrarInformacionSesion();
        
        // Conectar listener de WebSocket
        webSocketManager.setListener(this);
        
        // Actualizar estados de botones
        actualizarEstadoBotones();
    }
    
    private void initViews() {
        // Buttons
        btnDeposito = findViewById(R.id.btn_deposito);
        btnRetiro = findViewById(R.id.btn_retiro);
        btnMovimientos = findViewById(R.id.btn_movimientos);
        btnTransferencia = findViewById(R.id.btn_tranferencia);
        btnBack = findViewById(R.id.btn_back);
        
        // Info TextViews - agregar estos al layout si no existen
        // tvCajeroInfo = findViewById(R.id.tv_cajero_info);
        // tvCuentaInfo = findViewById(R.id.tv_cuenta_info);
    }
    
    private void mostrarInformacionSesion() {
        String cajeroNombre = sessionManager.getCajeroNombre();
        String cuenta = sessionManager.getCuentaSeleccionada();
        
        // Puedes actualizar el titulo o mostrar en el UI
        if (getSupportActionBar() != null) {
            String titulo = "Cajero: " + (cajeroNombre != null ? cajeroNombre : "N/A") + 
                          " | Cuenta: " + (cuenta != null ? cuenta : "N/A");
            getSupportActionBar().setTitle(titulo);
        }
    }
    
    private void actualizarEstadoBotones() {
        if (cuentaActual == null) {
            // No hay cuenta seleccionada
            deshabilitarBotones();
            return;
        }
        
        // Verificar operaciones bloqueadas
        btnDeposito.setEnabled(!webSocketManager.isOperacionBloqueada(cuentaActual, "deposito"));
        btnRetiro.setEnabled(!webSocketManager.isOperacionBloqueada(cuentaActual, "retiro"));
        btnTransferencia.setEnabled(!webSocketManager.isOperacionBloqueada(cuentaActual, "transferencia"));
    }
    
    private void deshabilitarBotones() {
        btnDeposito.setEnabled(false);
        btnRetiro.setEnabled(false);
        btnTransferencia.setEnabled(false);
    }

    private void setupListeners() {
        // Back button (Volver a cuentas)
        btnBack.setOnClickListener(v -> volverACuentas());
        
        btnDeposito.setOnClickListener(v -> goToBankFunction("deposito", DepositoActivity.class));
        btnRetiro.setOnClickListener(v -> goToBankFunction("retiro", RetiroActivity.class));
        btnMovimientos.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, MovimientosActivity.class);
            startActivity(intent);
        });
        btnTransferencia.setOnClickListener(v -> goToBankFunction("transferencia", TransferenciaActivity.class));
    }
    
    private void volverACuentas() {
        // Liberar operación actual si existe
        liberarOperacionActual();
        
        // Volver a selección de cuentas
        finish();
    }

    private void logout() {
        // Liberar operación actual
        liberarOperacionActual();
        
        // Liberar cajero
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

        Intent intent = new Intent(MenuActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void goToBankFunction(String operacion, Class<?> pageName) {
        if (cuentaActual == null) {
            Toast.makeText(this, "No hay cuenta seleccionada", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Verificar si la operación está bloqueada
        if (webSocketManager.isOperacionBloqueada(cuentaActual, operacion)) {
            Toast.makeText(this, "Esta operación está siendo utilizada por otro usuario", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Intentar bloquear la operación
        String sessionId = sessionManager.getWsSessionId();
        RestApiClient.getApiService().bloquearOperacion(cuentaActual, operacion, sessionId)
            .enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    runOnUiThread(() -> {
                        if (response.isSuccessful() && response.body() != null && response.body().isExito()) {
                            // Guardar operación actual
                            sessionManager.setOperacionActual(operacion, cuentaActual);
                            
                            // Ir a la actividad
                            Intent intent = new Intent(MenuActivity.this, pageName);
                            startActivity(intent);
                        } else {
                            String mensaje = response.body() != null ? response.body().getMensaje() : "Error al bloquear operación";
                            Toast.makeText(MenuActivity.this, mensaje, Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    runOnUiThread(() -> {
                        Toast.makeText(MenuActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            });
    }
    
    private void liberarOperacionActual() {
        String operacion = sessionManager.getOperacionActual();
        String cuenta = sessionManager.getCuentaOperacion();
        
        if (operacion != null && cuenta != null) {
            String sessionId = sessionManager.getWsSessionId();
            RestApiClient.getApiService().liberarOperacion(cuenta, operacion, sessionId)
                .enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        sessionManager.clearOperacionActual();
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        sessionManager.clearOperacionActual();
                    }
                });
        }
    }
    
    // WebSocket callbacks
    @Override
    public void onConnected() {
        // Conexión establecida
    }
    
    @Override
    public void onDisconnected() {
        // Conexión perdida
    }
    
    @Override
    public void onError(String error) {
        // Error de conexión
    }
    
    @Override
    public void onEstadoCompleto(Set<String> cajerosOcupados, Map<String, Set<String>> operacionesBloqueadas) {
        runOnUiThread(this::actualizarEstadoBotones);
    }
    
    @Override
    public void onCajeroOcupado(String cajeroId) {
        // No aplica en esta pantalla
    }
    
    @Override
    public void onCajeroLiberado(String cajeroId) {
        // No aplica en esta pantalla
    }
    
    @Override
    public void onOperacionBloqueada(String cuenta, String operacion) {
        runOnUiThread(this::actualizarEstadoBotones);
    }
    
    @Override
    public void onOperacionLiberada(String cuenta, String operacion) {
        runOnUiThread(this::actualizarEstadoBotones);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        webSocketManager.setListener(this);
        actualizarEstadoBotones();
        
        // Si no hay operación en curso, limpiar
        if (sessionManager.getOperacionActual() == null) {
            // Volver desde otra actividad
        }
    }
    
    @Override
    public void onBackPressed() {
        volverACuentas();
    }
}
