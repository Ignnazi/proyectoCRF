package ingsof;

import ingsof.entidad.Participantecrf;
import ingsof.repositorio.ParticipantecrfR;
import ingsof.servicio.ParticipantecrfS;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParticipantecrfST {

    @Mock
    ParticipantecrfR repo;

    @InjectMocks
    ParticipantecrfS servicio;

    @Test
    void listar_ok() {
        when(repo.findAll()).thenReturn(List.of(new Participantecrf(), new Participantecrf()));

        var lista = servicio.listar();

        assertEquals(2, lista.size());
        verify(repo).findAll();
    }

    @SuppressWarnings("null")
    @Test
    void crear_basico_generaCodigoYFecha() {
        Participantecrf in = new Participantecrf();
        in.setNombre("Ana");
        in.setGrupo("Caso");     // → prefijo CS

        when(repo.maxNumeroPorPrefijo("CS")).thenReturn(0);
        when(repo.save(any(Participantecrf.class))).thenAnswer(i -> i.getArgument(0));

        Participantecrf out = servicio.crear(in);

        assertEquals("CS001", out.getCodPart());
        assertEquals("Caso", out.getGrupo());
        assertNotNull(out.getFechaInclusion());
        verify(repo).save(any(Participantecrf.class));
    }

    @SuppressWarnings("null")
    @Test
    void actualizar_basico_sinCambioDeGrupo() {
        Participantecrf db = new Participantecrf();
        db.setCodPart("CS010");
        db.setNombre("Viejo");
        db.setGrupo("Caso");
        db.setIdUser(1);
        db.setFechaInclusion(LocalDateTime.parse("2025-11-06T08:00:00"));

        when(repo.findById("CS010")).thenReturn(Optional.of(db));
        when(repo.save(any(Participantecrf.class))).thenAnswer(i -> i.getArgument(0));

        // cambios simples
        Participantecrf cambios = new Participantecrf();
        cambios.setNombre("Nuevo");
        cambios.setIdUser(2);

        Participantecrf out = servicio.actualizar("CS010", cambios);

        assertEquals("CS010", out.getCodPart());
        assertEquals("Nuevo", out.getNombre());
        assertEquals(2, out.getIdUser());
        verify(repo, never()).actualizarCodigo(anyString(), anyString());
        verify(repo).save(any(Participantecrf.class));
    }

    @Test
    void eliminar_ok() {
        servicio.eliminar("CS001");
        verify(repo).deleteById("CS001");
    }
}
