package ec.edu.monster.climov_eurekabank_soapjava_g02.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import ec.edu.monster.climov_eurekabank_soapjava_g02.R;

public class MovimientosActivity extends AppCompatActivity {
    private EditText etCuentaConsulta;
    private Button btnConsultarCuenta;
    private ProgressBar progressBar;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movimientos);

        initViews();
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        etCuentaConsulta = findViewById(R.id.et_cuenta_consulta);
        btnConsultarCuenta = findViewById(R.id.btn_consultar_cuenta);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> goToMenu());

        btnConsultarCuenta.setOnClickListener(v -> {
            String cuenta = etCuentaConsulta.getText().toString().trim();
            if (cuenta.isEmpty()) {
                Toast.makeText(this, "Debe ingresar un código de cuenta.", Toast.LENGTH_SHORT).show();
            } else {
                // Navegar a CuentaDetalleActivity con el código de cuenta
                Intent intent = new Intent(MovimientosActivity.this, CuentaDetalleActivity.class);
                intent.putExtra(CuentaDetalleActivity.EXTRA_CUENTA, cuenta);
                startActivity(intent);
            }
        });
    }

    private void goToMenu() {
        Intent intent = new Intent(this, MenuActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
