package ec.edu.monster.climov_eurekabank_soapjava_g02.model.pojo.common;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import okhttp3.ResponseBody;

@Root(name = "Envelope", strict = false)
public class SoapFaultEnvelope {

    @Element(name = "Body", required = false)
    private FaultBody body;

    public SoapFaultEnvelope() {}

    public String getFaultMessage() {
        if (body != null && body.fault != null && body.fault.faultstring != null) {
            return body.fault.faultstring;
        }
        return "Error desconocido del servidor";
    }

    @Root(name = "Body", strict = false)
    public static class FaultBody {
        @Element(name = "Fault", required = false)
        private Fault fault;
    }

    @Root(name = "Fault", strict = false)
    public static class Fault {
        @Element(name = "faultcode", required = false)
        private String faultcode;

        @Element(name = "faultstring", required = false)
        private String faultstring;
    }
}

