package ingsof.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import ingsof.entidad.Genotipo;

public interface GenotipoR extends JpaRepository<Genotipo, Integer> {
}
