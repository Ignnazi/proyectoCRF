package ingsof;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import ingsof.entidad.Participantecrf;
import ingsof.repositorio.ParticipantecrfR;
import ingsof.servicio.ParticipantecrfS;

@ExtendWith(MockitoExtension.class)
class ParticipantecrfST {

    @Mock
    private ParticipantecrfR repo;

    @InjectMocks
    private ParticipantecrfS servicio;

    @Test
    void crear_DeberiaGenerarIdCorrectoParaCaso() {
        // DATOS
        Participantecrf nuevo = new Participantecrf();
        nuevo.setNombre("Juan Perez");
        nuevo.setGrupo("Caso");

        // MOCK: Simulamos que el último ID de casos (CS) fue 0, así que debería generar CS001
        when(repo.maxNumeroPorPrefijo("CS")).thenReturn(0);
        when(repo.save(any(Participantecrf.class))).thenAnswer(i -> i.getArguments()[0]);

        // EJECUCIÓN
        Participantecrf resultado = servicio.crear(nuevo);

        // VERIFICACIÓN
        assertNotNull(resultado.getCodPart());
        assertEquals("CS001", resultado.getCodPart());
        assertNotNull(resultado.getFechaInclusion()); // Debe asignarse fecha si es nula
    }

    @Test
    void crear_DeberiaGenerarIdCorrectoParaControl() {
        // DATOS
        Participantecrf nuevo = new Participantecrf();
        nuevo.setNombre("Maria Lopez");
        nuevo.setGrupo("Control");

        // MOCK: Simulamos que ya existen 15 controles, el siguiente debe ser 16 (CT016)
        when(repo.maxNumeroPorPrefijo("CT")).thenReturn(15);
        when(repo.save(any(Participantecrf.class))).thenAnswer(i -> i.getArguments()[0]);

        // EJECUCIÓN
        Participantecrf resultado = servicio.crear(nuevo);

        // VERIFICACIÓN
        assertEquals("CT016", resultado.getCodPart());
    }

    @Test
    void crear_DeberiaFallarSiFaltanDatos() {
        Participantecrf invalido = new Participantecrf();
        // No tiene nombre ni grupo

        assertThrows(ResponseStatusException.class, () -> {
            servicio.crear(invalido);
        });
    }
    
    @Test
    void actualizar_DeberiaCambiarIdSiCambiaGrupo() {
        // DATOS: Participante existente (Control)
        String idViejo = "CT005";
        Participantecrf existente = new Participantecrf();
        existente.setCodPart(idViejo);
        existente.setNombre("Ana");
        existente.setGrupo("Control");
        
        // DATOS: Cambios solicitados (pasa a Caso)
        Participantecrf cambios = new Participantecrf();
        cambios.setGrupo("Caso");

        // MOCKS
        when(repo.findById(idViejo)).thenReturn(Optional.of(existente));
        when(repo.maxNumeroPorPrefijo("CS")).thenReturn(9);
         when(repo.actualizarCodigo("CS010", idViejo)).thenReturn(1);

        Participantecrf actualizadoDb = new Participantecrf();
        actualizadoDb.setCodPart("CS010");
        actualizadoDb.setNombre("Ana");
        actualizadoDb.setGrupo("Caso");

        when(repo.findById("CS010")).thenReturn(Optional.of(actualizadoDb));

        when(repo.save(any(Participantecrf.class)))
        .thenAnswer(inv -> inv.getArgument(0));

        // EJECUCIÓN
        Participantecrf resultado = servicio.actualizar(idViejo, cambios);


        // VERIFICACIÓN
        assertEquals("CS010", resultado.getCodPart());
        assertEquals("Caso", resultado.getGrupo());
        verify(repo).actualizarCodigo("CS010", idViejo);
    }
}