package ec.edu.monster.climov_eurekabank_soapjava_g02.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ec.edu.monster.climov_eurekabank_soapjava_g02.R;
import ec.edu.monster.climov_eurekabank_soapjava_g02.adapter.MovimientoAdapter;
import ec.edu.monster.climov_eurekabank_soapjava_g02.controller.CuentaDetalleController;
import ec.edu.monster.climov_eurekabank_soapjava_g02.model.pojo.methods.MovimientoResponseEnvelope;

public class CuentaDetalleActivity extends AppCompatActivity implements CuentaDetalleController.CuentaDetalleView {
    public static final String EXTRA_CUENTA = "extra_cuenta_codigo";

    private TextView tvNumeroCuenta, tvSaldo, tvSinMovimientos;
    private RecyclerView rvMovimientos;
    private ProgressBar progressBar;
    private ImageButton btnBack;

    private CuentaDetalleController controller;
    private String cuentaCodigo;
    private MovimientoAdapter movimientoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuenta_detalle);

        // 1. Obtener datos de la cuenta
        cuentaCodigo = getIntent().getStringExtra(EXTRA_CUENTA);
        if (cuentaCodigo == null) {
            Toast.makeText(this, "Cuenta no especificada.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        controller = new CuentaDetalleController(this);

        initViews();
        setupRecyclerView();
        setupListeners();

        // 2. Iniciar la carga de datos
        controller.cargarDetalles(cuentaCodigo);
    }

    private void initViews() {
        tvNumeroCuenta = findViewById(R.id.tv_numero_cuenta);
        tvSaldo = findViewById(R.id.tv_saldo);
        tvSinMovimientos = findViewById(R.id.tv_sin_movimientos);
        rvMovimientos = findViewById(R.id.rv_movimientos);
        progressBar = findViewById(R.id.progress_bar);
        btnBack = findViewById(R.id.btn_back);
    }

    private void setupRecyclerView() {
        rvMovimientos.setLayoutManager(new LinearLayoutManager(this));
        movimientoAdapter = new MovimientoAdapter(this, new ArrayList<>());
        rvMovimientos.setAdapter(movimientoAdapter);

        // Configurar el TextView de la cuenta inmediatamente
        tvNumeroCuenta.setText(cuentaCodigo);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
    }

    // --- Implementación de Callbacks del Controller ---

    @Override
    public void onMontoLoaded(double saldo) {
        runOnUiThread(() -> {
            // Formatear el saldo como moneda
            NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("es", "EC"));
            String saldoFormateado = format.format(saldo);
            tvSaldo.setText(saldoFormateado);
        });
    }

    @Override
    public void onMovimientosLoaded(List<MovimientoResponseEnvelope.Movimiento> movimientos) {
        runOnUiThread(() -> {
            if (movimientos == null || movimientos.isEmpty()) {
                rvMovimientos.setVisibility(View.GONE);
                tvSinMovimientos.setVisibility(View.VISIBLE);
            } else {
                rvMovimientos.setVisibility(View.VISIBLE);
                tvSinMovimientos.setVisibility(View.GONE);

                // Actualizar el adaptador del RecyclerView
                movimientoAdapter.setMovimientos(movimientos);
            }
        });
    }

    @Override
    public void onFailure(String error) {
        runOnUiThread(() -> {
            System.out.println("Error" + error);
            Toast.makeText(this, "Error: " + error, Toast.LENGTH_LONG).show();
            goBack();
        });
    }

    @Override
    public void mostrarCargando(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    private void goBack() {
        Intent intent = new Intent(this, MovimientosActivity.class);
        startActivity(intent);
        finish();
    }
}
