package zheyben.minimarketaj.minimarket.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import zheyben.minimarketaj.minimarket.modelo.EstadoVenta;
import zheyben.minimarketaj.minimarket.modelo.Venta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

// DAO: implementa el acceso a la tabla Venta mediante JDBC
@Repository
public class VentaDAOImpl implements VentaDAO {

    private static final String SQL_INSERTAR = "INSERT INTO Venta (fechaHora, total, estado) VALUES (?, ?, ?)";

    private static final String COLUMNAS =
            "SELECT idVenta, fechaHora, total, estado, fechaAnulacion, motivoAnulacion FROM Venta";

    private static final String SQL_LISTAR_TODAS = COLUMNAS + " ORDER BY fechaHora, idVenta";

    private static final String SQL_BUSCAR_POR_ID = COLUMNAS + " WHERE idVenta = ?";

    private static final String SQL_BUSCAR_ULTIMA = COLUMNAS + " ORDER BY idVenta DESC LIMIT 1";

    // Las condiciones evitan anular una venta ya anulada o una venta que ya no es la última.
    // MySQL no permite consultar la misma tabla que se actualiza, por eso se usa una subconsulta derivada.
    private static final String SQL_ANULAR =
            "UPDATE Venta SET estado = ?, fechaAnulacion = ?, motivoAnulacion = ? "
                    + "WHERE idVenta = ? AND estado = ? "
                    + "AND idVenta = (SELECT ultima FROM (SELECT MAX(idVenta) AS ultima FROM Venta) AS ultimaVenta)";

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

    @Override
    public Optional<Venta> buscarPorId(int idVenta) {
        return jdbcTemplate.query(SQL_BUSCAR_POR_ID, this::mapearVenta, idVenta).stream().findFirst();
    }

    @Override
    public Optional<Venta> buscarUltima() {
        return jdbcTemplate.query(SQL_BUSCAR_ULTIMA, this::mapearVenta).stream().findFirst();
    }

    @Override
    public boolean anular(int idVenta, LocalDateTime fechaAnulacion, String motivo) {
        int filasActualizadas = jdbcTemplate.update(SQL_ANULAR,
                EstadoVenta.ANULADA.name(), Timestamp.valueOf(fechaAnulacion), motivo,
                idVenta, EstadoVenta.REGISTRADA.name());
        return filasActualizadas == 1;
    }

    // Prepara la sentencia INSERT pidiendo a MySQL el código autogenerado
    private PreparedStatement prepararInsercion(Connection conexion, Venta venta) throws SQLException {
        PreparedStatement sentencia = conexion.prepareStatement(SQL_INSERTAR, Statement.RETURN_GENERATED_KEYS);
        sentencia.setTimestamp(1, Timestamp.valueOf(venta.getFechaHora()));
        sentencia.setBigDecimal(2, venta.getTotal());
        sentencia.setString(3, EstadoVenta.REGISTRADA.name());
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
        Timestamp anulacion = fila.getTimestamp("fechaAnulacion");
        return new Venta(
                fila.getInt("idVenta"),
                fila.getTimestamp("fechaHora").toLocalDateTime(),
                fila.getBigDecimal("total"),
                EstadoVenta.desdeTexto(fila.getString("estado")),
                anulacion == null ? null : anulacion.toLocalDateTime(),
                fila.getString("motivoAnulacion")
        );
    }
}
