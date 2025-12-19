package ingsof;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

// @ExtendWith inicializa los Mocks sin necesidad de levantar todo Spring Boot (es muy rápido)
@ExtendWith(MockitoExtension.class)
class AntecedentesST {

    @Mock
    private AntecedenteR repo;

    @Mock
    private ParticipantecrfR partRepo;

    @InjectMocks
    private AntecedenteS servicio;

    // --- PRUEBAS PARA LISTAR ---

    @Test
    @DisplayName("Listar: Debería retornar una lista de antecedentes")
    void listar_DeberiaRetornarLista() {
        // Arrange
        Antecedente a1 = new Antecedente();
        Antecedente a2 = new Antecedente();
        when(repo.findAll()).thenReturn(Arrays.asList(a1, a2));

        // Act
        List<Antecedente> resultado = servicio.listar();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(repo).findAll(); // Verifica que se llamó al repo
    }

    // --- PRUEBAS PARA POR ID ---

    @Test
    @DisplayName("PorId: Debería retornar antecedente si existe")
    void porId_DeberiaRetornarAntecedente_CuandoExiste() {
        // Arrange
        Integer id = 1;
        Antecedente a = new Antecedente();
        a.setIdAntec(id);
        when(repo.findById(id)).thenReturn(Optional.of(a));

        // Act
        Antecedente resultado = servicio.porId(id);

        // Assert
        assertEquals(id, resultado.getIdAntec());
    }

    @Test
    @DisplayName("PorId: Debería lanzar excepción si no existe")
    void porId_DeberiaLanzarExcepcion_CuandoNoExiste() {
        // Arrange
        Integer id = 99;
        when(repo.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        // Verificamos que lance ResponseStatusException
        assertThrows(ResponseStatusException.class, () -> servicio.porId(id));
    }

    // --- PRUEBAS PARA CREAR (LÓGICA DE NEGOCIO) ---

    @Test
    @DisplayName("Crear: Debería guardar exitosamente cuando los datos son válidos")
    void crear_DeberiaGuardar_CuandoDatosValidos() {
        // Arrange
        String codPart = "P001";
        Antecedente nuevo = new Antecedente();
        nuevo.setCodPart(codPart);
        nuevo.setDiagnostico("No"); // Seguro para cualquier grupo

        Participantecrf participante = new Participantecrf();
        participante.setGrupo("Control"); // Simulamos que es Control

        when(partRepo.findById(codPart)).thenReturn(Optional.of(participante));
        when(repo.save(any(Antecedente.class))).thenReturn(nuevo);

        // Act
        Antecedente resultado = servicio.crear(nuevo);

        // Assert
        assertNotNull(resultado);
        verify(repo).save(nuevo);
    }

    @Test
    @DisplayName("Crear: Debería fallar si NO es 'Caso' pero tiene Diagnóstico 'Sí'")
    void crear_DeberiaFallar_SiControlTieneDiagnostico() {
        // Arrange
        String codPart = "P002";
        Antecedente nuevo = new Antecedente();
        nuevo.setCodPart(codPart);
        nuevo.setDiagnostico("Sí"); // ESTO DEBERÍA FALLAR EN CONTROLES

        Participantecrf participante = new Participantecrf();
        participante.setGrupo("Control"); // Es control, no Caso

        when(partRepo.findById(codPart)).thenReturn(Optional.of(participante));

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> servicio.crear(nuevo));
        // Verificamos que NUNCA se llame a guardar
        verify(repo, never()).save(any());
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

        when(partRepo.findById(codPart)).thenReturn(Optional.of(participante));

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> servicio.crear(nuevo));
        verify(repo, never()).save(any());
    }

    // --- PRUEBAS PARA ACTUALIZAR ---

    @Test
    @DisplayName("Actualizar: Debería actualizar campos correctamente")
    void actualizar_DeberiaActualizarCampos() {
        // Arrange
        Integer id = 1;
        String codPart = "P001";
        
        // Objeto existente en BD
        Antecedente existente = new Antecedente();
        existente.setIdAntec(id);
        existente.setCodPart(codPart);
        
        // Objeto con los cambios solicitados
        Antecedente cambios = new Antecedente();
        cambios.setCirugia("Nueva Cirugía");

        Participantecrf participante = new Participantecrf();
        participante.setGrupo("Caso"); // Es Caso, permite todo

        when(repo.findById(id)).thenReturn(Optional.of(existente));
        when(partRepo.findById(codPart)).thenReturn(Optional.of(participante));
        when(repo.save(any(Antecedente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Antecedente resultado = servicio.actualizar(id, cambios);

        // Assert
        assertEquals("Nueva Cirugía", resultado.getCirugia()); // Se actualizó
        verify(repo).save(existente);
    }
}