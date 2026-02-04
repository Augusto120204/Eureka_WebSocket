package ec.edu.monster.climov_eurekabank_soapjava_g02.controller;

import ec.edu.monster.climov_eurekabank_soapjava_g02.model.data.api.SoapApiService;

public class LoginController {
    private final LoginView view;
    private final SoapApiService service;

    public LoginController(LoginView view) {
        this.view = view;
        this.service = new SoapApiService();
    }

    public void iniciarLogin(String user, String password) {
        if (user.isEmpty() || password.isEmpty()) {
            view.onLoginFailure("Usuario y contraseña son requeridos.");
            return;
        }

        view.mostrarCargando(true);

        service.performLogin(user, password, new SoapApiService.SimpleCallBack() {
            @Override
            public void onResult(boolean success, String errorMessage) {
                view.mostrarCargando(false);
                if (success) {
                    view.onLoginSuccess();
                } else {
                    view.onLoginFailure(errorMessage != null ? errorMessage : "Error de autenticación desconocido.");
                }
            }
        });
    }

    public interface LoginView {
        void mostrarCargando(boolean isLoading);
        void onLoginSuccess();
        void onLoginFailure(String mensajeError);
    }
}
