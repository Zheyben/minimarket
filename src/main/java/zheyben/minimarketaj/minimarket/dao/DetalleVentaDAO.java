package zheyben.minimarketaj.minimarket.dao;

import zheyben.minimarketaj.minimarket.dto.DetalleHistorial;
import zheyben.minimarketaj.minimarket.modelo.DetalleVenta;

import java.util.List;

// DAO: define las operaciones de acceso a datos de DetalleVenta
public interface DetalleVentaDAO {

    void insertarTodos(List<DetalleVenta> detalles);

    // Devuelve los detalles de todas las ventas junto con el nombre de cada producto
    List<DetalleHistorial> listarConNombreProducto();
}
