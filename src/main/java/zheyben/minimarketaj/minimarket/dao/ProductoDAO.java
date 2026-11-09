package zheyben.minimarketaj.minimarket.dao;

import zheyben.minimarketaj.minimarket.modelo.Producto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

// DAO: define las operaciones de acceso a datos de Producto
public interface ProductoDAO {

    List<Producto> listarTodos();

    List<Producto> buscarPorNombre(String texto);

    Optional<Producto> buscarPorId(int idProducto);

    // Devuelve true si se actualizó el precio del producto
    boolean actualizarPrecio(int idProducto, BigDecimal nuevoPrecio);

    // Resta unidades solo si hay suficientes. Devuelve true si el descuento se realizó
    boolean descontarCantidad(int idProducto, int cantidad);
}
