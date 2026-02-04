package ec.edu.monster.climov_eurekabank_soapjava_g02.controller;

import ec.edu.monster.climov_eurekabank_soapjava_g02.model.data.api.SoapApiService;

public class DepositoController{
    private final DepositoController.DepositoView view;
    private final SoapApiService service;

    public DepositoController(DepositoController.DepositoView view) {
        this.view = view;
        this.service = new SoapApiService();
    }

    public void iniciarDeposito(String cuenta, Double importe) {
        if (cuenta.isEmpty() || importe.isNaN()) {
            view.onDepositoFailure("Cuenta e importe son requeridos.");
            return;
        }

        view.mostrarCargando(true);

        service.performDeposito(cuenta, importe, new SoapApiService.SimpleCallBack() {
            @Override
            public void onResult(boolean success, String resultMessage) {
                view.mostrarCargando(false);
                if (success) {
                    view.onDepositoSuccess(resultMessage);
                } else {
                    view.onDepositoFailure(resultMessage != null ? resultMessage : "Error de deposito desconocido.");
                }
            }
        });
    }

    public interface DepositoView {
        void mostrarCargando(boolean isLoading);
        void onDepositoSuccess(String messageSuccess);
        void onDepositoFailure(String mensajeError);
    }
}
