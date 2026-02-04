package ec.edu.monster.climov_eurekabank_soapjava_g02.controller;

import java.util.List;
import ec.edu.monster.climov_eurekabank_soapjava_g02.model.data.api.SoapApiService;
import ec.edu.monster.climov_eurekabank_soapjava_g02.model.pojo.methods.MovimientoResponseEnvelope;

public class CuentaDetalleController {
    private final SoapApiService service;
    private CuentaDetalleView view;

    public CuentaDetalleController(CuentaDetalleView callback) {
        this.service = new SoapApiService();
        this.view = callback;
    }

    public void cargarDetalles(String cuenta) {
        if (cuenta == null || cuenta.trim().isEmpty()) {
            view.onFailure("El número de cuenta no puede estar vacío.");
            return;
        }
        // 1. Cargar Monto
        cargarMonto(cuenta);

        // 2. Cargar Movimientos
        cargarMovimientos(cuenta);
    }

    private void cargarMonto(String cuenta) {
        if (cuenta.isEmpty()) {
            view.onFailure("Cuenta es requerida.");
            return;
        }

        view.mostrarCargando(true);

        service.performTraerMonto(cuenta, new SoapApiService.MontoCallBack() {
            @Override
            public void onResult(boolean success, Double monto, String resultMessage) {
                view.mostrarCargando(false);
                if (success) {
                    view.onMontoLoaded(monto);
                } else {
                    view.onFailure(resultMessage != null ? resultMessage : "Error de consulta de monto desconocido.");
                }
            }
        });
    }

    private void cargarMovimientos(String cuenta) {
        if (cuenta.isEmpty()) {
            view.onFailure("Cuenta es requerida.");
            return;
        }

        view.mostrarCargando(true);

        service.performTraerMovimientos(cuenta, new SoapApiService.MovimientosCallBack() {
            @Override
            public void onResult(boolean success, List<MovimientoResponseEnvelope.Movimiento> movimientos, String resultMessage) {
                view.mostrarCargando(false);
                if (success) {
                    view.onMovimientosLoaded(movimientos);
                } else {
                    view.onFailure(resultMessage != null ? resultMessage : "Error de consulta de movimientos desconocido.");
                }
            }
        });
    }


    public interface CuentaDetalleView {
        void onMontoLoaded(double saldo);
        void onMovimientosLoaded(List<MovimientoResponseEnvelope.Movimiento> movimientos);
        void onFailure(String error);
        void mostrarCargando(boolean isLoading);
    }
}
