package ingsof.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import ingsof.entidad.Usuario;
import java.util.Optional;

public interface UsuarioR extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByNombre(String nombre);
}
