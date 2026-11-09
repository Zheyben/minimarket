package zheyben.minimarketaj.minimarket.dao;

import zheyben.minimarketaj.minimarket.modelo.Venta;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

// DAO: define las operaciones de acceso a datos de Venta
public interface VentaDAO {

    // Guarda la venta y devuelve el código generado por la base de datos
    int insertar(Venta venta);

    // Devuelve las ventas en orden cronológico
    List<Venta> listarTodas();

    Optional<Venta> buscarPorId(int idVenta);

    // Devuelve la última venta registrada en el sistema (la de código más alto)
    Optional<Venta> buscarUltima();

    // Marca la venta como anulada solo si sigue registrada y es la última venta.
    // Devuelve true si la anulación se realizó
    boolean anular(int idVenta, LocalDateTime fechaAnulacion, String motivo);
}
