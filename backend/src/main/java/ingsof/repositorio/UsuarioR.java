package ingsof.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import ingsof.entidad.Usuario;

public interface UsuarioR extends JpaRepository<Usuario, Integer> {
}
