package ec.edu.monster.controladores;

import ec.edu.monster.modelo.Usuario;
import ec.edu.monster.servicios.EurekaService; // Asumo que usaremos el nuevo EurekaService
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    // Se asume inyección del servicio para consumir el backend SOAP
    private final EurekaService eurekaService;

    public LoginController(EurekaService eurekaService) {
        this.eurekaService = eurekaService;
    }

    /**
     * GET: Muestra la página de inicio de sesión.
     */
    @GetMapping("/login")
    public String mostrarPaginaLogin(HttpSession session) {
        // Limpia el indicador de sesión al cargar la página de login
        session.removeAttribute("isAuthenticated");
        return "login"; // Retorna la vista login.html
    }

    /**
     * POST: Maneja la autenticación del usuario.
     */
    @PostMapping("/login")
    public String manejarLogin(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpSession session,
            Model model) {

        Usuario usuario = new Usuario(username, password);
        
        try {
            // Llama al servicio para autenticar
            boolean autenticado = eurekaService.login(usuario); 

            if (autenticado) {
                // Redirige a la pantalla de cajeros y marca la sesión como autenticada
                session.setAttribute("isAuthenticated", true);
                // Guardamos el usuario por si acaso, aunque no tenga cuenta fija
                session.setAttribute("usuarioLogueado", username); 
                return "redirect:/cajeros";
            } else {
                model.addAttribute("error", "Usuario o contraseña inválidos.");
                return "login";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Error de comunicación: " + e.getMessage());
            return "login";
        }
    }
    
    /**
     * POST: Cierra la sesión y redirige al login.
     */
    @PostMapping("/logout")
    public String manejarLogout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}