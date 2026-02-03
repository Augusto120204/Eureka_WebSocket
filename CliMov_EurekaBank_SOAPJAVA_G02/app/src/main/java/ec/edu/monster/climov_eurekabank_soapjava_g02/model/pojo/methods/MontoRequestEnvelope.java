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
public class MontoRequestEnvelope {
    @Element(name = "Body")
    @Namespace(prefix = "soapenv", reference = "http://schemas.xmlsoap.org/soap/envelope/")
    private MontoBody body;

    public MontoRequestEnvelope(String cuenta) {
        this.body = new MontoBody(cuenta);
    }

    @Root(name = "Body", strict = false)
    public static class MontoBody {
        @Element(name = "traerMonto")
        @Namespace(prefix = "ws", reference = "http://ws.monster.edu.ec/")
        private MontoData montoData;

        public MontoBody(String cuenta) {
            this.montoData = new MontoData(cuenta);
        }
    }

    @Root(name = "traerMonto", strict = false)
    public static class MontoData {
        @Element(name = "cuenta", required = false)
        private String cuenta;

        public MontoData(String cuenta) {
            this.cuenta = cuenta;
        }
    }
}
