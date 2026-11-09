package zheyben.minimarketaj.minimarket.excepcion;

// CUS04 y CUS05, flujo alternativo: no hay unidades suficientes de un producto
public class StockInsuficienteException extends NegocioException {

    public StockInsuficienteException(String nombreProducto, int cantidadDisponible) {
        super("Cantidad insuficiente de " + nombreProducto
                + ". Unidades disponibles: " + cantidadDisponible + ".");
    }

    public StockInsuficienteException(String nombreProducto) {
        super("Cantidad insuficiente de " + nombreProducto + ". La venta no se registró.");
    }
}
