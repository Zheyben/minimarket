package zheyben.minimarketaj.minimarket.seguridad;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import zheyben.minimarketaj.minimarket.dto.UsuarioSesion;

import java.io.IOException;

// RNF02: CUS03 Actualizar precios y CUS06 Consultar historial de ventas son exclusivos del propietario.
// El permiso se comprueba en el servidor, aunque alguien escriba la dirección directamente en el navegador.
@Component
public class PropietarioInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest solicitud, HttpServletResponse respuesta, Object controlador)
            throws IOException {
        if (esPropietario(solicitud)) {
            return true;
        }
        respuesta.sendError(HttpServletResponse.SC_FORBIDDEN, "Función exclusiva del propietario.");
        return false;
    }

    private boolean esPropietario(HttpServletRequest solicitud) {
        return SesionUsuario.obtener(solicitud)
                .map(UsuarioSesion::esPropietario)
                .orElse(false);
    }
}
