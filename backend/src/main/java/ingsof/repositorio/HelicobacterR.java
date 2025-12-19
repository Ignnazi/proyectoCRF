package ingsof.repositorio;

import ingsof.entidad.Helicobacter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HelicobacterR extends JpaRepository<Helicobacter, Integer> {
    Optional<Helicobacter> findByCodPart(String codPart);
}
