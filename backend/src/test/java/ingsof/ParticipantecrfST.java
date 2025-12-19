package ingsof;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import ingsof.entidad.Participantecrf;
import ingsof.repositorio.ParticipantecrfR;
import ingsof.servicio.ParticipantecrfS;

@ExtendWith(MockitoExtension.class)
class ParticipantecrfST {

    @Mock
    private ParticipantecrfR repo;

    @InjectMocks
    private ParticipantecrfS servicio;

    // -------------------------------------------------------------------
    // TESTS DE CREAR (INSERT)
    // -------------------------------------------------------------------

    @Test
    @DisplayName("Crear - Genera código automático correctamente (Caso -> CS...)")
    void crear_GeneraCodigoAutomatico() {
        // Arrange
        Participantecrf p = new Participantecrf();
        p.setNombre("Juan Perez");
        p.setGrupo("Caso");
        // codPart es null, así que debe generarlo

        // Simulamos que el ultimo numero para "CS" es 0, así que debería generar "CS001"
        when(repo.maxNumeroPorPrefijo("CS")).thenReturn(0);
        when(repo.save(any(Participantecrf.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Participantecrf resultado = servicio.crear(p);

        // Assert
        assertNotNull(resultado.getCodPart());
        assertEquals("CS001", resultado.getCodPart());
        assertNotNull(resultado.getFechaInclusion()); // Debe poner fecha actual
        verify(repo).save(p);
    }

    @Test
    @DisplayName("Crear - Falla si faltan datos obligatorios")
    void crear_FaltanDatos_LanzaExcepcion() {
        Participantecrf p = new Participantecrf();
        p.setNombre(null); // Obligatorio

        Exception ex = assertThrows(IllegalArgumentException.class, () -> servicio.crear(p));
        assertEquals("nombre es requerido", ex.getMessage());
    }

    @Test
    @DisplayName("Crear - Falla si se envía un ID manual que ya existe")
    void crear_IdManualDuplicado_LanzaExcepcion() {
        Participantecrf p = new Participantecrf();
        p.setNombre("Maria");
        p.setGrupo("Control");
        p.setCodPart("CT999"); // ID manual

        // Simulamos que ya existe en BD
        when(repo.existsByCodPart("CT999")).thenReturn(true);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> servicio.crear(p));
        assertTrue(ex.getMessage().contains("cod_part ya existe"));
    }

    // -------------------------------------------------------------------
    // TESTS DE ACTUALIZAR (UPDATE)
    // -------------------------------------------------------------------

    @Test
    @DisplayName("Actualizar - Simple (Sin cambio de grupo)")
    void actualizar_SinCambioGrupo_ActualizaCampos() {
        // Arrange
        String id = "CS010";
        Participantecrf existente = new Participantecrf();
        existente.setCodPart(id);
        existente.setNombre("Nombre Viejo");
        existente.setGrupo("Caso");

        Participantecrf cambios = new Participantecrf();
        cambios.setNombre("Nombre Nuevo");
        cambios.setGrupo("Caso"); // Mismo grupo

        when(repo.findById(id)).thenReturn(Optional.of(existente));
        when(repo.save(any(Participantecrf.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Participantecrf resultado = servicio.actualizar(id, cambios);

        // Assert
        assertEquals("Nombre Nuevo", resultado.getNombre());
        assertEquals("CS010", resultado.getCodPart()); // El ID no cambia
        verify(repo).save(existente);
        // Verificamos que NO se llamó a la lógica compleja de cambio de código
        verify(repo, never()).actualizarCodigo(anyString(), anyString());
    }

    @Test
    @DisplayName("Actualizar - Complejo (Con cambio de grupo y cambio de ID)")
    void actualizar_ConCambioGrupo_CambiaId() {
        // ESCENARIO: Cambiamos de "Caso" (CS010) a "Control" (Debería ser CT005 si el max es 4)
        String idViejo = "CS010";
        
        // 1. El usuario existente
        Participantecrf existente = new Participantecrf();
        existente.setCodPart(idViejo);
        existente.setGrupo("Caso");
        existente.setNombre("Ana");

        // 2. Los cambios solicitados
        Participantecrf cambios = new Participantecrf();
        cambios.setGrupo("Control"); // CAMBIO DE GRUPO

        // 3. El usuario "nuevo" que recuperamos tras el update nativo
        String idNuevoEsperado = "CT005";
        Participantecrf usuarioRenombrado = new Participantecrf();
        usuarioRenombrado.setCodPart(idNuevoEsperado);
        usuarioRenombrado.setGrupo("Control");
        usuarioRenombrado.setNombre("Ana");

        // --- MOCKS ---
        // Paso A: Buscar el original
        when(repo.findById(idViejo)).thenReturn(Optional.of(existente));
        
        // Paso B: Calcular nuevo ID (Simulamos que hay 4 Controles, el siguiente es 5)
        when(repo.maxNumeroPorPrefijo("CT")).thenReturn(4);
        
        // Paso C: Ejecutar update nativo (retorna 1 fila afectada)
        when(repo.actualizarCodigo(idNuevoEsperado, idViejo)).thenReturn(1);
        
        // Paso D: Buscar el nuevo registro (Simulamos que la BD ya lo tiene con nuevo ID)
        when(repo.findById(idNuevoEsperado)).thenReturn(Optional.of(usuarioRenombrado));
        
        // Paso E: Guardar final
        when(repo.save(any(Participantecrf.class))).thenReturn(usuarioRenombrado);

        // Act
        Participantecrf resultado = servicio.actualizar(idViejo, cambios);

        // Assert
        assertEquals("CT005", resultado.getCodPart());
        assertEquals("Control", resultado.getGrupo());
        
        // Verificamos que se llamó al query nativo
        verify(repo).actualizarCodigo(idNuevoEsperado, idViejo);
    }

    @Test
    @DisplayName("Actualizar - Error si ID no existe")
    void actualizar_NoExiste_LanzaExcepcion() {
        String id = "XX000";
        when(repo.findById(id)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> 
            servicio.actualizar(id, new Participantecrf())
        );
    }

    @Test
    void eliminar_LlamaAlRepo() {
        servicio.eliminar("CS001");
        verify(repo).deleteById("CS001");
    }
    
    @Test
    void listar_LlamaAlRepo() {
        servicio.listar();
        verify(repo).findAll();
    }
}