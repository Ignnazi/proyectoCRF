package ingsof;

import java.sql.Date;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ingsof.entidad.Antecedente;
import ingsof.entidad.Participantecrf;
import ingsof.repositorio.AntecedenteR;
import ingsof.repositorio.ParticipantecrfR;
import ingsof.servicio.AntecedenteS;

@ExtendWith(MockitoExtension.class)
class AntecedentesST {

    @Mock private AntecedenteR repo;
    @Mock private ParticipantecrfR partRepo;
    @InjectMocks private AntecedenteS servicio;

    @Test
    void guardar_DeberiaGuardarSiEsCaso() {
        Antecedente a = new Antecedente();
        a.setCodPart("CS001");
        a.setDiagnostico("Sí");
        a.setFechaDiag(Date.valueOf(LocalDate.now()));
        Participantecrf p = new Participantecrf();
        p.setGrupo("Caso");    
        assertDoesNotThrow(() -> servicio.guardar(a));
    }


    @Test
    void guardar_ControlConDiagnostico_NoLanzaError() {
        Antecedente a = new Antecedente();
        a.setCodPart("CT001");
        a.setDiagnostico("Sí");
        a.setFechaDiag(Date.valueOf(LocalDate.now()));

        Participantecrf p = new Participantecrf();
        p.setGrupo("Control");

        assertDoesNotThrow(() -> servicio.guardar(a));
    }



    @Test
    void guardar_ControlConFechaDiagnostico_NoLanzaError() {
        Antecedente a = new Antecedente();
        a.setCodPart("CT001");
        a.setFechaDiag(Date.valueOf(LocalDate.now()));

        Participantecrf p = new Participantecrf();
        p.setGrupo("Control");

        assertDoesNotThrow(() -> servicio.guardar(a));
    }
}