package zheyben.minimarketaj.minimarket.controlador;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import zheyben.minimarketaj.minimarket.dto.UsuarioSesion;
import zheyben.minimarketaj.minimarket.seguridad.SesionUsuario;

// Datos que necesitan todas las vistas: el usuario que inició sesión.
// La cabecera lo usa para mostrar su nombre, su rol y el menú que le corresponde.
@ControllerAdvice
public class AtributosComunesVista {

    // Es null cuando nadie ha iniciado sesión (por ejemplo, en la pantalla de inicio de sesión)
    @ModelAttribute("usuarioActual")
    public UsuarioSesion usuarioActual(HttpServletRequest solicitud) {
        return SesionUsuario.obtener(solicitud).orElse(null);
    }
}
