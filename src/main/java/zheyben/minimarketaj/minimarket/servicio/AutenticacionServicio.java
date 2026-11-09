package zheyben.minimarketaj.minimarket.servicio;

import org.springframework.stereotype.Service;
import zheyben.minimarketaj.minimarket.dao.UsuarioDAO;
import zheyben.minimarketaj.minimarket.dto.UsuarioSesion;
import zheyben.minimarketaj.minimarket.excepcion.CredencialesInvalidasException;
import zheyben.minimarketaj.minimarket.modelo.Rol;
import zheyben.minimarketaj.minimarket.modelo.Usuario;

// Capa de lógica de negocio: CUS01 Autenticarse en el sistema
@Service
public class AutenticacionServicio {

    private final UsuarioDAO usuarioDAO;

    public AutenticacionServicio(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    // Valida las credenciales y devuelve los datos del usuario con su rol
    public UsuarioSesion autenticar(String nombreUsuario, String contrasena) {
        validarDatosIngresados(nombreUsuario, contrasena);
        Usuario usuario = usuarioDAO.buscarPorNombreUsuario(nombreUsuario.trim())
                .filter(usuarioEncontrado -> contrasenaCoincide(usuarioEncontrado, contrasena))
                .orElseThrow(CredencialesInvalidasException::new);
        return crearUsuarioSesion(usuario);
    }

    private void validarDatosIngresados(String nombreUsuario, String contrasena) {
        if (estaVacio(nombreUsuario) || estaVacio(contrasena)) {
            throw new CredencialesInvalidasException();
        }
    }

    private boolean estaVacio(String texto) {
        return texto == null || texto.isBlank();
    }

    private boolean contrasenaCoincide(Usuario usuario, String contrasena) {
        return contrasena.equals(usuario.getContraseña());
    }

    private UsuarioSesion crearUsuarioSesion(Usuario usuario) {
        return new UsuarioSesion(
                usuario.getIdUsuario(),
                usuario.getNombreUsuario(),
                Rol.desdeTexto(usuario.getRol())
        );
    }
}
