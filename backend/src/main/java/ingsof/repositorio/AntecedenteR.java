package ingsof.repositorio;

import ingsof.entidad.Antecedente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AntecedenteR extends JpaRepository<Antecedente, Integer> {
    Optional<Antecedente> findByCodPart(String codPart);
}
