package ingsof;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

import ingsof.entidad.Habito;
import ingsof.repositorio.HabitoR;
import ingsof.servicio.HabitoS;

@ExtendWith(MockitoExtension.class)
class HabitoST {

    @Mock
    private HabitoR repo;

    @InjectMocks
    private HabitoS servicio;

    // --- PRUEBAS DE CREACIÓN Y ELIMINACIÓN ---

    @Test
    @DisplayName("Guardar: Debería llamar al repositorio")
    void guardar_DeberiaLlamarRepo() {
        Habito h = new Habito();
        h.setTipo("Fumar");
        
        servicio.guardar(h);
        
        verify(repo).save(h);
    }

    @Test
    @DisplayName("Eliminar: Debería llamar al repositorio con el ID correcto")
    void eliminar_DeberiaLlamarRepo() {
        int id = 5;
        servicio.eliminar(id);
        verify(repo).deleteById(id);
    }

    // --- PRUEBAS DE LECTURA ---

    @Test
    @DisplayName("Listar: Debería retornar lista de hábitos")
    void listar_DeberiaRetornarLista() {
        when(repo.findAll()).thenReturn(Arrays.asList(new Habito(), new Habito()));
        List<Habito> lista = servicio.listar();
        assertEquals(2, lista.size());
    }

    @Test
    @DisplayName("ObtenerPorId: Debería retornar hábito si existe")
    void obtenerPorId_DeberiaRetornarHabito() {
        int id = 1;
        Habito h = new Habito();
        h.setIdHabit(id);
        
        when(repo.findById(id)).thenReturn(Optional.of(h));

        Optional<Habito> resultado = servicio.obtenerPorId(id);

        assertTrue(resultado.isPresent());
        assertEquals(id, resultado.get().getIdHabit());
    }

    // --- PRUEBAS DE ACTUALIZACIÓN (La parte más importante) ---

    @Test
    @DisplayName("Actualizar: Debería actualizar TODOS los campos correctamente")
    void actualizar_DeberiaActualizarTodosLosCampos() {
        // Arrange
        int id = 1;
        
        // 1. Datos originales en BD
        Habito existente = new Habito();
        existente.setIdHabit(id);
        existente.setTipo("Fumar");
        existente.setEstado("Actual");
        existente.setCantidad("1 cajetilla");

        // 2. Datos nuevos que llegan del frontend
        Habito cambios = new Habito();
        cambios.setTipo("Dejar de Fumar");
        cambios.setEstado("Ex");
        cambios.setAniosConsumo("10 años");
        cambios.setCantidad("0");
        cambios.setTiempoDejado("1 mes");
        cambios.setFrecuencia("Diaria");
        cambios.setEdadInicio(18);

        when(repo.findById(id)).thenReturn(Optional.of(existente));
        
        // Mockeamos save para inspeccionar qué se está guardando
        when(repo.save(any(Habito.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        servicio.actualizar(id, cambios);

        // Assert
        // Verificamos campo por campo para asegurar que no hubo error de mapeo
        assertEquals("Dejar de Fumar", existente.getTipo());
        assertEquals("Ex", existente.getEstado());
        assertEquals("10 años", existente.getAniosConsumo());
        assertEquals("0", existente.getCantidad());
        assertEquals("1 mes", existente.getTiempoDejado());
        assertEquals("Diaria", existente.getFrecuencia());
        assertEquals(18, existente.getEdadInicio());
        
        verify(repo).save(existente);
    }

    @Test
    @DisplayName("Actualizar: No debería hacer nada si el ID no existe")
    void actualizar_NoDeberiaHacerNada_SiNoExiste() {
        // Arrange
        int id = 99;
        when(repo.findById(id)).thenReturn(Optional.empty());

        // Act
        servicio.actualizar(id, new Habito());

        // Assert
        // Verificamos que NUNCA se llame a save(), protegiendo la integridad de la BD
        verify(repo, never()).save(any());
    }
}
