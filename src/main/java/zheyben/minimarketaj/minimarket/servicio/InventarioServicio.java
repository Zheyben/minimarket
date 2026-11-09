package zheyben.minimarketaj.minimarket.servicio;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zheyben.minimarketaj.minimarket.dao.ProductoDAO;
import zheyben.minimarketaj.minimarket.dto.DetalleCalculado;
import zheyben.minimarketaj.minimarket.excepcion.StockInsuficienteException;

import java.util.List;

// Capa de lógica de negocio: CUS05 Descontar inventario.
// Se ejecuta dentro del registro de una venta (CUS04), nunca por separado.
@Service
public class InventarioServicio {

    private final ProductoDAO productoDAO;

    public InventarioServicio(ProductoDAO productoDAO) {
        this.productoDAO = productoDAO;
    }

    // Resta las unidades vendidas. Si un producto no tiene unidades suficientes,
    // lanza una excepción y la transacción de la venta se deshace por completo.
    @Transactional
    public void descontarInventario(List<DetalleCalculado> detalles) {
        detalles.forEach(this::descontarUnidades);
    }

    private void descontarUnidades(DetalleCalculado detalle) {
        boolean descontado = productoDAO.descontarCantidad(detalle.idProducto(), detalle.cantidad());
        if (!descontado) {
            throw new StockInsuficienteException(detalle.nombreProducto());
        }
    }
}
