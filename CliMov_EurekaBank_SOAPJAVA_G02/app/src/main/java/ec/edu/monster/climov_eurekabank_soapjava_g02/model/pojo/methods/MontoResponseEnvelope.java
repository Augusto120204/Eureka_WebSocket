package ec.edu.monster.climov_eurekabank_soapjava_g02.model.pojo.methods;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.NamespaceList;
import org.simpleframework.xml.Root;

@Root(name = "Envelope", strict = false)
@NamespaceList({
        @Namespace(prefix = "S", reference = "http://schemas.xmlsoap.org/soap/envelope/")
})
public class MontoResponseEnvelope {
    @Element(name = "Body", required = false)
    private MontoResponseBody body;

    public MontoResponseBody getBody() { return body;}

    public double getResult() {
        return body.montoData.monto;
    }

    @Root(name = "Body", strict = false)
    public static class MontoResponseBody {
        @Element(name = "traerMontoResponse", required = false)
        private MontoResponseData montoData;

        public MontoResponseData getMontoData() { return montoData; }
    }

    @Root(name = "traerMontoResponse", strict = false)
    public static class MontoResponseData {
        @Element(name = "monto", required = false)
        private double monto;
    }
}
