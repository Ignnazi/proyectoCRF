package ingsof;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

import ingsof.entidad.Factor;
import ingsof.repositorio.FactoresR;
import ingsof.servicio.FactorS;

@ExtendWith(MockitoExtension.class)
class FactorST {

    @Mock
    private FactoresR repo;

    @InjectMocks
    private FactorS servicio;

    // --- TEST DE GUARDAR ---

    @Test
    @DisplayName("Guardar - Debería llamar al repositorio correctamente")
    void guardar_LlamaAlRepo() {
        // Arrange
        Factor factor = new Factor();
        factor.setCodPart("PART-123");
        factor.setCarnes("Raras veces");

        // Act
        servicio.guardar(factor);

        // Assert
        verify(repo, times(1)).save(factor);
    }

    // --- TESTS DE OBTENER ---

    @Test
    @DisplayName("ObtenerPorId - Debería devolver el factor si existe")
    void obtenerPorId_Existe_DevuelveFactor() {
        // Arrange
        int id = 1;
        Factor factorSimulado = new Factor();
        factorSimulado.setIdFac(id);
        
        when(repo.findById(id)).thenReturn(Optional.of(factorSimulado));

        // Act
        Optional<Factor> resultado = servicio.obtenerPorId(id);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(id, resultado.get().getIdFac());
    }

    @Test
    @DisplayName("ObtenerPorId - Debería devolver vacío si no existe")
    void obtenerPorId_NoExiste_DevuelveVacio() {
        int id = 99;
        when(repo.findById(id)).thenReturn(Optional.empty());

        Optional<Factor> resultado = servicio.obtenerPorId(id);

        assertFalse(resultado.isPresent());
    }

    @Test
    @DisplayName("ObtenerTodos - Debería devolver una lista de factores")
    void obtenerTodos_DevuelveLista() {
        // Arrange
        List<Factor> listaSimulada = Arrays.asList(new Factor(), new Factor());
        when(repo.findAll()).thenReturn(listaSimulada);

        // Act
        List<Factor> resultado = servicio.obtenerTodos();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
    }

    // --- TESTS DE ACTUALIZAR ---

    @Test
    @DisplayName("Actualizar - Debería modificar los campos y guardar si el ID existe")
    void actualizar_IdExiste_ActualizaCampos() {
        // Arrange
        int id = 10;
        
        // El factor que "ya existe" en la BD
        Factor factorExistente = new Factor();
        factorExistente.setIdFac(id);
        factorExistente.setCarnes("Nunca"); // Valor antiguo
        factorExistente.setQuimicos("No");  // Valor antiguo

        // Los datos nuevos que llegan del frontend/controlador
        Factor factorNuevosDatos = new Factor();
        factorNuevosDatos.setCarnes("Diario"); // Nuevo valor
        factorNuevosDatos.setQuimicos("Sí");   // Nuevo valor
        factorNuevosDatos.setDetalleQuimicos("Cloro");

        when(repo.findById(id)).thenReturn(Optional.of(factorExistente));

        // Act
        servicio.actualizarPorId(id, factorNuevosDatos);

        // Assert
        // Verificamos que el objeto existente se haya modificado
        assertEquals("Diario", factorExistente.getCarnes());
        assertEquals("Sí", factorExistente.getQuimicos());
        assertEquals("Cloro", factorExistente.getDetalleQuimicos());
        
        // Verificamos que se llame a save con el objeto modificado
        verify(repo).save(factorExistente);
    }

    @Test
    @DisplayName("Actualizar - No debería hacer nada si el ID no existe")
    void actualizar_IdNoExiste_NoGuarda() {
        int id = 99;
        Factor factorNuevosDatos = new Factor();
        
        when(repo.findById(id)).thenReturn(Optional.empty());

        servicio.actualizarPorId(id, factorNuevosDatos);

        verify(repo, never()).save(any());
    }

    // --- TEST DE ELIMINAR ---

    @Test
    @DisplayName("Eliminar - Debería llamar al repositorio con el ID correcto")
    void eliminar_LlamaDeleteById() {
        int id = 5;
        servicio.eliminar(id);
        verify(repo, times(1)).deleteById(id);
    }
}