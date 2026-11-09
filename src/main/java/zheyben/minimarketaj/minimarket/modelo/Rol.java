package zheyben.minimarketaj.minimarket.modelo;

import java.util.Locale;

// Modelo: roles que puede tener un usuario del sistema (columna rol de la tabla Usuario)
public enum Rol {

    VENDEDOR,
    PROPIETARIO;

    // Convierte el texto guardado en la base de datos en un rol
    public static Rol desdeTexto(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalStateException("El usuario no tiene un rol asignado");
        }
        return Rol.valueOf(texto.trim().toUpperCase(Locale.ROOT));
    }
}
