package ec.edu.monster.climov_eurekabank_soapjava_g02.controller;

import ec.edu.monster.climov_eurekabank_soapjava_g02.model.data.api.SoapApiService;

public class TransferenciaController {
    private final TransferenciaController.TransferenciaView view;
    private final SoapApiService service;

    public TransferenciaController(TransferenciaController.TransferenciaView view) {
        this.view = view;
        this.service = new SoapApiService();
    }

    public void iniciarTransferencia(String cuentaOrigen, String cuentaDestino, Double importe) {
        if (cuentaOrigen.isEmpty() || cuentaDestino.isEmpty() || importe.isNaN()) {
            view.onTransferenciaFailure("Cuentas e importe son requeridos.");
            return;
        }

        view.mostrarCargando(true);

        service.performTransferencia(cuentaOrigen, cuentaDestino, importe, new SoapApiService.SimpleCallBack() {
            @Override
            public void onResult(boolean success, String resultMessage) {
                view.mostrarCargando(false);
                if (success) {
                    view.onTransferenciaSuccess(resultMessage);
                } else {
                    view.onTransferenciaFailure(resultMessage != null ? resultMessage : "Error de transferencia desconocido.");
                }
            }
        });
    }

    public interface TransferenciaView {
        void mostrarCargando(boolean isLoading);
        void onTransferenciaSuccess(String messageSuccess);
        void onTransferenciaFailure(String mensajeError);
    }
}
