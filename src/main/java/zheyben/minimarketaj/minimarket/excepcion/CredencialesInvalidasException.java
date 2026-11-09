package zheyben.minimarketaj.minimarket.excepcion;

// CUS01, flujo alternativo: el usuario o la contraseña no coinciden
public class CredencialesInvalidasException extends NegocioException {

    public CredencialesInvalidasException() {
        super("Usuario o contraseña incorrectos.");
    }
}
