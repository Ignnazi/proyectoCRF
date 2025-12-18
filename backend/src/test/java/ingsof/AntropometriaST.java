package ingsof;

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

import ingsof.entidad.Antropometria;
import ingsof.repositorio.AntropometriaR;
import ingsof.repositorio.ParticipantecrfR;
import ingsof.servicio.AntropometriaS;

@ExtendWith(MockitoExtension.class)
class AntropometriaST {

    @Mock
    private AntropometriaR repo;

    @Mock
    private ParticipantecrfR partRepo;

    @InjectMocks
    private AntropometriaS servicio;

    // --- PRUEBAS DE LISTADO ---

    @Test
    @DisplayName("Listar: Debería retornar lista completa")
    void listar_DeberiaRetornarLista() {
        when(repo.findAll()).thenReturn(Arrays.asList(new Antropometria(), new Antropometria()));
        List<Antropometria> resultado = servicio.listar();
        assertEquals(2, resultado.size());
    }

    @Test
    @DisplayName("Por Participante: Debería filtrar correctamente")
    void porParticipante_DeberiaFiltrar() {
        String codPart = "P001";
        when(repo.findAllByCodPart(codPart)).thenReturn(List.of(new Antropometria()));
        
        var resultado = servicio.porParticipante(codPart);
        
        assertNotNull(resultado);
        verify(repo).findAllByCodPart(codPart);
    }

    // --- PRUEBAS DE CÁLCULO DE IMC (Lógica Crítica) ---

    @Test
    @DisplayName("Crear: Debería calcular IMC correctamente y redondear")
    void crear_DeberiaCalcularImc() {
        // Arrange
        String codPart = "P001";
        Antropometria nuevo = new Antropometria();
        nuevo.setCodPart(codPart);
        nuevo.setPeso(70.0);
        nuevo.setEstatura(1.75);
        // Cálculo esperado: 70 / (1.75 * 1.75) = 22.857... -> Redondeado a 22.9

        when(partRepo.existsById(codPart)).thenReturn(true);
        when(repo.save(any(Antropometria.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Antropometria resultado = servicio.crear(nuevo);

        // Assert
        assertEquals(22.9, resultado.getImc()); // Verificamos la matemática
        verify(repo).save(nuevo);
    }

    @Test
    @DisplayName("Crear: Debería fallar si peso o estatura son inválidos")
    void crear_DeberiaFallar_DatosInvalidos() {
        // Arrange
        Antropometria datosMalos = new Antropometria();
        datosMalos.setCodPart("P001");
        datosMalos.setPeso(0.0); // Peso inválido
        datosMalos.setEstatura(1.70);

        when(partRepo.existsById("P001")).thenReturn(true);

        // Act & Assert
        // Esperamos BAD_REQUEST por la validación en calcImc
        assertThrows(ResponseStatusException.class, () -> servicio.crear(datosMalos));
    }

    @Test
    @DisplayName("Crear: Debería fallar si participante no existe")
    void crear_DeberiaFallar_SinParticipante() {
        Antropometria nuevo = new Antropometria();
        nuevo.setCodPart("FANTASMA");
        
        when(partRepo.existsById("FANTASMA")).thenReturn(false);

        assertThrows(ResponseStatusException.class, () -> servicio.crear(nuevo));
        verify(repo, never()).save(any());
    }

    // --- PRUEBAS DE ACTUALIZACIÓN ---

    @Test
    @DisplayName("Actualizar: Debería recalcular IMC si cambia el peso")
    void actualizar_DeberiaRecalcularImc() {
        // Arrange
        Integer id = 1;
        
        // Datos actuales en BD (Peso 70, Estatura 1.75, IMC 22.9)
        Antropometria db = new Antropometria();
        db.setIdAntrop(id);
        db.setPeso(70.0);
        db.setEstatura(1.75); 
        db.setImc(22.9);

        // Datos nuevos (Subió a 80kg, estatura null/mantiene)
        Antropometria cambios = new Antropometria();
        cambios.setPeso(80.0); 

        when(repo.findById(id)).thenReturn(Optional.of(db));
        when(repo.save(any(Antropometria.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Antropometria resultado = servicio.actualizar(id, cambios);

        // Assert
        assertEquals(80.0, resultado.getPeso()); // Peso nuevo
        assertEquals(1.75, resultado.getEstatura()); // Estatura vieja
        // Nuevo cálculo: 80 / (1.75*1.75) = 26.122... -> Redondeado 26.1
        assertEquals(26.1, resultado.getImc()); 
    }

    @Test
    @DisplayName("Actualizar: Debería validar participante si este cambia")
    void actualizar_DeberiaValidarParticipanteNuevo() {
        // Arrange
        Integer id = 1;
        Antropometria db = new Antropometria();
        db.setPeso(70.0); db.setEstatura(1.70); // Datos dummy para calcImc
        
        Antropometria cambios = new Antropometria();
        cambios.setCodPart("NUEVO_PART");

        when(repo.findById(id)).thenReturn(Optional.of(db));
        when(partRepo.existsById("NUEVO_PART")).thenReturn(false); // No existe

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> servicio.actualizar(id, cambios));
    }
}