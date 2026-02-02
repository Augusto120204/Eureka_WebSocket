package ec.edu.monster.modelos;

import java.util.Date;

// Creamos un DTO simple para manejar los tipos de la vista
public class MovimientoViewDTO {
    private Integer nromov;
    private Date fecha; // Ahora es un tipo Date simple
    private String tipo;
    private String accion;
    private double importe;

    // Getters y Setters (Necesarios para Thymeleaf)

    public Integer getNromov() {
        return nromov;
    }

    public void setNromov(Integer nromov) {
        this.nromov = nromov;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public double getImporte() {
        return importe;
    }

    public void setImporte(double importe) {
        this.importe = importe;
    }
}