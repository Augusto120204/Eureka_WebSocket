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
public class CuentasRequestEnvelope {

    @Element(name = "Body")
    @Namespace(prefix = "soapenv", reference = "http://schemas.xmlsoap.org/soap/envelope/")
    private CuentasBody body;

    public CuentasRequestEnvelope() {
        this.body = new CuentasBody();
    }

    @Root(name = "Body", strict = false)
    public static class CuentasBody {
        @Element(name = "traerCuentas")
        @Namespace(prefix = "ws", reference = "http://ws.monster.edu.ec/")
        private CuentasData cuentasData;

        public CuentasBody() {
            this.cuentasData = new CuentasData();
        }
    }

    @Root(name = "traerCuentas", strict = false)
    public static class CuentasData {
        // Sin parametros
    }
}
