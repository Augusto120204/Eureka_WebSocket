package ec.edu.monster.climov_eurekabank_soapjava_g02.model.dto;

/**
 * DTO para representar un empleado/cajero.
 */
public class Empleado {
    private String codigo;
    private String nombre;
    private String paterno;
    private String materno;
    private String ciudad;
    private String direccion;
    private boolean ocupado;

    public Empleado() {}

    public Empleado(String codigo, String nombre, String paterno, String ciudad) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.paterno = paterno;
        this.ciudad = ciudad;
        this.ocupado = false;
    }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getPaterno() { return paterno; }
    public void setPaterno(String paterno) { this.paterno = paterno; }

    public String getMaterno() { return materno; }
    public void setMaterno(String materno) { this.materno = materno; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public boolean isOcupado() { return ocupado; }
    public void setOcupado(boolean ocupado) { this.ocupado = ocupado; }

    public String getNombreCompleto() {
        return nombre + " " + paterno;
    }
}
