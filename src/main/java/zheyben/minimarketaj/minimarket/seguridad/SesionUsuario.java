package zheyben.minimarketaj.minimarket.seguridad;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import zheyben.minimarketaj.minimarket.dto.UsuarioSesion;

import java.util.Optional;

// Centraliza cómo se guarda y se lee el usuario que inició sesión
public final class SesionUsuario {

    // Nombre con el que se guarda el usuario en la sesión (también se usa desde las vistas)
    public static final String ATRIBUTO_USUARIO = "usuarioSesion";

    private SesionUsuario() {
    }

    public static Optional<UsuarioSesion> obtener(HttpServletRequest solicitud) {
        HttpSession sesion = solicitud.getSession(false);
        if (sesion == null) {
            return Optional.empty();
        }
        return Optional.ofNullable((UsuarioSesion) sesion.getAttribute(ATRIBUTO_USUARIO));
    }

    // Se cambia el identificador de la sesión al iniciar sesión para evitar que se reutilice uno anterior
    public static void iniciar(HttpServletRequest solicitud, UsuarioSesion usuario) {
        HttpSession sesion = solicitud.getSession(true);
        solicitud.changeSessionId();
        sesion.setAttribute(ATRIBUTO_USUARIO, usuario);
    }

    public static void cerrar(HttpServletRequest solicitud) {
        HttpSession sesion = solicitud.getSession(false);
        if (sesion != null) {
            sesion.invalidate();
        }
    }
}
