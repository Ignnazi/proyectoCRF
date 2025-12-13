package ingsof.repositorio;

import ingsof.entidad.Antropometria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AntropometriaR extends JpaRepository<Antropometria, Integer> {
    List<Antropometria> findAllByCodPart(String codPart);
}
