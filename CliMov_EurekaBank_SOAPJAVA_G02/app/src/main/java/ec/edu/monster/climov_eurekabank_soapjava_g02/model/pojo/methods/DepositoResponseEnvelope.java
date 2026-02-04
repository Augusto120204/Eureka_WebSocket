package ec.edu.monster.climov_eurekabank_soapjava_g02.model.pojo.methods;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.NamespaceList;
import org.simpleframework.xml.Root;

@Root(name = "Envelope", strict = false)
@NamespaceList({
        @Namespace(prefix = "S", reference = "http://schemas.xmlsoap.org/soap/envelope/")
})
public class DepositoResponseEnvelope {
    @Element(name = "Body", required = false)
    private DepositoResponseEnvelope.DepositoResponseBody body;

    public double getResult() {
        // Retorna 0 si hay algún problema de parsing (caso extremadamente raro)
        return body != null && body.depositoData != null ? body.depositoData.resultado : 0;
    }

    @Root(name = "Body", strict = false)
    public static class DepositoResponseBody {
        @Element(name = "regDepositoResponse", required = false)
        private DepositoResponseEnvelope.DepositoResponseData depositoData;
    }

    @Root(name = "regDepositoResponse", strict = false)
    public static class DepositoResponseData {
        @Element(name = "resultado", required = false)
        private int resultado;
    }
}
