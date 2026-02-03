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
public class TransferenciaRequestEnvelope {
    @Element(name = "Body")
    @Namespace(prefix = "soapenv", reference = "http://schemas.xmlsoap.org/soap/envelope/")
    private TransferenciaBody body;

    public TransferenciaRequestEnvelope(String cuentaOrigen, String cuentaDestino, Double importe) {
        this.body = new TransferenciaBody(cuentaOrigen, cuentaDestino, importe);
    }

    @Root(name = "Body", strict = false)
    public static class TransferenciaBody {
        @Element(name = "regTransferencia")
        @Namespace(prefix = "ws", reference = "http://ws.monster.edu.ec/")
        private TransferenciaData transferenciaData;

        public TransferenciaBody(String cuentaOrigen, String cuentaDestino, Double importe) {
            this.transferenciaData = new TransferenciaData(cuentaOrigen, cuentaDestino, importe);
        }
    }

    @Root(name = "regTransferencia", strict = false)
    public static class TransferenciaData {
        @Element(name = "cuentaOrigen", required = false)
        private String cuentaOrigen;

        @Element(name = "cuentaDestino", required = false)
        private String cuentaDestino;

        @Element(name = "importe", required = false)
        private Double importe;

        public TransferenciaData(String cuentaOrigen, String cuentaDestino, Double importe) {
            this.cuentaOrigen = cuentaOrigen;
            this.cuentaDestino = cuentaDestino;
            this.importe = importe;
        }
    }
}
