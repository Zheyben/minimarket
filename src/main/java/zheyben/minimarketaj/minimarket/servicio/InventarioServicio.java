package zheyben.minimarketaj.minimarket.servicio;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zheyben.minimarketaj.minimarket.dao.ProductoDAO;
import zheyben.minimarketaj.minimarket.dto.DetalleCalculado;
import zheyben.minimarketaj.minimarket.excepcion.DatosInvalidosException;
import zheyben.minimarketaj.minimarket.excepcion.ProductoNoEncontradoException;
import zheyben.minimarketaj.minimarket.excepcion.StockInsuficienteException;
import zheyben.minimarketaj.minimarket.modelo.DetalleVenta;
import zheyben.minimarketaj.minimarket.modelo.Producto;

import java.util.List;

// Capa de lógica de negocio: CUS05 Descontar inventario y CUS07 Actualizar cantidades.
// También devuelve las unidades de una venta anulada (CUS08).
@Service
public class InventarioServicio {

    // Mayor cantidad disponible que se acepta para un producto
    private static final int CANTIDAD_MAXIMA = 999_999;

    private final ProductoDAO productoDAO;

    public InventarioServicio(ProductoDAO productoDAO) {
        this.productoDAO = productoDAO;
    }

    // CUS05: resta las unidades vendidas. Si un producto no tiene unidades suficientes,
    // lanza una excepción y la transacción de la venta se deshace por completo.
    @Transactional
    public void descontarInventario(List<DetalleCalculado> detalles) {
        detalles.forEach(this::descontarUnidades);
    }

    // CUS07: suma las unidades de la mercadería que llegó a la tienda
    @Transactional
    public Producto registrarIngreso(int idProducto, Integer unidades) {
        Producto producto = obtenerProducto(idProducto);
        if (unidades == null || unidades <= 0) {
            throw new DatosInvalidosException("Las unidades que llegaron deben ser mayores que cero.");
        }
        validarCantidadMaxima(producto.getCantidadDisponible() + (long) unidades);
        productoDAO.aumentarCantidad(idProducto, unidades);
        return obtenerProducto(idProducto);
    }

    // CUS07: reemplaza la cantidad disponible por la que el propietario contó en la tienda
    @Transactional
    public Producto ajustarCantidad(int idProducto, Integer cantidadContada) {
        obtenerProducto(idProducto);
        if (cantidadContada == null || cantidadContada < 0) {
            throw new DatosInvalidosException("La cantidad contada no puede ser negativa.");
        }
        validarCantidadMaxima(cantidadContada);
        productoDAO.fijarCantidad(idProducto, cantidadContada);
        return obtenerProducto(idProducto);
    }

    // CUS08: devuelve al inventario las unidades de una venta anulada
    @Transactional
    public void devolverUnidades(List<DetalleVenta> detalles) {
        detalles.forEach(detalle -> productoDAO.aumentarCantidad(detalle.getIdProducto(), detalle.getCantidad()));
    }

    private void descontarUnidades(DetalleCalculado detalle) {
        boolean descontado = productoDAO.descontarCantidad(detalle.idProducto(), detalle.cantidad());
        if (!descontado) {
            throw new StockInsuficienteException(detalle.nombreProducto());
        }
    }

    private Producto obtenerProducto(int idProducto) {
        return productoDAO.buscarPorId(idProducto)
                .orElseThrow(() -> new ProductoNoEncontradoException(idProducto));
    }

    private void validarCantidadMaxima(long cantidad) {
        if (cantidad > CANTIDAD_MAXIMA) {
            throw new DatosInvalidosException("La cantidad disponible no puede superar las 999 999 unidades.");
        }
    }
}
