package ec.edu.monster.modelo;

import ec.edu.monster.modelos.MovimientoViewDTO;
import ec.edu.monster.ws.conuni.Movimiento; // Importa la clase Movimiento de tu servicio SOAP
import java.util.List;

public class CuentaViewModel {
    
    private String cuentaCodigo;
    private double montoActual;
    private List<MovimientoViewDTO> movimientos; // Se asume que Movimiento viene del paquete ws.conuni
    private String mensajeError;

    // Constructor vacío (necesario para Spring/Thymeleaf)
    public CuentaViewModel() {
    }

    // Constructor con todos los campos (opcional, pero útil)
    public CuentaViewModel(String cuentaCodigo, double montoActual, List<MovimientoViewDTO> movimientos, String mensajeError) {
        this.cuentaCodigo = cuentaCodigo;
        this.montoActual = montoActual;
        this.movimientos = movimientos;
        this.mensajeError = mensajeError;
    }

    // --- Getters y Setters ---

    public String getCuentaCodigo() {
        return cuentaCodigo;
    }

    public void setCuentaCodigo(String cuentaCodigo) {
        this.cuentaCodigo = cuentaCodigo;
    }

    public double getMontoActual() {
        return montoActual;
    }

    public void setMontoActual(double montoActual) {
        this.montoActual = montoActual;
    }

    public List<MovimientoViewDTO> getMovimientos() {
        return movimientos;
    }

    public void setMovimientos(List<MovimientoViewDTO> movimientos) {
        this.movimientos = movimientos;
    }

    public String getMensajeError() {
        return mensajeError;
    }

    public void setMensajeError(String mensajeError) {
        this.mensajeError = mensajeError;
    }
}