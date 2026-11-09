package zheyben.minimarketaj.minimarket.excepcion;

// Los datos ingresados no cumplen las reglas del sistema (por ejemplo, un precio o una cantidad no válidos)
public class DatosInvalidosException extends NegocioException {

    public DatosInvalidosException(String mensaje) {
        super(mensaje);
    }
}
