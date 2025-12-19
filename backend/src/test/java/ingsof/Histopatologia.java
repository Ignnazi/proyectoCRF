package ingsof;

import ingsof.entidad.Histopatologia;
import ingsof.entidad.Participantecrf;
import ingsof.repositorio.HistopatologiaR;
import ingsof.repositorio.ParticipantecrfR;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import ingsof.servicio.HistopatologiaS;

@ExtendWith(MockitoExtension.class)
class HistopatologiaST {

    @Mock
    private HistopatologiaR repo;

    @Mock
    private ParticipantecrfR partRepo;

    @InjectMocks
    private HistopatologiaS servicio;

    // --- PRUEBAS DE CREACIÓN (Reglas de Negocio) ---

    @Test
    @DisplayName("Crear: Debería guardar si es 'Caso' y no existe registro previo")
    void crear_Exito() {
        // Arrange
        String codPart = "P001";
        Histopatologia nuevo = new Histopatologia();
        nuevo.setCodPart(codPart);

        Participantecrf participante = new Participantecrf();
        participante.setGrupo("Caso"); // Es válido

        when(partRepo.findById(codPart)).thenReturn(Optional.of(participante));
        when(repo.existsByCodPart(codPart)).thenReturn(false); // No existe previo
        when(repo.save(any(Histopatologia.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Histopatologia resultado = servicio.crear(nuevo);

        // Assert
        assertNotNull(resultado);
        verify(repo).save(nuevo);
    }

    @Test
    @DisplayName("Crear: Debería fallar si participante es 'Control'")
    void crear_Falla_SiEsControl() {
        // Arrange
        String codPart = "P002";
        Histopatologia nuevo = new Histopatologia();
        nuevo.setCodPart(codPart);

        Participantecrf participante = new Participantecrf();
        participante.setGrupo("Control"); // INVALIDO para histopatología

        when(partRepo.findById(codPart)).thenReturn(Optional.of(participante));

        // Act & Assert
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, 
            () -> servicio.crear(nuevo));
        
        assertEquals("Solo se permiten registros de casos", ex.getReason());
        verify(repo, never()).save(any());
    }

    @Test
    @DisplayName("Crear: Debería fallar si ya existe histopatología para el participante")
    void crear_Falla_SiYaExiste() {
        // Arrange
        String codPart = "P001";
        Histopatologia nuevo = new Histopatologia();
        nuevo.setCodPart(codPart);
        
        Participantecrf participante = new Participantecrf();
        participante.setGrupo("Caso");

        when(partRepo.findById(codPart)).thenReturn(Optional.of(participante));
        when(repo.existsByCodPart(codPart)).thenReturn(true); // YA EXISTE

        // Act & Assert
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, 
            () -> servicio.crear(nuevo));
            
        assertEquals("Ya existe histopatologia para este participante", ex.getReason());
        verify(repo, never()).save(any());
    }

    // --- PRUEBAS DE ACTUALIZACIÓN ---

    @Test
    @DisplayName("Actualizar: Debería actualizar campos correctamente")
    void actualizar_Exito() {
        // Arrange
        int id = 1;
        String codPart = "P001";
        
        Histopatologia existente = new Histopatologia();
        existente.setIdHisto(id);
        existente.setCodPart(codPart);
        existente.setEstadio("I");

        Histopatologia cambios = new Histopatologia();
        cambios.setEstadio("II"); // Cambio de estadio
        // codPart viene null, se mantiene el existente

        Participantecrf participante = new Participantecrf();
        participante.setGrupo("Caso");

        when(repo.findById(id)).thenReturn(Optional.of(existente));
        when(partRepo.findById(codPart)).thenReturn(Optional.of(participante)); // Busca al dueño actual
        when(repo.save(any(Histopatologia.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Histopatologia resultado = servicio.actualizar(id, cambios);

        // Assert
        assertEquals("II", resultado.getEstadio());
        verify(repo).save(existente);
    }

    // --- PRUEBAS DE BORRADO AUTOMÁTICO (Lógica Especial) ---

    @Test
    @DisplayName("BorrarControl: Debería eliminar registro si participante es 'Control'")
    void borrarControl_DeberiaBorrar() {
        // Arrange
        String codPart = "P003";
        Participantecrf participante = new Participantecrf();
        participante.setGrupo("Control"); // Se cambió a Control

        when(partRepo.findById(codPart)).thenReturn(Optional.of(participante));
        when(repo.existsByCodPart(codPart)).thenReturn(true); // Tiene histopatología antigua

        // Act
        servicio.borrarControl(codPart);

        // Assert
        verify(repo).deleteByCodPart(codPart); // Verificamos que se llamó a borrar
    }

    @Test
    @DisplayName("BorrarControl: NO debería borrar si participante sigue siendo 'Caso'")
    void borrarControl_NoDeberiaBorrar() {
        // Arrange
        String codPart = "P004";
        Participantecrf participante = new Participantecrf();
        participante.setGrupo("Caso"); // Sigue siendo Caso

        when(partRepo.findById(codPart)).thenReturn(Optional.of(participante));

        // Act
        servicio.borrarControl(codPart);

        // Assert
        verify(repo, never()).deleteByCodPart(anyString()); // Nunca debió intentar borrar
    }
    
    // --- PRUEBAS DE LECTURA ---
    
    @Test
    @DisplayName("PorId: Debería lanzar excepción si no existe")
    void porId_NoExiste() {
        when(repo.findById(99)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> servicio.porId(99));
    }
}