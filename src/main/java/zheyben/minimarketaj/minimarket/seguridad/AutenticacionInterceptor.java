package zheyben.minimarketaj.minimarket.seguridad;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

// Precondición de los casos de uso CUS02 a CUS06: el actor debe haber iniciado sesión.
// Si no hay sesión activa, lo envía a la pantalla de inicio de sesión.
@Component
public class AutenticacionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest solicitud, HttpServletResponse respuesta, Object controlador)
            throws IOException {
        if (SesionUsuario.obtener(solicitud).isPresent()) {
            return true;
        }
        redirigirAInicioDeSesion(solicitud, respuesta);
        return false;
    }

    private void redirigirAInicioDeSesion(HttpServletRequest solicitud, HttpServletResponse respuesta)
            throws IOException {
        respuesta.sendRedirect(solicitud.getContextPath() + "/login");
    }
}
