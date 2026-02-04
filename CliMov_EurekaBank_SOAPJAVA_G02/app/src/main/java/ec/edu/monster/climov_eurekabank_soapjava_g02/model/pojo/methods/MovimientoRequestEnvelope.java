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
public class MovimientoRequestEnvelope {
    @Element(name = "Body")
    @Namespace(prefix = "soapenv", reference = "http://schemas.xmlsoap.org/soap/envelope/")
    private MovimientoRequestEnvelope.MovimientoBody body;

    public MovimientoRequestEnvelope(String cuenta) {
        this.body = new MovimientoRequestEnvelope.MovimientoBody(cuenta);
    }

    @Root(name = "Body", strict = false)
    public static class MovimientoBody {
        @Element(name = "traerMovimientos")
        @Namespace(prefix = "ws", reference = "http://ws.monster.edu.ec/")
        private MovimientoRequestEnvelope.MovimientoData movimientoData;

        public MovimientoBody(String cuenta) {
            this.movimientoData = new MovimientoData(cuenta);
        }
    }

    @Root(name = "traerMovimientos", strict = false)
    public static class MovimientoData {
        @Element(name = "cuenta", required = false)
        private String cuenta;

        public MovimientoData(String cuenta) {
            this.cuenta = cuenta;
        }
    }
}
