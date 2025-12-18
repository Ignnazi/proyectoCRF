package ingsof;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import ingsof.entidad.Genotipo;
import ingsof.repositorio.GenotipoR;
import ingsof.servicio.GenotipoS;

@ExtendWith(MockitoExtension.class)
class GenotipoST {

    @Mock
    private GenotipoR repo;

    @InjectMocks
    private GenotipoS servicio;

    // --- PRUEBAS DE CREACIÓN (GUARDAR) ---

    @Test
    @DisplayName("Guardar: Debería guardar y asignar fecha actual si viene nula")
    void guardar_DeberiaAsignarFechaYGuardar() {
        // Arrange
        Genotipo nuevo = new Genotipo();
        nuevo.setTlr9Rs5743836("TT"); // Valor válido
        
        when(repo.save(any(Genotipo.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Genotipo resultado = servicio.guardar(nuevo);

        // Assert
        assertNotNull(resultado.getFechaToma(), "La fecha no debería ser nula");
        assertEquals(LocalDate.now(), resultado.getFechaToma(), "Debería asignar la fecha de hoy");
        verify(repo).save(nuevo);
    }

    @Test
    @DisplayName("Guardar: Debería fallar con valor de genotipo inválido")
    void guardar_DeberiaFallar_ValorInvalido() {
        // Arrange
        Genotipo invalido = new Genotipo();
        invalido.setTlr9Rs5743836("XX"); // "XX" no está en la lista permitida

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> servicio.guardar(invalido));
            
        assertTrue(exception.getMessage().contains("debe tener uno de los siguientes valores"));
        verify(repo, never()).save(any());
    }

    @Test
    @DisplayName("Guardar: Debería permitir todos los valores válidos")
    void guardar_DeberiaPermitirValoresValidos() {
        // Probamos la lista permitida: "TT", "TC", "CC", "GG", "GC", "GT"
        List<String> validos = Arrays.asList("TT", "TC", "CC", "GG", "GC", "GT");

        for (String valor : validos) {
            Genotipo g = new Genotipo();
            g.setTlr9Rs5743836(valor);
            servicio.guardar(g); // No debería lanzar excepción
        }
        
        verify(repo, times(validos.size())).save(any());
    }

    // --- PRUEBAS DE ACTUALIZACIÓN ---

    @Test
    @DisplayName("Actualizar: Debería actualizar solo campos no nulos")
    void actualizar_DeberiaActualizarParcialmente() {
        // Arrange
        int id = 1;
        Genotipo existente = new Genotipo();
        existente.setIdGenotip(id);
        existente.setTlr9Rs5743836("TT"); // Valor original

        Genotipo cambios = new Genotipo();
        cambios.setTlr9Rs5743836("GG"); // Cambio válido
        // Los demás campos en 'cambios' son null, no deberían tocarse

        when(repo.findById(id)).thenReturn(Optional.of(existente));
        when(repo.save(any(Genotipo.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Genotipo resultado = servicio.actualizar(id, cambios);

        // Assert
        assertEquals("GG", resultado.getTlr9Rs5743836());
        verify(repo).save(existente);
    }

    @Test
    @DisplayName("Actualizar: Debería fallar si ID no existe")
    void actualizar_DeberiaFallar_SiNoExiste() {
        int id = 99;
        when(repo.findById(id)).thenReturn(Optional.empty());

        // Tu código lanza RuntimeException en este caso
        RuntimeException ex = assertThrows(RuntimeException.class, 
            () -> servicio.actualizar(id, new Genotipo()));
            
        assertEquals("Genotipo no encontrado con id: " + id, ex.getMessage());
    }

    @Test
    @DisplayName("Actualizar: Debería validar valores antes de guardar")
    void actualizar_DeberiaValidarValores() {
        // Arrange
        int id = 1;
        Genotipo existente = new Genotipo();
        when(repo.findById(id)).thenReturn(Optional.of(existente));

        Genotipo cambiosInvalidos = new Genotipo();
        cambiosInvalidos.setMthfrRs1801133("ZZ"); // Inválido

        // Act & Assert
        assertThrows(IllegalArgumentException.class, 
            () -> servicio.actualizar(id, cambiosInvalidos));
            
        verify(repo, never()).save(any());
    }

    // --- PRUEBAS DE LECTURA Y ELIMINACIÓN ---

    @Test
    @DisplayName("ObtenerPorId: Debería retornar opcional")
    void obtenerPorId_Test() {
        when(repo.findById(1)).thenReturn(Optional.of(new Genotipo()));
        assertTrue(servicio.obtenerPorId(1).isPresent());
    }

    @Test
    @DisplayName("Eliminar: Debería llamar al repositorio")
    void eliminar_Test() {
        servicio.eliminar(5);
        verify(repo).deleteById(5);
    }
}