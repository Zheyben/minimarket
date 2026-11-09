package zheyben.minimarketaj.minimarket.excepcion;

// El producto solicitado no existe en el catálogo
public class ProductoNoEncontradoException extends NegocioException {

    public ProductoNoEncontradoException(int idProducto) {
        super("No se encontró el producto con código " + idProducto + ".");
    }
}
