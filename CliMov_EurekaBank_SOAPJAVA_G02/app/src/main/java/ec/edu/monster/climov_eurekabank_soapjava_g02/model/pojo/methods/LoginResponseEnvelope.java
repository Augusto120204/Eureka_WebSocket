package ec.edu.monster.climov_eurekabank_soapjava_g02.model.pojo.methods;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.NamespaceList;
import org.simpleframework.xml.Root;

@Root(name = "Envelope", strict = false)
@NamespaceList({
        @Namespace(prefix = "S", reference = "http://schemas.xmlsoap.org/soap/envelope/")
})
public class LoginResponseEnvelope {
    @Element(name = "Body", required = false)
    public LoginResponseBody body;

    @Root(name = "Body", strict = false)
    public static class LoginResponseBody {
        @Element(name = "loginResponse", required = false)
        public LoginResponseData loginResponse;
    }

    @Root(name = "loginResponse", strict = false)
    public static class LoginResponseData {
        @Element(name = "login", required = false)
        public int result;
    }
}
