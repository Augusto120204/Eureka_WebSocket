package ec.edu.monster.climov_eurekabank_soapjava_g02.model.pojo.methods;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.NamespaceList;
import org.simpleframework.xml.Root;

@Root(name = "Envelope", strict = false)
@NamespaceList({
        @Namespace(prefix = "soapenv", reference = "http://schemas.xmlsoap.org/soap/envelope/"),
        @Namespace(prefix = "ws", reference = "http://ws.monster.edu.ec/")
})
@Namespace(prefix = "soapenv", reference = "http://schemas.xmlsoap.org/soap/envelope/")
public class EmpleadosRequestEnvelope {

    @Element(name = "Body")
    @Namespace(prefix = "soapenv", reference = "http://schemas.xmlsoap.org/soap/envelope/")
    private EmpleadosBody body;

    public EmpleadosRequestEnvelope() {
        this.body = new EmpleadosBody();
    }

    @Root(name = "Body", strict = false)
    public static class EmpleadosBody {
        @Element(name = "traerEmpleados")
        @Namespace(prefix = "ws", reference = "http://ws.monster.edu.ec/")
        private EmpleadosData empleadosData;

        public EmpleadosBody() {
            this.empleadosData = new EmpleadosData();
        }
    }

    @Root(name = "traerEmpleados", strict = false)
    public static class EmpleadosData {
        // Sin parametros
    }
}
