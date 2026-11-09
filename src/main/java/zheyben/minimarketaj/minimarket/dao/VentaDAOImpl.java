package zheyben.minimarketaj.minimarket.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import zheyben.minimarketaj.minimarket.modelo.Venta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

// DAO: implementa el acceso a la tabla Venta mediante JDBC
@Repository
public class VentaDAOImpl implements VentaDAO {

    private static final String SQL_INSERTAR = "INSERT INTO Venta (fechaHora, total) VALUES (?, ?)";

    private static final String SQL_LISTAR_TODAS =
            "SELECT idVenta, fechaHora, total FROM Venta ORDER BY fechaHora, idVenta";

    private final JdbcTemplate jdbcTemplate;

    public VentaDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int insertar(Venta venta) {
        KeyHolder codigoGenerado = new GeneratedKeyHolder();
        jdbcTemplate.update(conexion -> prepararInsercion(conexion, venta), codigoGenerado);
        return obtenerCodigo(codigoGenerado);
    }

    @Override
    public List<Venta> listarTodas() {
        return jdbcTemplate.query(SQL_LISTAR_TODAS, this::mapearVenta);
    }

    // Prepara la sentencia INSERT pidiendo a MySQL el código autogenerado
    private PreparedStatement prepararInsercion(Connection conexion, Venta venta) throws SQLException {
        PreparedStatement sentencia = conexion.prepareStatement(SQL_INSERTAR, Statement.RETURN_GENERATED_KEYS);
        sentencia.setTimestamp(1, Timestamp.valueOf(venta.getFechaHora()));
        sentencia.setBigDecimal(2, venta.getTotal());
        return sentencia;
    }

    private int obtenerCodigo(KeyHolder codigoGenerado) {
        Number codigo = codigoGenerado.getKey();
        if (codigo == null) {
            throw new IllegalStateException("La base de datos no devolvió el código de la venta");
        }
        return codigo.intValue();
    }

    // Convierte una fila de la tabla Venta en un objeto Venta
    private Venta mapearVenta(ResultSet fila, int numeroFila) throws SQLException {
        return new Venta(
                fila.getInt("idVenta"),
                fila.getTimestamp("fechaHora").toLocalDateTime(),
                fila.getBigDecimal("total")
        );
    }
}
