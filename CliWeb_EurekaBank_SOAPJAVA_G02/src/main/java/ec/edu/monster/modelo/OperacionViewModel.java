package ec.edu.monster.modelo;

public class OperacionViewModel {
    
    private String cuenta;          // Usado como Cuenta Origen o Cuenta Principal
    private double importe;
    private String cuentaDestino;
    private String resultado;       // Mensaje de éxito o error

    // Constructor vacío
    public OperacionViewModel() {
    }

    // Constructor (opcional)
    public OperacionViewModel(String cuenta, double importe, String cuentaDestino, String resultado) {
        this.cuenta = cuenta;
        this.importe = importe;
        this.cuentaDestino = cuentaDestino;
        this.resultado = resultado;
    }

    // --- Getters y Setters ---

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public double getImporte() {
        return importe;
    }

    public void setImporte(double importe) {
        this.importe = importe;
    }

    public String getCuentaDestino() {
        return cuentaDestino;
    }

    public void setCuentaDestino(String cuentaDestino) {
        this.cuentaDestino = cuentaDestino;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }
}