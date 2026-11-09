package zheyben.minimarketaj.minimarket.controlador;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import zheyben.minimarketaj.minimarket.dto.UsuarioSesion;
import zheyben.minimarketaj.minimarket.excepcion.CredencialesInvalidasException;
import zheyben.minimarketaj.minimarket.seguridad.SesionUsuario;
import zheyben.minimarketaj.minimarket.servicio.AutenticacionServicio;

// Controlador: CUS01 Autenticarse en el sistema
@Controller
public class AutenticacionControlador {

    private static final String VISTA_LOGIN = "login";
    private static final String REDIRECCION_CATALOGO = "redirect:/catalogo";
    private static final String REDIRECCION_LOGIN = "redirect:/login";

    private final AutenticacionServicio autenticacionServicio;

    public AutenticacionControlador(AutenticacionServicio autenticacionServicio) {
        this.autenticacionServicio = autenticacionServicio;
    }

    @GetMapping("/login")
    public String mostrarInicioSesion() {
        return VISTA_LOGIN;
    }

    // CUS01, pasos 2 a 5: valida las credenciales y abre la sesión con el rol del usuario
    @PostMapping("/login")
    public String iniciarSesion(@RequestParam(required = false) String nombreUsuario,
                                @RequestParam(required = false) String contrasena,
                                HttpServletRequest solicitud,
                                Model modelo) {
        try {
            UsuarioSesion usuario = autenticacionServicio.autenticar(nombreUsuario, contrasena);
            SesionUsuario.iniciar(solicitud, usuario);
            return REDIRECCION_CATALOGO;
        } catch (CredencialesInvalidasException error) {
            return mostrarErrorDeCredenciales(modelo, nombreUsuario, error);
        }
    }

    // Permite que otro trabajador use el mismo celular con su propia cuenta y rol
    @PostMapping("/logout")
    public String cerrarSesion(HttpServletRequest solicitud) {
        SesionUsuario.cerrar(solicitud);
        return REDIRECCION_LOGIN;
    }

    // CUS01, flujo alternativo: informa el error y permite volver a ingresar los datos
    private String mostrarErrorDeCredenciales(Model modelo, String nombreUsuario,
                                              CredencialesInvalidasException error) {
        modelo.addAttribute("error", error.getMessage());
        modelo.addAttribute("nombreUsuario", nombreUsuario);
        return VISTA_LOGIN;
    }
}
