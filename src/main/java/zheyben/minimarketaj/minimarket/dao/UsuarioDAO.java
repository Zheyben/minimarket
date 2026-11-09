package zheyben.minimarketaj.minimarket.dao;

import zheyben.minimarketaj.minimarket.modelo.Usuario;

import java.util.Optional;

// DAO: define las operaciones de acceso a datos de Usuario
public interface UsuarioDAO {

    Optional<Usuario> buscarPorNombreUsuario(String nombreUsuario);
}
