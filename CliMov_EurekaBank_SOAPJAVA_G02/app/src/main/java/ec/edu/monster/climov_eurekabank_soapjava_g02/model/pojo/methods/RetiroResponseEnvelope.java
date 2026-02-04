package ec.edu.monster.climov_eurekabank_soapjava_g02.model.pojo.methods;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.NamespaceList;
import org.simpleframework.xml.Root;

@Root(name = "Envelope", strict = false)
@NamespaceList({
        @Namespace(prefix = "S", reference = "http://schemas.xmlsoap.org/soap/envelope/")
})
public class RetiroResponseEnvelope{
    @Element(name = "Body", required = false)
    private RetiroResponseBody body;

    public double getResult() {
        // Retorna 0 si hay algún problema de parsing (caso extremadamente raro)
        return body != null && body.retiroData != null ? body.retiroData.resultado : 0;
    }

    @Root(name = "Body", strict = false)
    public static class RetiroResponseBody {
        @Element(name = "regRetiroResponse", required = false)
        private RetiroResponseData retiroData;
    }

    @Root(name = "regRetiroResponse", strict = false)
    public static class RetiroResponseData {
        @Element(name = "resultado", required = false)
        private int resultado;
    }
}