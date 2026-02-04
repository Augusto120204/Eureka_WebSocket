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
public class LoginRequestEnvelope {

    @Element(name = "Body")
    @Namespace(prefix = "soapenv", reference = "http://schemas.xmlsoap.org/soap/envelope/")
    private LoginBody body;

    public LoginRequestEnvelope(String username, String password) {
        this.body = new LoginBody(username, password);
    }

    @Root(name = "Body", strict = false)
    public static class LoginBody {
        @Element(name = "login")
        @Namespace(prefix = "ws", reference = "http://ws.monster.edu.ec/")
        private LoginData loginData;

        public LoginBody(String username, String password) {
            this.loginData = new LoginData(username, password);
        }
    }

    @Root(name = "login", strict = false)
    public static class LoginData {
        @Element(name = "usuario", required = false)
        private String user;

        @Element(name = "contrasena", required = false)
        private String password;

        public LoginData(String user, String password) {
            this.user = user;
            this.password = password;
        }
    }
}