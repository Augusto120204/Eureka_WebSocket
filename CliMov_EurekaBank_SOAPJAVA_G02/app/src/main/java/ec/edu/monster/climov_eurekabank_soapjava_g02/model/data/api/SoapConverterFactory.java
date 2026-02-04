package ec.edu.monster.climov_eurekabank_soapjava_g02.model.data.api;

import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.stream.Format;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

public class SoapConverterFactory extends Converter.Factory {
    private final Persister persister;

    public SoapConverterFactory() {
        Format format = new Format("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        this.persister = new Persister(new AnnotationStrategy(), format);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type,
                                                          Annotation[] parameterAnnotations,
                                                          Annotation[] methodAnnotations,
                                                          Retrofit retrofit) {
        return new Converter<Object, RequestBody>() {
            @Override
            public RequestBody convert(Object value) throws IOException {
                try {
                    java.io.StringWriter writer = new java.io.StringWriter();
                    persister.write(value, writer);
                    String xml = writer.toString();
                    return RequestBody.create(MediaType.parse("text/xml; charset=utf-8"), xml);
                } catch (Exception e) {
                    throw new IOException("Error serializing SOAP request", e);
                }
            }
        };
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type,
                                                            Annotation[] annotations,
                                                            Retrofit retrofit) {
        return new Converter<ResponseBody, Object>() {
            @Override
            public Object convert(ResponseBody value) throws IOException {
                try {
                    String xml = value.string();
                    return persister.read((Class<?>) type, xml);
                } catch (Exception e) {
                    throw new IOException("Error deserializing SOAP response", e);
                }
            }
        };
    }
}
