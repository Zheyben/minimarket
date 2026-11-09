package zheyben.minimarketaj.minimarket.excepcion;

// Base de los errores esperados por las reglas del sistema (flujos alternativos de los casos de uso).
// Su mensaje está pensado para mostrarse al usuario.
public abstract class NegocioException extends RuntimeException {

    protected NegocioException(String mensaje) {
        super(mensaje);
    }
}
