package ingsof.repositorio;

import ingsof.entidad.Participantecrf;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipantecrfR extends JpaRepository<Participantecrf, String> {

    @Query(value =
      "SELECT IFNULL(MAX(CAST(SUBSTRING(cod_part,3) AS UNSIGNED)),0) " +
      "FROM participantecrf WHERE cod_part LIKE CONCAT(?1,'%')",
      nativeQuery = true)
    int maxNumeroPorPrefijo(String prefijo);

    @Modifying
    @Query(value = "UPDATE participantecrf SET cod_part=?1 WHERE cod_part=?2", nativeQuery = true)
    int actualizarCodigo(String nuevo, String viejo);

    boolean existsByCodPart(String codPart);
}

