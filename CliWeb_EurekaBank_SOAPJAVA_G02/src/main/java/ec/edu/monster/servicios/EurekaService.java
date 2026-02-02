package ec.edu.monster.servicios;

import ec.edu.monster.modelo.Usuario;
import ec.edu.monster.ws.conuni.Empleado;
import ec.edu.monster.ws.conuni.Movimiento;
import ec.edu.monster.ws.conuni.WSEureka;
import ec.edu.monster.ws.conuni.WSEureka_Service;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * Capa de servicio que consume el Web Service SOAP del backend.
 *
 * @author cesar
 */
@Service
public class EurekaService {
    private final WSEureka wsClient;
    
    public EurekaService(){
        WSEureka_Service service = new WSEureka_Service();
        wsClient = service.getWSEurekaPort();
    }
    
    /**
     * Autentica un usuario contra el backend.
     * @param usuario El objeto Usuario con credenciales.
     * @return true si la autenticación es exitosa, false en caso contrario.
     */
    public boolean login(Usuario usuario) {
        try {
            int resultado = wsClient.login(usuario.getUsuario(), usuario.getContrasena());
            
            // El backend devuelve 1 para éxito, -1 para fallo
            if(resultado == 1){
                usuario.setAutenticado(true);
            } else {
                usuario.setAutenticado(false);
            }
            return usuario.getAutenticado();
        } catch (Exception e) {
            System.err.println("Error al autenticar: " + e.getMessage());
            // Si hay un error de comunicación o SOAP Fault, asumimos fallo de autenticación.
            return false;
        }
    }

    /**
     * Obtiene el saldo actual de una cuenta.
     * @param cuenta El código de la cuenta.
     * @return El saldo de la cuenta.
     * @throws Exception Si la cuenta no existe/está activa o hay un error de comunicación.
     */
    public double traerMonto(String cuenta) throws Exception {
        try {
            return wsClient.traerMonto(cuenta);
        } catch (Exception e) {
            // El backend lanza RuntimeException con el mensaje de error de negocio (SOAP Fault).
            System.err.println("Error al obtener el monto: " + e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Obtiene la lista de movimientos de una cuenta.
     * @param cuenta El código de la cuenta.
     * @return Una lista de objetos Movimiento.
     * @throws Exception Si la cuenta no existe o hay un error de comunicación.
     */
    public List<Movimiento> traerMovimientos(String cuenta) throws Exception {
        try {
            // El cliente de JAX-WS mapea la respuesta automáticamente a List<Movimiento>
            List<Movimiento> lista = wsClient.traerMovimientos(cuenta);
            return lista != null ? lista : Collections.emptyList();
        } catch (Exception e) {
            System.err.println("Error al traer movimientos: " + e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Registra un depósito en una cuenta.
     * @param cuenta El código de la cuenta.
     * @param importe El monto a depositar.
     * @param codEmp El código del empleado.
     * @return true si la operación fue exitosa.
     * @throws Exception Si la operación falla (SOAP Fault con mensaje de error).
     */
    public boolean registrarDeposito(String cuenta, double importe, String codEmp) throws Exception {
        try {
            // regDeposito devuelve 1 en caso de éxito o lanza SOAP Fault
            int resultado = wsClient.regDeposito(cuenta, importe);
            return resultado == 1; 
        } catch (Exception e) {
            System.err.println("Error al registrar depósito: " + e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Registra un retiro de una cuenta.
     * @param cuenta El código de la cuenta.
     * @param importe El monto a retirar.
     * @return true si la operación fue exitosa.
     * @throws Exception Si el saldo es insuficiente o la operación falla.
     */
    public boolean registrarRetiro(String cuenta, double importe) throws Exception {
        // Se asume codEmp se maneja en el backend o se usa un valor fijo ("0001")
        try {
            // regRetiro devuelve 1 en caso de éxito o lanza SOAP Fault
            int resultado = wsClient.regRetiro(cuenta, importe);
            return resultado == 1;
        } catch (Exception e) {
            System.err.println("Error al registrar retiro: " + e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Registra una transferencia entre cuentas.
     * @param cuentaOrigen El código de la cuenta de donde sale el dinero.
     * @param cuentaDestino El código de la cuenta de destino.
     * @param importe El monto a transferir.
     * @return true si la operación fue exitosa.
     * @throws Exception Si la transferencia falla (ej. saldo insuficiente, cuenta inactiva).
     */
    public boolean registrarTransferencia(String cuentaOrigen, String cuentaDestino, double importe) throws Exception {
        // Se asume codEmp se maneja en el backend.
        try {
            // regTransferencia devuelve 1 en caso de éxito o lanza SOAP Fault
            int resultado = wsClient.regTransferencia(cuentaOrigen, cuentaDestino, importe);
            return resultado == 1;
        } catch (Exception e) {
            System.err.println("Error al registrar transferencia: " + e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Obtiene la lista de todas las cuentas disponibles.
     * @return Una lista de códigos de cuenta.
     * @throws Exception Si hay un error de comunicación.
     */
    public List<String> traerCuentas() throws Exception {
        try {
            List<String> lista = wsClient.traerCuentas();
            return lista != null ? lista : Collections.emptyList();
        } catch (Exception e) {
            System.err.println("Error al traer cuentas: " + e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Obtiene la lista de todos los empleados.
     * Excluye el empleado con código "9999".
     * @return Una lista de objetos Empleado.
     * @throws Exception Si hay un error de comunicación.
     */
    public List<Empleado> traerEmpleados() throws Exception {
        try {
            List<Empleado> lista = wsClient.traerEmpleados();
            if (lista != null) {
                // Filtrar para excluir el empleado con código 9999
                return lista.stream()
                    .filter(emp -> !"9999".equals(emp.getCodigo()))
                    .collect(Collectors.toList());
            }
            return Collections.emptyList();
        } catch (Exception e) {
            System.err.println("Error al traer empleados: " + e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    /**
     * Crea un nuevo empleado en el sistema.
     * @param codigo Código del empleado (4 caracteres)
     * @param paterno Apellido paterno
     * @param materno Apellido materno
     * @param nombre Nombre del empleado
     * @param ciudad Ciudad
     * @param direccion Dirección (opcional)
     * @return true si la operación fue exitosa.
     * @throws Exception Si la operación falla.
     */
    public boolean crearEmpleado(String codigo, String paterno, String materno, 
                                  String nombre, String ciudad, String direccion) throws Exception {
        try {
            int resultado = wsClient.crearEmpleado(codigo, paterno, materno, nombre, ciudad, direccion);
            return resultado == 1;
        } catch (Exception e) {
            System.err.println("Error al crear empleado: " + e.getMessage());
            throw new Exception(e.getMessage());
        }
    }
}