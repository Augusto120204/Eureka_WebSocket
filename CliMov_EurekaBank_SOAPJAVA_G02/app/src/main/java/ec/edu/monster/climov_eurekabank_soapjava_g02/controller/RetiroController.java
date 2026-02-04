package ec.edu.monster.climov_eurekabank_soapjava_g02.controller;

import ec.edu.monster.climov_eurekabank_soapjava_g02.model.data.api.SoapApiService;

public class RetiroController {
    private final RetiroView view;
    private final SoapApiService service;

    public RetiroController(RetiroView view) {
        this.view = view;
        this.service = new SoapApiService();
    }

    public void iniciarRetiro(String cuenta, Double importe) {
        if (cuenta.isEmpty() || importe.isNaN()) {
            view.onRetiroFailure("Cuenta e importe son requeridos.");
            return;
        }

        view.mostrarCargando(true);

        service.performRetiro(cuenta, importe, new SoapApiService.SimpleCallBack() {
            @Override
            public void onResult(boolean success, String resultMessage) {
                view.mostrarCargando(false);
                if (success) {
                    view.onRetiroSuccess(resultMessage);
                } else {
                    view.onRetiroFailure(resultMessage != null ? resultMessage : "Error de retiro desconocido.");
                }
            }
        });
    }

    public interface RetiroView {
        void mostrarCargando(boolean isLoading);
        void onRetiroSuccess(String messageSuccess);
        void onRetiroFailure(String mensajeError);
    }
}
