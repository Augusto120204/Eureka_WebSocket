package ec.edu.monster.climov_eurekabank_soapjava_g02.model.dto;

/**
 * DTO para la respuesta del API REST.
 */
public class ApiResponse {
    private boolean exito;
    private String mensaje;

    public ApiResponse() {}

    public boolean isExito() { return exito; }
    public void setExito(boolean exito) { this.exito = exito; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
}
