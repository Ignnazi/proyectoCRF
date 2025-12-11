package ingsof.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import ingsof.entidad.Habito;

public interface HabitoR extends JpaRepository<Habito, Integer> {
    // Para listar los hábitos de un participante específico
    List<Habito> findByCodPart(String codPart);
}
