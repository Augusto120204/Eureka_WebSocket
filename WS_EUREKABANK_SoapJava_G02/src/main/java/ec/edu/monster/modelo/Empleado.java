package ec.edu.monster.modelo;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Modelo que representa un Empleado del sistema EurekaBank.
 * 
 * @author leito
 */
@XmlRootElement(name = "empleado")
public class Empleado {
    
    private String codigo;       // chr_emplcodigo (char 4)
    private String paterno;      // vch_emplpaterno (varchar 25)
    private String materno;      // vch_emplmaterno (varchar 25)
    private String nombre;       // vch_emplnombre (varchar 30)
    private String ciudad;       // vch_emplciudad (varchar 30)
    private String direccion;    // vch_empldireccion (varchar 50)

    public Empleado() {
    }

    public Empleado(String codigo, String paterno, String materno, String nombre, String ciudad, String direccion) {
        this.codigo = codigo;
        this.paterno = paterno;
        this.materno = materno;
        this.nombre = nombre;
        this.ciudad = ciudad;
        this.direccion = direccion;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getPaterno() {
        return paterno;
    }

    public void setPaterno(String paterno) {
        this.paterno = paterno;
    }

    public String getMaterno() {
        return materno;
    }

    public void setMaterno(String materno) {
        this.materno = materno;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
}
