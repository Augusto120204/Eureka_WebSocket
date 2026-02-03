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
public class DepositoRequestEnvelope {
    @Element(name = "Body")
    @Namespace(prefix = "soapenv", reference = "http://schemas.xmlsoap.org/soap/envelope/")
    private DepositoRequestEnvelope.DepositoBody body;

    public DepositoRequestEnvelope(String cuenta, Double importe) {
        this.body = new DepositoRequestEnvelope.DepositoBody(cuenta, importe);
    }

    @Root(name = "Body", strict = false)
    public static class DepositoBody {
        @Element(name = "regDeposito")
        @Namespace(prefix = "ws", reference = "http://ws.monster.edu.ec/")
        private DepositoRequestEnvelope.DepositoData depositoData;

        public DepositoBody(String cuenta, Double importe) {
            this.depositoData = new DepositoRequestEnvelope.DepositoData(cuenta, importe);
        }
    }

    @Root(name = "regDeposito", strict = false)
    public static class DepositoData {
        @Element(name = "cuenta", required = false)
        private String cuenta;

        @Element(name = "importe", required = false)
        private Double importe;

        public DepositoData(String cuenta, Double importe) {
            this.cuenta = cuenta;
            this.importe = importe;
        }
    }
}
