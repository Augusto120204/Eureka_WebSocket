package ec.edu.monster.climov_eurekabank_soapjava_g02.model.pojo.methods;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.NamespaceList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "Envelope", strict = false)
@NamespaceList({
        @Namespace(prefix = "S", reference = "http://schemas.xmlsoap.org/soap/envelope/")
})
public class EmpleadosResponseEnvelope {
    @Element(name = "Body", required = false)
    public EmpleadosResponseBody body;

    public EmpleadosResponseBody getBody() {
        return body;
    }

    public List<Empleado> getEmpleados() {
        if (body != null && body.empleadosResponse != null) {
            return body.empleadosResponse.empleados;
        }
        return null;
    }

    @Root(name = "Body", strict = false)
    public static class EmpleadosResponseBody {
        @Element(name = "traerEmpleadosResponse", required = false)
        public EmpleadosResponseData empleadosResponse;
    }

    @Root(name = "traerEmpleadosResponse", strict = false)
    public static class EmpleadosResponseData {
        @ElementList(name = "empleado", inline = true, required = false)
        @Namespace(reference = "http://ws.monster.edu.ec/")
        public List<Empleado> empleados;
    }

    @Root(name = "empleado", strict = false)
    public static class Empleado {
        @Element(name = "ciudad", required = false)
        public String ciudad;
        @Element(name = "codigo", required = false)
        public String codigo;
        @Element(name = "direccion", required = false)
        public String direccion;
        @Element(name = "materno", required = false)
        public String materno;
        @Element(name = "nombre", required = false)
        public String nombre;
        @Element(name = "paterno", required = false)
        public String paterno;

        public String getCodigo() { return codigo; }
        public String getNombre() { return nombre; }
        public String getPaterno() { return paterno; }
        public String getMaterno() { return materno; }
        public String getCiudad() { return ciudad; }
        public String getDireccion() { return direccion; }

        public String getNombreCompleto() {
            return nombre + " " + paterno;
        }
    }
}
