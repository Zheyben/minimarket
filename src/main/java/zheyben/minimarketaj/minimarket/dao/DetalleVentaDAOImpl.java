package zheyben.minimarketaj.minimarket.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import zheyben.minimarketaj.minimarket.dto.DetalleHistorial;
import zheyben.minimarketaj.minimarket.modelo.DetalleVenta;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

// DAO: implementa el acceso a la tabla DetalleVenta mediante JDBC
@Repository
public class DetalleVentaDAOImpl implements DetalleVentaDAO {

    private static final String SQL_INSERTAR =
            "INSERT INTO DetalleVenta (idVenta, idProducto, cantidad, subtotal) VALUES (?, ?, ?, ?)";

    private static final String SQL_LISTAR_CON_NOMBRE_PRODUCTO =
            "SELECT d.idVenta, p.nombre, d.cantidad, d.subtotal "
                    + "FROM DetalleVenta d "
                    + "INNER JOIN Producto p ON p.idProducto = d.idProducto "
                    + "ORDER BY d.idVenta, d.idDetalleVenta";

    private static final String SQL_LISTAR_POR_VENTA =
            "SELECT idDetalleVenta, idVenta, idProducto, cantidad, subtotal "
                    + "FROM DetalleVenta WHERE idVenta = ? ORDER BY idDetalleVenta";

    private final JdbcTemplate jdbcTemplate;

    public DetalleVentaDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void insertarTodos(List<DetalleVenta> detalles) {
        jdbcTemplate.batchUpdate(SQL_INSERTAR, detalles, detalles.size(), this::asignarValores);
    }

    @Override
    public List<DetalleHistorial> listarConNombreProducto() {
        return jdbcTemplate.query(SQL_LISTAR_CON_NOMBRE_PRODUCTO, this::mapearDetalleHistorial);
    }

    @Override
    public List<DetalleVenta> listarPorVenta(int idVenta) {
        return jdbcTemplate.query(SQL_LISTAR_POR_VENTA, this::mapearDetalleVenta, idVenta);
    }

    // Convierte una fila de la tabla DetalleVenta en un objeto DetalleVenta
    private DetalleVenta mapearDetalleVenta(ResultSet fila, int numeroFila) throws SQLException {
        return new DetalleVenta(
                fila.getInt("idDetalleVenta"),
                fila.getInt("idVenta"),
                fila.getInt("idProducto"),
                fila.getInt("cantidad"),
                fila.getBigDecimal("subtotal")
        );
    }

    // Asigna los valores de un detalle a la sentencia INSERT
    private void asignarValores(PreparedStatement sentencia, DetalleVenta detalle) throws SQLException {
        sentencia.setInt(1, detalle.getIdVenta());
        sentencia.setInt(2, detalle.getIdProducto());
        sentencia.setInt(3, detalle.getCantidad());
        sentencia.setBigDecimal(4, detalle.getSubtotal());
    }

    // Convierte una fila de la consulta en un detalle del historial
    private DetalleHistorial mapearDetalleHistorial(ResultSet fila, int numeroFila) throws SQLException {
        return new DetalleHistorial(
                fila.getInt("idVenta"),
                fila.getString("nombre"),
                fila.getInt("cantidad"),
                fila.getBigDecimal("subtotal")
        );
    }
}
