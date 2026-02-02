package ec.edu.monster.controladores;

import ec.edu.monster.modelo.OperacionViewModel;
import ec.edu.monster.modelo.CuentaViewModel;
import ec.edu.monster.modelos.MovimientoViewDTO;
import ec.edu.monster.servicios.EurekaService;
import ec.edu.monster.ws.conuni.Empleado;
import ec.edu.monster.ws.conuni.Movimiento;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
// Note: removed ModelAttribute import — using RequestParam binding for transfer form

@Controller
public class EurekabankController {

    private final EurekaService eurekaService;

    // --- Definiciones Estáticas de Tipos de Movimiento (Para la vista) ---
    private static final Map<String, String> TIPOS_MOVIMIENTOS = Map.ofEntries(
        Map.entry("001", "Apertura de Cuenta"),
        Map.entry("002", "Cancelar Cuenta"),
        Map.entry("003", "Depósito"),
        Map.entry("004", "Retiro"),
        Map.entry("005", "Interes"),
        Map.entry("006", "Mantenimiento"),
        Map.entry("007", "ITF"),
        Map.entry("008", "Transferencia Recibida"),
        Map.entry("009", "Transferencia Enviada"),
        Map.entry("010", "Cargo por Movimiento")
    );

    // Cantidad de cajeros por página
    private static final int CAJEROS_POR_PAGINA = 4;

    public EurekabankController(EurekaService eurekaService) {
        this.eurekaService = eurekaService;
    }

    // --- Filtro de Autenticación (Simple) ---
    private String checkAuth(HttpSession session) {
        Boolean isAuthenticated = (Boolean) session.getAttribute("isAuthenticated");
        if (isAuthenticated == null || !isAuthenticated) {
            return "redirect:/login";
        }
        return null; // Autenticación exitosa
    }

    // --- Verificar que hay cuenta seleccionada ---
    private String checkCuentaSeleccionada(HttpSession session) {
        String cuenta = (String) session.getAttribute("cuentaSeleccionada");
        if (cuenta == null || cuenta.isEmpty()) {
            return "redirect:/cuentas";
        }
        return null;
    }

    // --- GET: Página de Cajeros ---
    @GetMapping("/cajeros")
    public String mostrarCajeros(
            @RequestParam(value = "pagina", defaultValue = "1") int pagina,
            HttpSession session, 
            Model model) {
        String authCheck = checkAuth(session);
        if (authCheck != null) return authCheck;
        
        String usuario = (String) session.getAttribute("usuarioLogueado");
        model.addAttribute("usuarioLogueado", usuario != null ? usuario : "Usuario");
        
        try {
            // Obtener todos los empleados (el servicio ya filtra el 9999)
            List<Empleado> todosLosEmpleados = eurekaService.traerEmpleados();
            
            // Calcular paginación
            int totalEmpleados = todosLosEmpleados.size();
            int totalPaginas = (int) Math.ceil((double) totalEmpleados / CAJEROS_POR_PAGINA);
            
            // Validar número de página
            if (pagina < 1) pagina = 1;
            if (pagina > totalPaginas && totalPaginas > 0) pagina = totalPaginas;
            
            // Obtener empleados de la página actual
            int inicio = (pagina - 1) * CAJEROS_POR_PAGINA;
            int fin = Math.min(inicio + CAJEROS_POR_PAGINA, totalEmpleados);
            
            List<Empleado> empleadosPagina = todosLosEmpleados.subList(inicio, fin);
            
            model.addAttribute("empleados", empleadosPagina);
            model.addAttribute("paginaActual", pagina);
            model.addAttribute("totalPaginas", totalPaginas);
            model.addAttribute("totalEmpleados", totalEmpleados);
            
        } catch (Exception e) {
            model.addAttribute("error", "Error al obtener los cajeros: " + e.getMessage());
            model.addAttribute("empleados", Collections.emptyList());
            model.addAttribute("paginaActual", 1);
            model.addAttribute("totalPaginas", 0);
        }
        
        return "cajeros";
    }

    // --- GET: Seleccionar Cajero ---
    @GetMapping("/seleccionarCajero")
    public String seleccionarCajero(
            @RequestParam("cajeroId") String cajeroId,
            @RequestParam("cajeroNombre") String cajeroNombre,
            HttpSession session,
            Model model) {
        String authCheck = checkAuth(session);
        if (authCheck != null) return authCheck;
        
        // Guardar el cajero seleccionado en sesión
        session.setAttribute("cajeroId", cajeroId);
        session.setAttribute("cajeroNombre", cajeroNombre);
        
        return "redirect:/cuentas";
    }

    // --- GET: Formulario para crear empleado ---
    @GetMapping("/nuevoEmpleado")
    public String mostrarFormularioNuevoEmpleado(HttpSession session, Model model) {
        String authCheck = checkAuth(session);
        if (authCheck != null) return authCheck;
        
        String usuario = (String) session.getAttribute("usuarioLogueado");
        model.addAttribute("usuarioLogueado", usuario != null ? usuario : "Usuario");
        
        return "nuevoEmpleado";
    }

    // --- POST: Crear nuevo empleado ---
    @PostMapping("/crearEmpleado")
    public String crearEmpleado(
            @RequestParam("codigo") String codigo,
            @RequestParam("paterno") String paterno,
            @RequestParam("materno") String materno,
            @RequestParam("nombre") String nombre,
            @RequestParam("ciudad") String ciudad,
            @RequestParam(value = "direccion", required = false, defaultValue = "") String direccion,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        String authCheck = checkAuth(session);
        if (authCheck != null) return authCheck;
        
        // Validaciones básicas
        if (codigo == null || codigo.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "El código del empleado es obligatorio.");
            return "redirect:/nuevoEmpleado";
        }
        if (codigo.length() > 4) {
            redirectAttributes.addFlashAttribute("error", "El código no puede tener más de 4 caracteres.");
            return "redirect:/nuevoEmpleado";
        }
        if ("9999".equals(codigo)) {
            redirectAttributes.addFlashAttribute("error", "El código 9999 está reservado y no puede usarse.");
            return "redirect:/nuevoEmpleado";
        }
        
        try {
            eurekaService.crearEmpleado(codigo, paterno, materno, nombre, ciudad, direccion);
            redirectAttributes.addFlashAttribute("resultado", "Empleado creado exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear empleado: " + e.getMessage());
        }
        
        return "redirect:/cajeros";
    }

    // --- GET: Página de Cuentas ---
    @GetMapping("/cuentas")
    public String mostrarCuentas(HttpSession session, Model model) {
        String authCheck = checkAuth(session);
        if (authCheck != null) return authCheck;
        
        // Verificar que hay cajero seleccionado
        String cajeroId = (String) session.getAttribute("cajeroId");
        if (cajeroId == null || cajeroId.isEmpty()) {
            return "redirect:/cajeros";
        }
        
        String usuario = (String) session.getAttribute("usuarioLogueado");
        String cajeroNombre = (String) session.getAttribute("cajeroNombre");
        
        model.addAttribute("usuarioLogueado", usuario != null ? usuario : "Usuario");
        model.addAttribute("cajeroNombre", cajeroNombre != null ? cajeroNombre : "Cajero");
        
        try {
            List<String> cuentas = eurekaService.traerCuentas();
            model.addAttribute("cuentas", cuentas);
        } catch (Exception e) {
            model.addAttribute("error", "Error al obtener las cuentas: " + e.getMessage());
            model.addAttribute("cuentas", Collections.emptyList());
        }
        
        return "cuentas";
    }

    // --- GET: Seleccionar Cuenta ---
    @GetMapping("/seleccionarCuenta")
    public String seleccionarCuenta(
            @RequestParam("cuenta") String cuenta,
            HttpSession session) {
        String authCheck = checkAuth(session);
        if (authCheck != null) return authCheck;
        
        // Guardar la cuenta seleccionada en sesión
        session.setAttribute("cuentaSeleccionada", cuenta);
        
        return "redirect:/";
    }

    // --- GET: Index (Menú Principal) ---
    @GetMapping("/")
    public String mostrarIndex(HttpSession session, Model model) {
        String authCheck = checkAuth(session);
        if (authCheck != null) return authCheck;
        
        // Verificar que hay cuenta seleccionada
        String cuentaCheck = checkCuentaSeleccionada(session);
        if (cuentaCheck != null) return cuentaCheck;
        
        String cuentaSeleccionada = (String) session.getAttribute("cuentaSeleccionada");
        String cajeroNombre = (String) session.getAttribute("cajeroNombre");
        String usuario = (String) session.getAttribute("usuarioLogueado");
        
        model.addAttribute("cuentaSeleccionada", cuentaSeleccionada);
        model.addAttribute("cajeroNombre", cajeroNombre);
        model.addAttribute("usuarioLogueado", usuario);
        
        return "index"; // Retorna la vista index.html (Menú Principal)
    }

    // --- GET/POST: Ver Movimientos ---
    
    @GetMapping("/verMovimientos")
    public String mostrarVerMovimientos(HttpSession session, Model model) {
        String authCheck = checkAuth(session);
        if (authCheck != null) return authCheck;
        
        // Verificar que hay cuenta seleccionada
        String cuentaCheck = checkCuentaSeleccionada(session);
        if (cuentaCheck != null) return cuentaCheck;
        
        // Pasa el mapa de tipos de movimiento a la vista
        model.addAttribute("tiposMovimientos", TIPOS_MOVIMIENTOS);
        
        // Pasar la cuenta seleccionada
        String cuentaSeleccionada = (String) session.getAttribute("cuentaSeleccionada");
        String cajeroNombre = (String) session.getAttribute("cajeroNombre");
        model.addAttribute("cuentaSeleccionada", cuentaSeleccionada);
        model.addAttribute("cajeroNombre", cajeroNombre);
        
        
        // Inicializa el modelo de cuenta
        if (!model.containsAttribute("cuentaModel")) {
             model.addAttribute("cuentaModel", new CuentaViewModel());
        }
        return "verMovimientos";
    }

    @PostMapping("/consultarMovimientos")
    public String consultarMovimientos(
            @RequestParam("cuentaCodigo") String cuentaCodigo,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (cuentaCodigo == null || cuentaCodigo.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Debe ingresar un código de cuenta.");
            return "redirect:/verMovimientos";
        }

        CuentaViewModel model = new CuentaViewModel();
        model.setCuentaCodigo(cuentaCodigo);
        
        try {
            model.setMontoActual(eurekaService.traerMonto(cuentaCodigo));

            List<Movimiento> movimientosSOAP = eurekaService.traerMovimientos(cuentaCodigo);

            // --- CONVERSIÓN CRÍTICA: Mapear y convertir la fecha ---
            List<MovimientoViewDTO> movimientosParaVista = movimientosSOAP.stream()
                .map(mov -> {
                    MovimientoViewDTO dto = new MovimientoViewDTO();
                    dto.setNromov(mov.getNromov());
                    dto.setTipo(mov.getTipo());
                    dto.setAccion(mov.getAccion()); // Asegúrate de que el modelo SOAP tiene este getter
                    dto.setImporte(mov.getImporte());

                    // CONVERSIÓN SEGURA: Convierte XMLGregorianCalendar a java.util.Date
                    if (mov.getFecha() != null) {
                        dto.setFecha(mov.getFecha().toGregorianCalendar().getTime());
                    }
                    return dto;
                })
                .collect(Collectors.toList());
            // --------------------------------------------------------

            model.setMovimientos(movimientosParaVista); // Ahora el modelo tiene List<MovimientoViewDTO>

            if (movimientosParaVista.isEmpty()) {
                 model.setMensajeError("No se encontraron movimientos para esta cuenta.");
            }

        } catch (Exception ex) {
            model.setMensajeError(ex.getMessage());
        }

        redirectAttributes.addFlashAttribute("cuentaModel", model);
        return "redirect:/verMovimientos";
    }
    
    // --- GET/POST: Depósito ---
    
    @GetMapping("/deposito")
    public String mostrarDeposito(HttpSession session, Model model) {
        String authCheck = checkAuth(session);
        if (authCheck != null) return authCheck;
        
        // Verificar que hay cuenta seleccionada
        String cuentaCheck = checkCuentaSeleccionada(session);
        if (cuentaCheck != null) return cuentaCheck;
        
        String cuentaSeleccionada = (String) session.getAttribute("cuentaSeleccionada");
        String cajeroNombre = (String) session.getAttribute("cajeroNombre");
        model.addAttribute("cuentaSeleccionada", cuentaSeleccionada);
        model.addAttribute("cajeroNombre", cajeroNombre);
        
        if (!model.containsAttribute("operacionModel")) {
             model.addAttribute("operacionModel", new OperacionViewModel());
        }
        return "deposito";
    }

    @PostMapping("/hacerDeposito")
    public String hacerDeposito(
            @RequestParam("cuenta") String cuenta,
            @RequestParam("importe") double importe,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (cuenta.isEmpty() || importe <= 0) {
            redirectAttributes.addFlashAttribute("error", "Datos inválidos: Cuenta e importe positivo requeridos.");
            return "redirect:/deposito";
        }

        try {
            // Se asume un codEmp fijo o se obtiene de la sesión (ej. "0001")
            String codEmp = "0001"; 
            eurekaService.registrarDeposito(cuenta, importe, codEmp);
            redirectAttributes.addFlashAttribute("resultado", "Depósito de " + importe + " realizado con éxito.");
            
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "Fallo en el depósito: " + ex.getMessage());
        }
        return "redirect:/deposito";
    }

    // --- GET/POST: Retiro ---

    @GetMapping("/retiro")
    public String mostrarRetiro(HttpSession session, Model model) {
        String authCheck = checkAuth(session);
        if (authCheck != null) return authCheck;
        
        // Verificar que hay cuenta seleccionada
        String cuentaCheck = checkCuentaSeleccionada(session);
        if (cuentaCheck != null) return cuentaCheck;
        
        String cuentaSeleccionada = (String) session.getAttribute("cuentaSeleccionada");
        String cajeroNombre = (String) session.getAttribute("cajeroNombre");
        model.addAttribute("cuentaSeleccionada", cuentaSeleccionada);
        model.addAttribute("cajeroNombre", cajeroNombre);

        if (!model.containsAttribute("operacionModel")) {
             model.addAttribute("operacionModel", new OperacionViewModel());
        }
        return "retiro";
    }

    @PostMapping("/hacerRetiro")
    public String hacerRetiro(
            @RequestParam("cuenta") String cuenta,
            @RequestParam("importe") double importe,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (cuenta.isEmpty() || importe <= 0) {
            redirectAttributes.addFlashAttribute("error", "Datos inválidos: Cuenta e importe positivo requeridos.");
            return "redirect:/retiro";
        }

        try {
            String codEmp = "0001";
            eurekaService.registrarRetiro(cuenta, importe);
            redirectAttributes.addFlashAttribute("resultado", "Retiro de " + importe + " realizado con éxito.");
            
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "Fallo en el retiro: " + ex.getMessage());
        }
        return "redirect:/retiro";
    }

    // --- GET/POST: Transferencia ---
    
    @GetMapping("/transferencia")
    public String mostrarTransferencia(HttpSession session, Model model) {
        String authCheck = checkAuth(session);
        if (authCheck != null) return authCheck;
        
        // Verificar que hay cuenta seleccionada
        String cuentaCheck = checkCuentaSeleccionada(session);
        if (cuentaCheck != null) return cuentaCheck;
        
        String cuentaSeleccionada = (String) session.getAttribute("cuentaSeleccionada");
        String cajeroNombre = (String) session.getAttribute("cajeroNombre");
        model.addAttribute("cuentaSeleccionada", cuentaSeleccionada);
        model.addAttribute("cajeroNombre", cajeroNombre);

        if (!model.containsAttribute("operacionModel")) {
             model.addAttribute("operacionModel", new OperacionViewModel());
        }
        return "transferencia";
    }

    @PostMapping("/hacerTransferencia")
    public String hacerTransferencia(
            @RequestParam("cuenta") String cuentaOrigen,
            @RequestParam("cuentaDestino") String cuentaDestino,
            @RequestParam("importe") double importe,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        if (cuentaOrigen == null || cuentaOrigen.isEmpty() || cuentaDestino == null || cuentaDestino.isEmpty() || importe <= 0) {
            redirectAttributes.addFlashAttribute("error", "Datos inválidos: Cuentas e importe positivo requeridos.");
            return "redirect:/transferencia";
        }
        if (cuentaOrigen.equals(cuentaDestino)) {
            redirectAttributes.addFlashAttribute("error", "Error: La cuenta origen y destino no pueden ser la misma.");
            return "redirect:/transferencia";
        }

        try {
            String codEmp = "0001";
            eurekaService.registrarTransferencia(cuentaOrigen, cuentaDestino, importe);
            redirectAttributes.addFlashAttribute("resultado", "Transferencia de " + importe + " a " + cuentaDestino + " realizada con éxito.");

        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "Fallo en la transferencia: " + ex.getMessage());
        }
        return "redirect:/transferencia";
    }
}