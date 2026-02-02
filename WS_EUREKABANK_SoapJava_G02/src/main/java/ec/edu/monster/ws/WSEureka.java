/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.ws;

import ec.edu.monster.controlador.Login;
import ec.edu.monster.modelo.Movimiento;
import ec.edu.monster.modelo.ResultadoOperacion;
import ec.edu.monster.servicio.EurekaService;
import java.util.ArrayList;
import java.util.List;
import jakarta.jws.WebService;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;

/**
 *
 * @author leito
 */
@WebService(serviceName = "WSEureka")
public class WSEureka {

    /**
     * Web service operation
     * @return Retorna la lista de todos los números de cuenta activos del sistema
     */
    @WebMethod(operationName = "traerCuentas")
    @WebResult(name = "cuenta")
    public List<String> traerCuentas() {
        try {
            EurekaService service = new EurekaService();
            return service.obtenerTodasLasCuentas();
        } catch (RuntimeException e) {
            System.err.println("Error en traerCuentas: " + e.getMessage());
            throw new RuntimeException("Error al consultar cuentas: " + e.getMessage());
        }
    }

    /**
     * Web service operation
     * @param cuenta
     * @return Retorna la lista de movimientos de una cuenta
     */
    @WebMethod(operationName = "traerMovimientos")
    @WebResult(name = "movimiento")
    public List<Movimiento> traerMovimientos(@WebParam(name = "cuenta") String cuenta) {
        try {
            EurekaService service = new EurekaService();
            return service.leerMovimientos(cuenta);
        } catch (RuntimeException e) { // Capturar la excepción del servicio
            // Opcional: Loguear el error, luego relanzar
            System.err.println("Error en traerMovimientos: " + e.getMessage());
            // Lanza una RuntimeException, que generalmente se convierte en un SOAP Fault
            throw new RuntimeException("Error al consultar movimientos: " + e.getMessage()); 
        }
    }
    
    @WebMethod(operationName = "traerMonto")
    @WebResult(name = "monto")
    public double traerMonto(@WebParam(name = "cuenta") String cuenta) {
        try {
            // Instancia del servicio de negocio
            EurekaService service = new EurekaService();
            
            // Llamada al método de servicio
            return service.getMonto(cuenta);
            
        } catch (RuntimeException e) { 
            // Captura las excepciones de la capa de servicio (por ejemplo, "cuenta no existe o no está activa")
            System.err.println("Error al obtener el monto: " + e.getMessage());
            
            // Relanza como RuntimeException. En JAX-WS, esto genera un SOAP Fault,
            // lo que permite al cliente saber que la operación falló.
            throw new RuntimeException("Fallo al consultar el saldo: " + e.getMessage());
        }
    }

    /**
     * Web service operation
     * @param cuenta
     * @param importe
     * @return Estado, 1 o -1
     */
    @WebMethod(operationName = "regDeposito")
    @WebResult(name = "resultado")
    public int regDeposito(@WebParam(name = "cuenta") String cuenta, @WebParam(name = "importe") double importe) {
        // ... validaciones ...
        String codEmp = "0001";
        try {
            EurekaService service = new EurekaService();
            service.registrarDeposito(cuenta, importe, codEmp);
            return 1;
        } catch (RuntimeException e) { 
            // Esto capturará los errores lanzados en EurekaService, incluyendo el de la cuenta inactiva.
            System.err.println("Error al registrar el depósito: " + e.getMessage());
            // Lanza una nueva RuntimeException o la original, para generar el SOAP Fault
            throw new RuntimeException("Fallo en el depósito: " + e.getMessage()); 
        }
    }
    
    @WebMethod(operationName = "login")
    @WebResult(name = "login")
    public int iniciarSesion(@WebParam(name = "usuario") String usuario, @WebParam(name = "contrasena") String contrasena) {
        try {
            Login service = new Login();
            
            if(service.IniciarSesion(usuario, contrasena)){
                return 1;
            }else{
                return -1;
            }
        } catch (Exception e) {
            // Registrar el error para diagnóstico
            System.err.println("Error al iniciar sesión: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    @WebMethod(operationName = "regRetiro")
    @WebResult(name = "resultado")
    public int regRetiro(@WebParam(name = "cuenta") String cuenta, @WebParam(name = "importe") double importe) {
        // Validación de parámetros mínima
        if (importe <= 0) {
            throw new RuntimeException("El importe para el retiro debe ser positivo.");
        }
        
        String codEmp = "0001";
        try {
            EurekaService service = new EurekaService();
            service.registrarRetiro(cuenta, importe, codEmp);
            return 1; // Éxito
        } catch (RuntimeException e) { 
            // Captura errores como "saldo insuficiente" o "cuenta no activa"
            System.err.println("Error al registrar el retiro: " + e.getMessage());
            throw new RuntimeException("Fallo en el retiro: " + e.getMessage()); 
        }
    }

    @WebMethod(operationName = "regTransferencia")
    @WebResult(name = "resultado")
    public int regTransferencia(
        @WebParam(name = "cuentaOrigen") String cuentaOrigen, 
        @WebParam(name = "cuentaDestino") String cuentaDestino, 
        @WebParam(name = "importe") double importe) {
            
        // Validación de parámetros mínima
        if (importe <= 0) {
            throw new RuntimeException("El importe para la transferencia debe ser positivo.");
        }
        
        // Evitar transferencia a la misma cuenta
        if (cuentaOrigen.equals(cuentaDestino)) {
            throw new RuntimeException("La cuenta origen y destino no pueden ser la misma.");
        }

        String codEmp = "0001";
        try {
            EurekaService service = new EurekaService();
            service.registrarTransferencia(cuentaOrigen, cuentaDestino, importe, codEmp);
            return 1; // Éxito
        } catch (RuntimeException e) { 
            // Captura errores como "saldo insuficiente" o fallos en cuentas
            System.err.println("Error al registrar la transferencia: " + e.getMessage());
            throw new RuntimeException("Fallo en la transferencia: " + e.getMessage()); 
        }
    }
}
