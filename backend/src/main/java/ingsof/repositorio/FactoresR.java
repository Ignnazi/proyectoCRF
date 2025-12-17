package ingsof.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import ingsof.entidad.Factor;

public interface FactoresR extends JpaRepository<Factor, Integer> {
}
