package ec.edu.monster.climov_eurekabank_soapjava_g02.model.pojo.methods;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.NamespaceList;
import org.simpleframework.xml.Root;

import java.util.Collections;
import java.util.List;

@Root(name = "Envelope", strict = false)
@NamespaceList({
        @Namespace(prefix = "S", reference = "http://schemas.xmlsoap.org/soap/envelope/")
})
public class MovimientoResponseEnvelope {
    @Element(name = "Body", required = false)
    private MovimientoResponseBody body;

    public MovimientoResponseBody getBody() {
        return body;
    }

    // Constructor vacío requerido por Simple XML
    public MovimientoResponseEnvelope() {}

    public List<Movimiento> getMovimientos() {
        if (body != null && body.movimientoData != null) {
            return body.movimientoData.movimientos;
        }
        return Collections.emptyList();
    }

    @Root(name = "Body", strict = false)
    public static class MovimientoResponseBody {
        @Element(name = "traerMovimientosResponse")
        private MovimientoResponseData movimientoData;

        public MovimientoResponseData getMovimientoData() { return movimientoData; }

        // Constructor vacío requerido por Simple XML
        public MovimientoResponseBody() {}
    }

    @Root(name = "traerMovimientosResponse", strict = false)
    public static class MovimientoResponseData {
        // Usa @ElementList para capturar múltiples nodos <movimiento>
        // inline=true indica que <movimiento> es hijo directo de traerMovimientosResponse
        @ElementList(inline = true, name = "movimiento")
        @Namespace(reference = "http://ws.monster.edu.ec/")
        private List<Movimiento> movimientos;

        // Constructor vacío requerido por Simple XML
        public MovimientoResponseData() {}
    }

    //Se hace estatica la clase porque es un elemento repetitivo "In-line" dentro de la lista
    @Root(name = "movimiento", strict = false)
    public static class Movimiento {
        @Element(name = "accion")
        private String accion;

        @Element(name = "cuenta")
        private String cuenta;

        @Element(name = "fecha")
        private String fecha; // Formato ISO 8601 con zona horaria

        @Element(name = "importe")
        private double importe;

        @Element(name = "nromov")
        private int nromov;

        @Element(name = "tipo")
        private String tipo;

        public String getAccion() {
            return accion;
        }

        public void setAccion(String accion) {
            this.accion = accion;
        }

        public String getCuenta() {
            return cuenta;
        }

        public void setCuenta(String cuenta) {
            this.cuenta = cuenta;
        }

        public String getFecha() {
            return fecha;
        }

        public void setFecha(String fecha) {
            this.fecha = fecha;
        }

        public double getImporte() {
            return importe;
        }

        public void setImporte(double importe) {
            this.importe = importe;
        }

        public int getNromov() {
            return nromov;
        }

        public void setNromov(int nromov) {
            this.nromov = nromov;
        }

        public String getTipo() {
            return tipo;
        }

        public void setTipo(String tipo) {
            this.tipo = tipo;
        }

        // Constructor vacío requerido por Simple XML
        public Movimiento() {}
    }
}
