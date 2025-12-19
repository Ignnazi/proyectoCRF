package ingsof;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

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
        a.setDiagnostico("Sí"); // Válido para caso

        Participantecrf p = new Participantecrf();
        p.setGrupo("Caso");

        when(partRepo.findById("CS001")).thenReturn(Optional.of(p));
        when(repo.save(any(Antecedente.class))).thenReturn(a);

        // CAMBIO: Se usa guardar() en lugar de crear()
        assertDoesNotThrow(() -> servicio.guardar(a));
    }

    @Test
    void guardar_DeberiaFallarSiControlTieneDiagnostico() {
        Antecedente a = new Antecedente();
        a.setCodPart("CT001");
        a.setDiagnostico("Sí"); // Inválido para control

        Participantecrf p = new Participantecrf();
        p.setGrupo("Control");

        when(partRepo.findById("CT001")).thenReturn(Optional.of(p));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            // CAMBIO: Se usa guardar() en lugar de crear()
            servicio.guardar(a);
        });
        assertTrue(ex.getMessage().contains("Solo los casos pueden tener diagnóstico"));
    }

    @Test
    @DisplayName("Crear: Debería fallar si NO es 'Caso' pero tiene Fecha Diagnóstico")
    void crear_DeberiaFallar_SiControlTieneFecha() {
        // Arrange
        String codPart = "P003";
        Antecedente nuevo = new Antecedente();
        nuevo.setCodPart(codPart);
        nuevo.setFechaDiag(java.sql.Date.valueOf(LocalDate.now())); // ESTO DEBERÍA FALLAR EN CONTROLES

        Participantecrf participante = new Participantecrf();
        participante.setGrupo("Control");

        when(partRepo.findById("CT001")).thenReturn(Optional.of(p));

        // CAMBIO: Se usa guardar() en lugar de crear()
        assertThrows(ResponseStatusException.class, () -> servicio.guardar(a));
    }
}