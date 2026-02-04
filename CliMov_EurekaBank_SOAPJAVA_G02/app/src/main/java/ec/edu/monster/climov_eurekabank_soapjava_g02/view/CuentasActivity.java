package ec.edu.monster.climov_eurekabank_soapjava_g02.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ec.edu.monster.climov_eurekabank_soapjava_g02.R;
import ec.edu.monster.climov_eurekabank_soapjava_g02.adapter.CuentaAdapter;
import ec.edu.monster.climov_eurekabank_soapjava_g02.model.data.api.SoapApiService;
import ec.edu.monster.climov_eurekabank_soapjava_g02.model.data.rest.RestApiClient;
import ec.edu.monster.climov_eurekabank_soapjava_g02.model.dto.ApiResponse;
import ec.edu.monster.climov_eurekabank_soapjava_g02.utils.SessionManager;
import ec.edu.monster.climov_eurekabank_soapjava_g02.websocket.WebSocketManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CuentasActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private SoapApiService soapApiService;
    
    private TextView tvCajeroNombre;
    private TextView tvError;
    private ProgressBar progressBar;
    private RecyclerView rvCuentas;
    private Button btnCambiarCajero;
    
    private CuentaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuentas);

        sessionManager = new SessionManager(this);
        soapApiService = new SoapApiService();
        
        initViews();
        setupRecyclerView();
        setupListeners();
        
        // Mostrar nombre del cajero
        String cajeroNombre = sessionManager.getCajeroNombre();
        tvCajeroNombre.setText("Cajero: " + (cajeroNombre != null ? cajeroNombre : "No seleccionado"));
        
        // Cargar cuentas
        cargarCuentas();
    }
    
    private void initViews() {
        tvCajeroNombre = findViewById(R.id.tv_cajero_nombre);
        tvError = findViewById(R.id.tv_error);
        progressBar = findViewById(R.id.progress_bar);
        rvCuentas = findViewById(R.id.rv_cuentas);
        btnCambiarCajero = findViewById(R.id.btn_back);
    }
    
    private void setupRecyclerView() {
        adapter = new CuentaAdapter();
        rvCuentas.setLayoutManager(new LinearLayoutManager(this));
        rvCuentas.setAdapter(adapter);
        
        adapter.setOnCuentaClickListener(cuenta -> seleccionarCuenta(cuenta));
    }
    
    private void setupListeners() {
        btnCambiarCajero.setOnClickListener(v -> cambiarCajero());
    }
    
    private void cargarCuentas() {
        progressBar.setVisibility(View.VISIBLE);
        
        soapApiService.performTraerCuentas(new SoapApiService.CuentasCallBack() {
            @Override
            public void onResult(boolean success, List<String> cuentas, String resultMessage) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    
                    if (success && cuentas != null) {
                        adapter.setCuentas(cuentas);
                    } else {
                        mostrarError("Error cargando cuentas: " + resultMessage);
                    }
                });
            }
        });
    }
    
    private void seleccionarCuenta(String cuenta) {
        // Guardar cuenta seleccionada
        sessionManager.setCuentaSeleccionada(cuenta);
        
        // Ir al menu de operaciones
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }
    
    private void mostrarError(String mensaje) {
        tvError.setText(mensaje);
        tvError.setVisibility(View.VISIBLE);
        tvError.postDelayed(() -> tvError.setVisibility(View.GONE), 5000);
    }
    
    private void cambiarCajero() {
        // Liberar el cajero actual
        String cajeroId = sessionManager.getCajeroId();
        if (cajeroId != null) {
            RestApiClient.getApiService().liberarCajero(cajeroId).enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    runOnUiThread(() -> {
                        sessionManager.setCajero(null, null);
                        finish();
                    });
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    runOnUiThread(() -> {
                        sessionManager.setCajero(null, null);
                        finish();
                    });
                }
            });
        } else {
            finish();
        }
    }
    
    @Override
    public void onBackPressed() {
        cambiarCajero();
    }
}
