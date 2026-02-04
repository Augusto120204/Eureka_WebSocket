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
        boolean depositoBloqueado = webSocketManager.isOperacionBloqueada(cuentaActual, "deposito");
        boolean retiroBloqueado = webSocketManager.isOperacionBloqueada(cuentaActual, "retiro");
        boolean transferenciaBloqueada = webSocketManager.isOperacionBloqueada(cuentaActual, "transferencia");
        boolean movimientosBloqueado = webSocketManager.isOperacionBloqueada(cuentaActual, "movimientos");
        
        android.util.Log.d("MenuActivity", "=== ACTUALIZAR ESTADO BOTONES ===");
        android.util.Log.d("MenuActivity", "Cuenta actual: " + cuentaActual);
        android.util.Log.d("MenuActivity", "Depósito bloqueado: " + depositoBloqueado);
        android.util.Log.d("MenuActivity", "Retiro bloqueado: " + retiroBloqueado);
        android.util.Log.d("MenuActivity", "Transferencia bloqueada: " + transferenciaBloqueada);
        android.util.Log.d("MenuActivity", "Movimientos bloqueado: " + movimientosBloqueado);
        
        // Actualizar depósito
        btnDeposito.setEnabled(!depositoBloqueado);
        actualizarEstiloBoton(btnDeposito, !depositoBloqueado);
        
        // Actualizar retiro
        btnRetiro.setEnabled(!retiroBloqueado);
        actualizarEstiloBoton(btnRetiro, !retiroBloqueado);
        
        // Actualizar transferencia
        btnTransferencia.setEnabled(!transferenciaBloqueada);
        actualizarEstiloBoton(btnTransferencia, !transferenciaBloqueada);
        
        // Actualizar movimientos
        btnMovimientos.setEnabled(!movimientosBloqueado);
        actualizarEstiloBoton(btnMovimientos, !movimientosBloqueado);
    }
    
    private void actualizarEstiloBoton(MaterialButton boton, boolean habilitado) {
        if (habilitado) {
            boton.setBackgroundResource(R.drawable.bg_login_gradient);
            boton.setTextColor(getResources().getColor(android.R.color.black));
            boton.setAlpha(1.0f);
        } else {
            boton.setBackgroundResource(R.drawable.bg_button_disabled);
            boton.setTextColor(getResources().getColor(android.R.color.darker_gray));
            boton.setAlpha(0.6f);
        }
    }
    
    private void deshabilitarBotones() {
        btnDeposito.setEnabled(false);
        actualizarEstiloBoton(btnDeposito, false);
        
        btnRetiro.setEnabled(false);
        actualizarEstiloBoton(btnRetiro, false);
        
        btnTransferencia.setEnabled(false);
        actualizarEstiloBoton(btnTransferencia, false);
        
        btnMovimientos.setEnabled(false);
        actualizarEstiloBoton(btnMovimientos, false);
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
        
        // Limpiar cuenta seleccionada
        sessionManager.setCuentaSeleccionada(null);
        
        // Volver a selección de cuentas creando un nuevo Intent
        // Esto asegura que CuentasActivity se recree si fue destruida
        Intent intent = new Intent(this, CuentasActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
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
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {                    android.util.Log.d("MenuActivity", "=== RESPUESTA BLOQUEAR OPERACION ===");
                    android.util.Log.d("MenuActivity", "Operación: " + operacion + ", Cuenta: " + cuentaActual);
                    android.util.Log.d("MenuActivity", "Código HTTP: " + response.code());
                    if (response.body() != null) {
                        android.util.Log.d("MenuActivity", "Éxito: " + response.body().isExito());
                        android.util.Log.d("MenuActivity", "Mensaje: " + response.body().getMensaje());
                    }
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
                    android.util.Log.e("MenuActivity", "=== ERROR BLOQUEAR OPERACION ===");
                    android.util.Log.e("MenuActivity", "Error: " + t.getMessage(), t);
                    
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
    protected void onPause() {
        super.onPause();
        // Remover el listener pero NO desconectar el WebSocket
        // porque la sesión del cajero sigue activa
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // NO desconectar el WebSocket aquí porque puede ser que
        // solo estemos volviendo a CuentasActivity
        // El WebSocket se desconectará cuando se libere el cajero
    }
    
    @Override
    public void onBackPressed() {
        volverACuentas();
    }
}
