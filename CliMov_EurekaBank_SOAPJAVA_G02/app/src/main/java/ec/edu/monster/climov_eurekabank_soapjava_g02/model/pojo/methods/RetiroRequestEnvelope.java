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
public class RetiroRequestEnvelope {
    @Element(name = "Body")
    @Namespace(prefix = "soapenv", reference = "http://schemas.xmlsoap.org/soap/envelope/")
    private RetiroBody body;

    public RetiroRequestEnvelope(String cuenta, Double importe) {
        this.body = new RetiroBody(cuenta, importe);
    }

    @Root(name = "Body", strict = false)
    public static class RetiroBody {
        @Element(name = "regRetiro")
        @Namespace(prefix = "ws", reference = "http://ws.monster.edu.ec/")
        private RetiroData retiroData;

        public RetiroBody(String cuenta, Double importe) {
            this.retiroData = new RetiroData(cuenta, importe);
        }
    }

    @Root(name = "regRetiro", strict = false)
    public static class RetiroData {
        @Element(name = "cuenta", required = false)
        private String cuenta;

        @Element(name = "importe", required = false)
        private Double importe;

        public RetiroData(String cuenta, Double importe) {
            this.cuenta = cuenta;
            this.importe = importe;
        }
    }
}