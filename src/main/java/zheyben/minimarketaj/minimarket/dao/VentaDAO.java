package zheyben.minimarketaj.minimarket.dao;

import zheyben.minimarketaj.minimarket.modelo.Venta;

import java.util.List;

// DAO: define las operaciones de acceso a datos de Venta
public interface VentaDAO {

    // Guarda la venta y devuelve el código generado por la base de datos
    int insertar(Venta venta);

    // Devuelve las ventas en orden cronológico
    List<Venta> listarTodas();
}
