
package ec.edu.monster.ws.conuni;

import javax.xml.namespace.QName;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ec.edu.monster.ws.conuni package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _CrearEmpleado_QNAME = new QName("http://ws.monster.edu.ec/", "crearEmpleado");
    private final static QName _CrearEmpleadoResponse_QNAME = new QName("http://ws.monster.edu.ec/", "crearEmpleadoResponse");
    private final static QName _Empleado_QNAME = new QName("http://ws.monster.edu.ec/", "empleado");
    private final static QName _Login_QNAME = new QName("http://ws.monster.edu.ec/", "login");
    private final static QName _LoginResponse_QNAME = new QName("http://ws.monster.edu.ec/", "loginResponse");
    private final static QName _Movimiento_QNAME = new QName("http://ws.monster.edu.ec/", "movimiento");
    private final static QName _RegDeposito_QNAME = new QName("http://ws.monster.edu.ec/", "regDeposito");
    private final static QName _RegDepositoResponse_QNAME = new QName("http://ws.monster.edu.ec/", "regDepositoResponse");
    private final static QName _RegRetiro_QNAME = new QName("http://ws.monster.edu.ec/", "regRetiro");
    private final static QName _RegRetiroResponse_QNAME = new QName("http://ws.monster.edu.ec/", "regRetiroResponse");
    private final static QName _RegTransferencia_QNAME = new QName("http://ws.monster.edu.ec/", "regTransferencia");
    private final static QName _RegTransferenciaResponse_QNAME = new QName("http://ws.monster.edu.ec/", "regTransferenciaResponse");
    private final static QName _TraerCuentas_QNAME = new QName("http://ws.monster.edu.ec/", "traerCuentas");
    private final static QName _TraerCuentasResponse_QNAME = new QName("http://ws.monster.edu.ec/", "traerCuentasResponse");
    private final static QName _TraerEmpleados_QNAME = new QName("http://ws.monster.edu.ec/", "traerEmpleados");
    private final static QName _TraerEmpleadosResponse_QNAME = new QName("http://ws.monster.edu.ec/", "traerEmpleadosResponse");
    private final static QName _TraerMonto_QNAME = new QName("http://ws.monster.edu.ec/", "traerMonto");
    private final static QName _TraerMontoResponse_QNAME = new QName("http://ws.monster.edu.ec/", "traerMontoResponse");
    private final static QName _TraerMovimientos_QNAME = new QName("http://ws.monster.edu.ec/", "traerMovimientos");
    private final static QName _TraerMovimientosResponse_QNAME = new QName("http://ws.monster.edu.ec/", "traerMovimientosResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ec.edu.monster.ws.conuni
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link CrearEmpleado }
     * 
     */
    public CrearEmpleado createCrearEmpleado() {
        return new CrearEmpleado();
    }

    /**
     * Create an instance of {@link CrearEmpleadoResponse }
     * 
     */
    public CrearEmpleadoResponse createCrearEmpleadoResponse() {
        return new CrearEmpleadoResponse();
    }

    /**
     * Create an instance of {@link Empleado }
     * 
     */
    public Empleado createEmpleado() {
        return new Empleado();
    }

    /**
     * Create an instance of {@link Login }
     * 
     */
    public Login createLogin() {
        return new Login();
    }

    /**
     * Create an instance of {@link LoginResponse }
     * 
     */
    public LoginResponse createLoginResponse() {
        return new LoginResponse();
    }

    /**
     * Create an instance of {@link Movimiento }
     * 
     */
    public Movimiento createMovimiento() {
        return new Movimiento();
    }

    /**
     * Create an instance of {@link RegDeposito }
     * 
     */
    public RegDeposito createRegDeposito() {
        return new RegDeposito();
    }

    /**
     * Create an instance of {@link RegDepositoResponse }
     * 
     */
    public RegDepositoResponse createRegDepositoResponse() {
        return new RegDepositoResponse();
    }

    /**
     * Create an instance of {@link RegRetiro }
     * 
     */
    public RegRetiro createRegRetiro() {
        return new RegRetiro();
    }

    /**
     * Create an instance of {@link RegRetiroResponse }
     * 
     */
    public RegRetiroResponse createRegRetiroResponse() {
        return new RegRetiroResponse();
    }

    /**
     * Create an instance of {@link RegTransferencia }
     * 
     */
    public RegTransferencia createRegTransferencia() {
        return new RegTransferencia();
    }

    /**
     * Create an instance of {@link RegTransferenciaResponse }
     * 
     */
    public RegTransferenciaResponse createRegTransferenciaResponse() {
        return new RegTransferenciaResponse();
    }

    /**
     * Create an instance of {@link TraerCuentas }
     * 
     */
    public TraerCuentas createTraerCuentas() {
        return new TraerCuentas();
    }

    /**
     * Create an instance of {@link TraerCuentasResponse }
     * 
     */
    public TraerCuentasResponse createTraerCuentasResponse() {
        return new TraerCuentasResponse();
    }

    /**
     * Create an instance of {@link TraerEmpleados }
     * 
     */
    public TraerEmpleados createTraerEmpleados() {
        return new TraerEmpleados();
    }

    /**
     * Create an instance of {@link TraerEmpleadosResponse }
     * 
     */
    public TraerEmpleadosResponse createTraerEmpleadosResponse() {
        return new TraerEmpleadosResponse();
    }

    /**
     * Create an instance of {@link TraerMonto }
     * 
     */
    public TraerMonto createTraerMonto() {
        return new TraerMonto();
    }

    /**
     * Create an instance of {@link TraerMontoResponse }
     * 
     */
    public TraerMontoResponse createTraerMontoResponse() {
        return new TraerMontoResponse();
    }

    /**
     * Create an instance of {@link TraerMovimientos }
     * 
     */
    public TraerMovimientos createTraerMovimientos() {
        return new TraerMovimientos();
    }

    /**
     * Create an instance of {@link TraerMovimientosResponse }
     * 
     */
    public TraerMovimientosResponse createTraerMovimientosResponse() {
        return new TraerMovimientosResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CrearEmpleado }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link CrearEmpleado }{@code >}
     */
    @XmlElementDecl(namespace = "http://ws.monster.edu.ec/", name = "crearEmpleado")
    public JAXBElement<CrearEmpleado> createCrearEmpleado(CrearEmpleado value) {
        return new JAXBElement<CrearEmpleado>(_CrearEmpleado_QNAME, CrearEmpleado.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CrearEmpleadoResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link CrearEmpleadoResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://ws.monster.edu.ec/", name = "crearEmpleadoResponse")
    public JAXBElement<CrearEmpleadoResponse> createCrearEmpleadoResponse(CrearEmpleadoResponse value) {
        return new JAXBElement<CrearEmpleadoResponse>(_CrearEmpleadoResponse_QNAME, CrearEmpleadoResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Empleado }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Empleado }{@code >}
     */
    @XmlElementDecl(namespace = "http://ws.monster.edu.ec/", name = "empleado")
    public JAXBElement<Empleado> createEmpleado(Empleado value) {
        return new JAXBElement<Empleado>(_Empleado_QNAME, Empleado.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Login }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Login }{@code >}
     */
    @XmlElementDecl(namespace = "http://ws.monster.edu.ec/", name = "login")
    public JAXBElement<Login> createLogin(Login value) {
        return new JAXBElement<Login>(_Login_QNAME, Login.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LoginResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link LoginResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://ws.monster.edu.ec/", name = "loginResponse")
    public JAXBElement<LoginResponse> createLoginResponse(LoginResponse value) {
        return new JAXBElement<LoginResponse>(_LoginResponse_QNAME, LoginResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Movimiento }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link Movimiento }{@code >}
     */
    @XmlElementDecl(namespace = "http://ws.monster.edu.ec/", name = "movimiento")
    public JAXBElement<Movimiento> createMovimiento(Movimiento value) {
        return new JAXBElement<Movimiento>(_Movimiento_QNAME, Movimiento.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RegDeposito }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link RegDeposito }{@code >}
     */
    @XmlElementDecl(namespace = "http://ws.monster.edu.ec/", name = "regDeposito")
    public JAXBElement<RegDeposito> createRegDeposito(RegDeposito value) {
        return new JAXBElement<RegDeposito>(_RegDeposito_QNAME, RegDeposito.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RegDepositoResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link RegDepositoResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://ws.monster.edu.ec/", name = "regDepositoResponse")
    public JAXBElement<RegDepositoResponse> createRegDepositoResponse(RegDepositoResponse value) {
        return new JAXBElement<RegDepositoResponse>(_RegDepositoResponse_QNAME, RegDepositoResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RegRetiro }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link RegRetiro }{@code >}
     */
    @XmlElementDecl(namespace = "http://ws.monster.edu.ec/", name = "regRetiro")
    public JAXBElement<RegRetiro> createRegRetiro(RegRetiro value) {
        return new JAXBElement<RegRetiro>(_RegRetiro_QNAME, RegRetiro.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RegRetiroResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link RegRetiroResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://ws.monster.edu.ec/", name = "regRetiroResponse")
    public JAXBElement<RegRetiroResponse> createRegRetiroResponse(RegRetiroResponse value) {
        return new JAXBElement<RegRetiroResponse>(_RegRetiroResponse_QNAME, RegRetiroResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RegTransferencia }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link RegTransferencia }{@code >}
     */
    @XmlElementDecl(namespace = "http://ws.monster.edu.ec/", name = "regTransferencia")
    public JAXBElement<RegTransferencia> createRegTransferencia(RegTransferencia value) {
        return new JAXBElement<RegTransferencia>(_RegTransferencia_QNAME, RegTransferencia.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RegTransferenciaResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link RegTransferenciaResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://ws.monster.edu.ec/", name = "regTransferenciaResponse")
    public JAXBElement<RegTransferenciaResponse> createRegTransferenciaResponse(RegTransferenciaResponse value) {
        return new JAXBElement<RegTransferenciaResponse>(_RegTransferenciaResponse_QNAME, RegTransferenciaResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TraerCuentas }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link TraerCuentas }{@code >}
     */
    @XmlElementDecl(namespace = "http://ws.monster.edu.ec/", name = "traerCuentas")
    public JAXBElement<TraerCuentas> createTraerCuentas(TraerCuentas value) {
        return new JAXBElement<TraerCuentas>(_TraerCuentas_QNAME, TraerCuentas.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TraerCuentasResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link TraerCuentasResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://ws.monster.edu.ec/", name = "traerCuentasResponse")
    public JAXBElement<TraerCuentasResponse> createTraerCuentasResponse(TraerCuentasResponse value) {
        return new JAXBElement<TraerCuentasResponse>(_TraerCuentasResponse_QNAME, TraerCuentasResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TraerEmpleados }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link TraerEmpleados }{@code >}
     */
    @XmlElementDecl(namespace = "http://ws.monster.edu.ec/", name = "traerEmpleados")
    public JAXBElement<TraerEmpleados> createTraerEmpleados(TraerEmpleados value) {
        return new JAXBElement<TraerEmpleados>(_TraerEmpleados_QNAME, TraerEmpleados.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TraerEmpleadosResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link TraerEmpleadosResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://ws.monster.edu.ec/", name = "traerEmpleadosResponse")
    public JAXBElement<TraerEmpleadosResponse> createTraerEmpleadosResponse(TraerEmpleadosResponse value) {
        return new JAXBElement<TraerEmpleadosResponse>(_TraerEmpleadosResponse_QNAME, TraerEmpleadosResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TraerMonto }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link TraerMonto }{@code >}
     */
    @XmlElementDecl(namespace = "http://ws.monster.edu.ec/", name = "traerMonto")
    public JAXBElement<TraerMonto> createTraerMonto(TraerMonto value) {
        return new JAXBElement<TraerMonto>(_TraerMonto_QNAME, TraerMonto.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TraerMontoResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link TraerMontoResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://ws.monster.edu.ec/", name = "traerMontoResponse")
    public JAXBElement<TraerMontoResponse> createTraerMontoResponse(TraerMontoResponse value) {
        return new JAXBElement<TraerMontoResponse>(_TraerMontoResponse_QNAME, TraerMontoResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TraerMovimientos }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link TraerMovimientos }{@code >}
     */
    @XmlElementDecl(namespace = "http://ws.monster.edu.ec/", name = "traerMovimientos")
    public JAXBElement<TraerMovimientos> createTraerMovimientos(TraerMovimientos value) {
        return new JAXBElement<TraerMovimientos>(_TraerMovimientos_QNAME, TraerMovimientos.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TraerMovimientosResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link TraerMovimientosResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://ws.monster.edu.ec/", name = "traerMovimientosResponse")
    public JAXBElement<TraerMovimientosResponse> createTraerMovimientosResponse(TraerMovimientosResponse value) {
        return new JAXBElement<TraerMovimientosResponse>(_TraerMovimientosResponse_QNAME, TraerMovimientosResponse.class, null, value);
    }

}
