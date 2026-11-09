package zheyben.minimarketaj.minimarket.excepcion;

// CUS08, flujo alternativo: solo se puede anular la última venta y solo si sigue registrada
public class VentaNoAnulableException extends NegocioException {

    public VentaNoAnulableException() {
        super("Solo se puede anular la última venta registrada.");
    }
}
