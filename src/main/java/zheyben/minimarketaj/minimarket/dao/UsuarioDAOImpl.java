package zheyben.minimarketaj.minimarket.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import zheyben.minimarketaj.minimarket.modelo.Usuario;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

// DAO: implementa el acceso a la tabla Usuario mediante JDBC
@Repository
public class UsuarioDAOImpl implements UsuarioDAO {

    private static final String SQL_BUSCAR_POR_NOMBRE_USUARIO =
            "SELECT idUsuario, nombreUsuario, contraseña, rol FROM Usuario WHERE nombreUsuario = ?";

    private final JdbcTemplate jdbcTemplate;

    public UsuarioDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Usuario> buscarPorNombreUsuario(String nombreUsuario) {
        return jdbcTemplate.query(SQL_BUSCAR_POR_NOMBRE_USUARIO, this::mapearUsuario, nombreUsuario)
                .stream()
                .findFirst();
    }

    // Convierte una fila de la tabla Usuario en un objeto Usuario
    private Usuario mapearUsuario(ResultSet fila, int numeroFila) throws SQLException {
        return new Usuario(
                fila.getInt("idUsuario"),
                fila.getString("nombreUsuario"),
                fila.getString("contraseña"),
                fila.getString("rol")
        );
    }
}
