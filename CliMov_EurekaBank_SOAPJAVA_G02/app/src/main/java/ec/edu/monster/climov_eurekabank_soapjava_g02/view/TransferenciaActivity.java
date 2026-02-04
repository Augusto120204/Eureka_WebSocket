package ec.edu.monster.climov_eurekabank_soapjava_g02.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ec.edu.monster.climov_eurekabank_soapjava_g02.R;
import ec.edu.monster.climov_eurekabank_soapjava_g02.controller.TransferenciaController;
import ec.edu.monster.climov_eurekabank_soapjava_g02.model.data.rest.RestApiClient;
import ec.edu.monster.climov_eurekabank_soapjava_g02.model.dto.ApiResponse;
import ec.edu.monster.climov_eurekabank_soapjava_g02.utils.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransferenciaActivity extends AppCompatActivity implements TransferenciaController.TransferenciaView {
    private TransferenciaController controller;
    private EditText etCuentaOrigen, etCuentaDestino, etImporte;
    private Button btnTransferencia;

    private ImageButton btnBack;
    private ProgressBar progressBar;
    
    private SessionManager sessionManager;
    private String cuentaSeleccionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_transferencia);

        sessionManager = new SessionManager(this);
        cuentaSeleccionada = sessionManager.getCuentaSeleccionada();
        
        etCuentaOrigen = findViewById(R.id.et_cuenta_origen);
        etCuentaDestino = findViewById(R.id.et_cuenta_destino);
        etImporte = findViewById(R.id.et_importe);
        btnTransferencia = findViewById(R.id.btn_transfer);
        progressBar = findViewById(R.id.progress_bar);
        btnBack = findViewById(R.id.btn_back);

        controller = new TransferenciaController(this);
        
        // Pre-llenar la cuenta seleccionada como origen
        if (cuentaSeleccionada != null) {
            etCuentaOrigen.setText(cuentaSeleccionada);
            etCuentaOrigen.setEnabled(false);
        }

        btnTransferencia.setOnClickListener(v -> {
            String cuentaOrigen = etCuentaOrigen.getText().toString();
            String cuentaDestino = etCuentaDestino.getText().toString();
            String importeString = etImporte.getText().toString();

            // Limpieza de la cadena para asegurar el formato estándar (punto)
            // Esto previene errores de NumberFormatException con locales que usan la coma
            importeString = importeString.replace(',', '.');

            try {
                Double importe = Double.parseDouble(importeString);
                controller.iniciarTransferencia(cuentaOrigen, cuentaDestino, importe);
            } catch (NumberFormatException e) {
                // Manejo de error si la entrada no es un número válido después de la limpieza
                Toast.makeText(this, "Por favor, ingrese un importe numérico válido.", Toast.LENGTH_LONG).show();
            }
        });

        btnBack.setOnClickListener(v -> volverAMenu());
    }

    @Override
    public void mostrarCargando(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnTransferencia.setEnabled(!isLoading);
    }

    @Override
    public void onTransferenciaSuccess(String mensajeSuccess) {
        Toast.makeText(this, mensajeSuccess, Toast.LENGTH_SHORT).show();
        liberarOperacionYVolver();
    }

    @Override
    public void onTransferenciaFailure(String mensajeError) {
        Toast.makeText(this, "Error de Transferencia: " + mensajeError, Toast.LENGTH_LONG).show();
    }
    
    private void liberarOperacionYVolver() {
        String operacion = sessionManager.getOperacionActual();
        String cuenta = sessionManager.getCuentaOperacion();
        
        if (operacion != null && cuenta != null) {
            String sessionId = sessionManager.getWsSessionId();
            RestApiClient.getApiService().liberarOperacion(cuenta, operacion, sessionId)
                .enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        runOnUiThread(() -> {
                            sessionManager.clearOperacionActual();
                            goToMenu();
                        });
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        runOnUiThread(() -> {
                            sessionManager.clearOperacionActual();
                            goToMenu();
                        });
                    }
                });
        } else {
            goToMenu();
        }
    }
    
    private void volverAMenu() {
        liberarOperacionYVolver();
    }

    private void goToMenu() {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
        finish();
    }
    
    @Override
    public void onBackPressed() {
        volverAMenu();
    }

}