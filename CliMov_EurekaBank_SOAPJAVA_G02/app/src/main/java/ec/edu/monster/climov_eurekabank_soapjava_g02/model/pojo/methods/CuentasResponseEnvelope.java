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
public class CuentasResponseEnvelope {
    @Element(name = "Body", required = false)
    public CuentasResponseBody body;

    public CuentasResponseBody getBody() {
        return body;
    }

    public List<String> getCuentas() {
        if (body != null && body.cuentasResponse != null) {
            return body.cuentasResponse.cuentas;
        }
        return null;
    }

    @Root(name = "Body", strict = false)
    public static class CuentasResponseBody {
        @Element(name = "traerCuentasResponse", required = false)
        public CuentasResponseData cuentasResponse;
    }

    @Root(name = "traerCuentasResponse", strict = false)
    public static class CuentasResponseData {
        @ElementList(name = "traerCuentas", inline = true, required = false, entry = "traerCuentas")
        public List<String> cuentas;
    }
}
