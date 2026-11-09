package zheyben.minimarketaj.minimarket.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import zheyben.minimarketaj.minimarket.modelo.Producto;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

// DAO: implementa el acceso a la tabla Producto mediante JDBC
@Repository
public class ProductoDAOImpl implements ProductoDAO {

    private static final String COLUMNAS = "SELECT idProducto, nombre, precio, cantidadDisponible FROM Producto";

    private static final String SQL_LISTAR_TODOS = COLUMNAS + " ORDER BY nombre";

    private static final String SQL_BUSCAR_POR_NOMBRE = COLUMNAS + " WHERE nombre LIKE ? ORDER BY nombre";

    private static final String SQL_BUSCAR_POR_ID = COLUMNAS + " WHERE idProducto = ?";

    private static final String SQL_ACTUALIZAR_PRECIO = "UPDATE Producto SET precio = ? WHERE idProducto = ?";

    // La condición cantidadDisponible >= ? evita que el inventario quede en negativo
    private static final String SQL_DESCONTAR_CANTIDAD =
            "UPDATE Producto SET cantidadDisponible = cantidadDisponible - ? "
                    + "WHERE idProducto = ? AND cantidadDisponible >= ?";

    private final JdbcTemplate jdbcTemplate;

    public ProductoDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Producto> listarTodos() {
        return jdbcTemplate.query(SQL_LISTAR_TODOS, this::mapearProducto);
    }

    @Override
    public List<Producto> buscarPorNombre(String texto) {
        return jdbcTemplate.query(SQL_BUSCAR_POR_NOMBRE, this::mapearProducto, "%" + texto + "%");
    }

    @Override
    public Optional<Producto> buscarPorId(int idProducto) {
        return jdbcTemplate.query(SQL_BUSCAR_POR_ID, this::mapearProducto, idProducto)
                .stream()
                .findFirst();
    }

    @Override
    public boolean actualizarPrecio(int idProducto, BigDecimal nuevoPrecio) {
        int filasActualizadas = jdbcTemplate.update(SQL_ACTUALIZAR_PRECIO, nuevoPrecio, idProducto);
        return filasActualizadas == 1;
    }

    @Override
    public boolean descontarCantidad(int idProducto, int cantidad) {
        int filasActualizadas = jdbcTemplate.update(SQL_DESCONTAR_CANTIDAD, cantidad, idProducto, cantidad);
        return filasActualizadas == 1;
    }

    // Convierte una fila de la tabla Producto en un objeto Producto
    private Producto mapearProducto(ResultSet fila, int numeroFila) throws SQLException {
        return new Producto(
                fila.getInt("idProducto"),
                fila.getString("nombre"),
                fila.getBigDecimal("precio"),
                fila.getInt("cantidadDisponible")
        );
    }
}
