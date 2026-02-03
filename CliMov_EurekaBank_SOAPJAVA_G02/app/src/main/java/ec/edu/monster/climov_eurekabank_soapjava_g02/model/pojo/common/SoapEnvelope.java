package ec.edu.monster.climov_eurekabank_soapjava_g02.model.pojo.common;

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
public class SoapEnvelope<T> {

    @Element(name = "Body", required = false)
    @Namespace(prefix = "soapenv", reference = "http://schemas.xmlsoap.org/soap/envelope/")
    public Body<T> body;

    public SoapEnvelope() {}

    public SoapEnvelope(T content) {
        this.body = new Body<>(content);
    }

    @Root(name = "Body", strict = false)
    public static class Body<T> {
        @Element(required = false)
        private T content;

        public Body() {}

        public Body(T content) {
            this.content = content;
        }

        public T getContent() {
            return content;
        }
    }
}
