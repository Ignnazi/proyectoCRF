package ingsof;

import ingsof.servicio.ParticipantecrfS;
import ingsof.entidad.Participantecrf;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class ParticipantecrfSTest {
    @Autowired
    private ParticipantecrfS servicio;

    @Test
    void crearParticipante() {
        Participantecrf participantecrf = new Participantecrf();
        participantecrf.setCodPart("CT006");
        participantecrf.setNombre("Juan Perez");
        participantecrf.setGrupo("Control");
        participantecrf.setIdUser(3);
        this.servicio.guardar(participantecrf);
    }
}
