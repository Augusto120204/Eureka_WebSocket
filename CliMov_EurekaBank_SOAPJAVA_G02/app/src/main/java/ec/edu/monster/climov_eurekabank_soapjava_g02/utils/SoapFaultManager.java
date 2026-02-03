package ec.edu.monster.climov_eurekabank_soapjava_g02.utils;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import ec.edu.monster.climov_eurekabank_soapjava_g02.model.pojo.common.SoapFaultEnvelope;
import okhttp3.ResponseBody;

public class SoapFaultManager {
    static public String parseSoapFault(ResponseBody errorBody) {
        if (errorBody == null) {
            return "Error del servidor sin detalles";
        }

        try {
            String xmlError = errorBody.string();

            // Parsear el XML del Fault usando Simple XML
            Serializer serializer = new Persister();
            SoapFaultEnvelope faultEnvelope = serializer.read(SoapFaultEnvelope.class, xmlError);

            return faultEnvelope.getFaultMessage();

        } catch (Exception e) {
            // Si falla el parsing, retornar mensaje genérico
            return "Error del servidor (no se pudo leer el detalle)";
        }
    }
}
