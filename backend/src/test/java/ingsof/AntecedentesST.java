package ingsof;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
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

    @Mock
    private AntecedenteR repo;

    @Mock
    private ParticipantecrfR partRepo;

    @InjectMocks
    private AntecedenteS servicio;

    // --- TESTS DE CREACIÓN ---

    @Test
    @DisplayName("Crear: Debería guardar correctamente cuando los datos son válidos")
    void crear_DatosValidos() {
        // Arrange
        String codPart = "PART-001";
        Antecedente a = new Antecedente();
        a.setCodPart("PART-001");
        a.setDiagnostico("No"); // Caso simple sin dependencias

        Participantecrf p = new Participantecrf();
        p.setGrupo("Caso");

        when(partRepo.findById(codPart)).thenReturn(Optional.of(p));
        when(repo.save(any(Antecedente.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        servicio.crear(a);

        // Assert
        verify(repo).save(a);
    }

    @Test
    @DisplayName("Crear: Debería fallar si falta el Participante")
    void crear_SinParticipante() {
        Antecedente a = new Antecedente();
        a.setCodPart("UNKNOWN");

        when(partRepo.findById("UNKNOWN")).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> servicio.crear(a));
        verify(repo, never()).save(any());
    }

    @Test
    @DisplayName("Crear: Debería fallar si es Control y tiene Diagnóstico 'Sí'")
    void crear_ControlConDiagnostico() {
        String codPart = "PART-002";
        Antecedente a = new Antecedente();
        a.setCodPart(codPart);
        a.setDiagnostico("Sí");

        Participantecrf p = new Participantecrf();
        p.setGrupo("Control");

        when(partRepo.findById(codPart)).thenReturn(Optional.of(p));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> servicio.crear(a));
        assertEquals("Solo los casos pueden tener diagnóstico histológico.", ex.getReason());
    }

    // --- TESTS DE ACTUALIZACIÓN ---

    @Test
    @DisplayName("Actualizar: Debería actualizar datos si el ID existe")
    void actualizar_Exito() {
        // Arrange
        int id = 1;
        String codPart = "PART-001";
        
        Antecedente existente = new Antecedente();
        existente.setIdAntec(id);
        existente.setCodPart(codPart);
        existente.setDiagnostico("No");

        Antecedente nuevosDatos = new Antecedente();
        nuevosDatos.setDiagnostico("Sí");
        // codPart null en cambios, usa el existente

        Participantecrf p = new Participantecrf();
        p.setGrupo("Caso");

        when(repo.findById(id)).thenReturn(Optional.of(existente));
        when(partRepo.findById(codPart)).thenReturn(Optional.of(p));
        when(repo.save(any(Antecedente.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        servicio.actualizar(id, nuevosDatos);

        // Assert
        assertEquals("Sí", existente.getDiagnostico());
        verify(repo).save(existente);
    }

    @Test
    @DisplayName("Actualizar: Debería lanzar excepción si el ID no existe")
    void actualizar_NoExiste() {
        int id = 99;
        when(repo.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> servicio.actualizar(id, new Antecedente()));
        verify(repo, never()).save(any());
    }
}
