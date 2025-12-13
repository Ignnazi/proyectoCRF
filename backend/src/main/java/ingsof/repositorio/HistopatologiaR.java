package ingsof.repositorio;

import ingsof.entidad.Histopatologia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HistopatologiaR extends JpaRepository<Histopatologia, Integer> {
    boolean existsByCodPart(String codPart);
    void deleteByCodPart(String codPart);
    Optional<Histopatologia> findByCodPart(String codPart);
}

