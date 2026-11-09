package zheyben.minimarketaj.minimarket.dto;

import zheyben.minimarketaj.minimarket.modelo.Rol;

import java.io.Serializable;

// Datos del usuario que se guardan en la sesión después de iniciar sesión (sin la contraseña).
// Es Serializable porque el servidor puede guardar las sesiones en disco.
public record UsuarioSesion(int idUsuario, String nombreUsuario, Rol rol) implements Serializable {

    public boolean esPropietario() {
        return rol == Rol.PROPIETARIO;
    }
}
