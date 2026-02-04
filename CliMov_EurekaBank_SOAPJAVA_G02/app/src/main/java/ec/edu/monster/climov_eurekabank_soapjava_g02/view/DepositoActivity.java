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
import ec.edu.monster.climov_eurekabank_soapjava_g02.controller.DepositoController;
import ec.edu.monster.climov_eurekabank_soapjava_g02.model.data.rest.RestApiClient;
import ec.edu.monster.climov_eurekabank_soapjava_g02.model.dto.ApiResponse;
import ec.edu.monster.climov_eurekabank_soapjava_g02.utils.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DepositoActivity extends AppCompatActivity implements DepositoController.DepositoView {
    private DepositoController controller;
    private EditText etDepositoCuenta, etDepositoImporte;
    private Button btnDeposito;

    private ImageButton btnBack;
    private ProgressBar progressBar;
    
    private SessionManager sessionManager;
    private String cuentaSeleccionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_deposito);

        sessionManager = new SessionManager(this);
        cuentaSeleccionada = sessionManager.getCuentaSeleccionada();
        
        etDepositoCuenta = findViewById(R.id.et_cuenta);
        etDepositoImporte = findViewById(R.id.et_importe);
        btnDeposito = findViewById(R.id.btn_deposito);
        progressBar = findViewById(R.id.progress_bar);
        btnBack = findViewById(R.id.btn_back);

        controller = new DepositoController(this);
        
        // Pre-llenar la cuenta seleccionada
        if (cuentaSeleccionada != null) {
            etDepositoCuenta.setText(cuentaSeleccionada);
            etDepositoCuenta.setEnabled(false);
        }

        btnDeposito.setOnClickListener(v -> {
            String cuenta = etDepositoCuenta.getText().toString();
            String importeString = etDepositoImporte.getText().toString();

            // Limpieza de la cadena para asegurar el formato estándar (punto)
            // Esto previene errores de NumberFormatException con locales que usan la coma
            importeString = importeString.replace(',', '.');

            try {
                Double importe = Double.parseDouble(importeString);
                controller.iniciarDeposito(cuenta, importe);
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
        btnDeposito.setEnabled(!isLoading);
    }

    @Override
    public void onDepositoSuccess(String mensajeSuccess) {
        Toast.makeText(this, mensajeSuccess, Toast.LENGTH_SHORT).show();
        liberarOperacionYVolver();
    }

    @Override
    public void onDepositoFailure(String mensajeError) {
        Toast.makeText(this, "Error de Depósito: " + mensajeError, Toast.LENGTH_LONG).show();
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
