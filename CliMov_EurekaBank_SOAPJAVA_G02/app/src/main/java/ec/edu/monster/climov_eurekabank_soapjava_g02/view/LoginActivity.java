package ec.edu.monster.climov_eurekabank_soapjava_g02.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ec.edu.monster.climov_eurekabank_soapjava_g02.R;
import ec.edu.monster.climov_eurekabank_soapjava_g02.controller.LoginController;
import ec.edu.monster.climov_eurekabank_soapjava_g02.utils.SessionManager;

public class LoginActivity extends AppCompatActivity implements LoginController.LoginView {
    private LoginController controller;
    private SessionManager sessionManager;
    private EditText etUser, etPassword;
    private Button btnLogin;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Verificar si ya está logueado
        sessionManager = new SessionManager(this);
        if (sessionManager.isLoggedIn()) {
            goToCajeros();
            return;
        }

        setContentView(R.layout.activity_login);

        etUser = findViewById(R.id.et_user);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progress_bar);

        controller = new LoginController(this);

        btnLogin.setOnClickListener(v -> {
            String user = etUser.getText().toString();
            String password = etPassword.getText().toString();
            controller.iniciarLogin(user, password);
        });
    }

    @Override
    public void mostrarCargando(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!isLoading);
    }

    @Override
    public void onLoginSuccess() {
        Toast.makeText(this, "¡Login Exitoso!", Toast.LENGTH_SHORT).show();

        // Guardar sesión
        String username = etUser.getText().toString();
        sessionManager.createLoginSession(username);

        goToCajeros();
    }

    @Override
    public void onLoginFailure(String mensajeError) {
        Toast.makeText(this, "Error de Login: " + mensajeError, Toast.LENGTH_LONG).show();
    }

    private void goToCajeros() {
        Intent intent = new Intent(this, CajerosActivity.class);
        startActivity(intent);
        finish();
    }
}
