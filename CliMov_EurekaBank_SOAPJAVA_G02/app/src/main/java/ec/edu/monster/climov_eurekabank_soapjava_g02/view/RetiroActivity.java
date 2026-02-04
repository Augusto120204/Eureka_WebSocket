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
import ec.edu.monster.climov_eurekabank_soapjava_g02.controller.RetiroController;
import ec.edu.monster.climov_eurekabank_soapjava_g02.model.data.rest.RestApiClient;
import ec.edu.monster.climov_eurekabank_soapjava_g02.model.dto.ApiResponse;
import ec.edu.monster.climov_eurekabank_soapjava_g02.utils.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RetiroActivity extends AppCompatActivity implements RetiroController.RetiroView{
    private RetiroController controller;
    private EditText etRetiroCuenta, etRetiroImporte;
    private Button btnRetiro;

    private ImageButton btnBack;
    private ProgressBar progressBar;
    
    private SessionManager sessionManager;
    private String cuentaSeleccionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_retiro);

        sessionManager = new SessionManager(this);
        cuentaSeleccionada = sessionManager.getCuentaSeleccionada();
        
        etRetiroCuenta = findViewById(R.id.et_retiro_cuenta);
        etRetiroImporte = findViewById(R.id.et_retiro_importe);
        btnRetiro = findViewById(R.id.btn_retiro);
        progressBar = findViewById(R.id.progress_bar);
        btnBack = findViewById(R.id.btn_back);

        controller = new RetiroController(this);
        
        // Pre-llenar la cuenta seleccionada
        if (cuentaSeleccionada != null) {
            etRetiroCuenta.setText(cuentaSeleccionada);
            etRetiroCuenta.setEnabled(false);
        }

        btnRetiro.setOnClickListener(v -> {
            String cuenta = etRetiroCuenta.getText().toString();
            String importeString = etRetiroImporte.getText().toString();

            // Limpieza de la cadena para asegurar el formato estándar (punto)
            // Esto previene errores de NumberFormatException con locales que usan la coma
            importeString = importeString.replace(',', '.');

            try {
                Double importe = Double.parseDouble(importeString);
                controller.iniciarRetiro(cuenta, importe);
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
        btnRetiro.setEnabled(!isLoading);
    }

    @Override
    public void onRetiroSuccess(String mensajeSuccess) {
        Toast.makeText(this, mensajeSuccess, Toast.LENGTH_SHORT).show();
        liberarOperacionYVolver();
    }

    @Override
    public void onRetiroFailure(String mensajeError) {
        Toast.makeText(this, "Error de Retiro: " + mensajeError, Toast.LENGTH_LONG).show();
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
