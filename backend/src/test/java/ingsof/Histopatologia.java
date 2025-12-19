package ingsof;

import ingsof.entidad.Histopatologia;
import ingsof.entidad.Participantecrf;
import ingsof.repositorio.HistopatologiaR;
import ingsof.repositorio.ParticipantecrfR;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import ingsof.servicio.HistopatologiaS;

@ExtendWith(MockitoExtension.class)
class HistopatologiaST {

    @Mock private HistopatologiaR repo;
    @Mock private ParticipantecrfR partRepo;
    @InjectMocks private HistopatologiaS servicio;

    @Test
    void crear_DeberiaPermitirSiEsCaso() {
        Histopatologia h = new Histopatologia();
        h.setCodPart("CS001");

        Participantecrf p = new Participantecrf();
        p.setGrupo("Caso");

        when(partRepo.findById("CS001")).thenReturn(Optional.of(p));
        when(repo.existsByCodPart("CS001")).thenReturn(false);
        when(repo.save(h)).thenReturn(h);

        assertDoesNotThrow(() -> servicio.crear(h));
    }

    @Test
    void crear_DeberiaBloquearSiEsControl() {
        Histopatologia h = new Histopatologia();
        h.setCodPart("CT001");

        Participantecrf p = new Participantecrf();
        p.setGrupo("Control"); // <--- ES CONTROL

        when(partRepo.findById("CT001")).thenReturn(Optional.of(p));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            servicio.crear(h);
        });
        assertTrue(ex.getMessage().contains("Solo se permiten registros de casos"));
    }
}