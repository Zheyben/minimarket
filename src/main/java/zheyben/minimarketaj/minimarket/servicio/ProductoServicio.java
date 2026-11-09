package zheyben.minimarketaj.minimarket.servicio;

import org.springframework.stereotype.Service;
import zheyben.minimarketaj.minimarket.dao.ProductoDAO;
import zheyben.minimarketaj.minimarket.excepcion.DatosInvalidosException;
import zheyben.minimarketaj.minimarket.excepcion.ProductoNoEncontradoException;
import zheyben.minimarketaj.minimarket.modelo.Producto;

import java.math.BigDecimal;
import java.util.List;

// Capa de lógica de negocio: CUS02 Consultar catálogo de productos y CUS03 Actualizar precios
@Service
public class ProductoServicio {

    // Mayor valor que admite la columna precio DECIMAL(10,2)
    private static final BigDecimal PRECIO_MAXIMO = new BigDecimal("99999999.99");

    private static final int DECIMALES_PERMITIDOS = 2;

    private final ProductoDAO productoDAO;

    public ProductoServicio(ProductoDAO productoDAO) {
        this.productoDAO = productoDAO;
    }

    public List<Producto> listarCatalogo() {
        return productoDAO.listarTodos();
    }

    // CUS02: si no se escribe ningún texto, se muestra todo el catálogo
    public List<Producto> buscarProductos(String texto) {
        if (texto == null || texto.isBlank()) {
            return listarCatalogo();
        }
        return productoDAO.buscarPorNombre(texto.trim());
    }

    public Producto obtenerProducto(int idProducto) {
        return productoDAO.buscarPorId(idProducto)
                .orElseThrow(() -> new ProductoNoEncontradoException(idProducto));
    }

    // CUS03: el permiso exclusivo del propietario se controla antes de llegar aquí (RNF02)
    public void actualizarPrecio(int idProducto, BigDecimal nuevoPrecio) {
        obtenerProducto(idProducto);
        validarPrecio(nuevoPrecio);
        boolean actualizado = productoDAO.actualizarPrecio(idProducto, nuevoPrecio);
        if (!actualizado) {
            throw new ProductoNoEncontradoException(idProducto);
        }
    }

    private void validarPrecio(BigDecimal precio) {
        if (precio == null || precio.signum() <= 0) {
            throw new DatosInvalidosException("El precio debe ser mayor que cero.");
        }
        if (precio.stripTrailingZeros().scale() > DECIMALES_PERMITIDOS) {
            throw new DatosInvalidosException("El precio puede tener como máximo dos decimales.");
        }
        if (precio.compareTo(PRECIO_MAXIMO) > 0) {
            throw new DatosInvalidosException("El precio supera el valor máximo permitido.");
        }
    }
}
