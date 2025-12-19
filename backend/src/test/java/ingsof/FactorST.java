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

import ingsof.entidad.Factor;
import ingsof.repositorio.FactoresR;
import ingsof.servicio.FactorS;

@ExtendWith(MockitoExtension.class)
class FactorST {

    @Mock
    private FactoresR repo;

    @InjectMocks
    private FactorS servicio;

    // --- PRUEBAS DE CREACIÓN Y ELIMINACIÓN ---

    @Test
    @DisplayName("Guardar: Debería llamar al repositorio")
    void guardar_DeberiaLlamarRepo() {
        Factor factor = new Factor();
        servicio.guardar(factor);
        verify(repo).save(factor);
    }

    @Test
    @DisplayName("Eliminar: Debería llamar al repositorio por ID")
    void eliminar_DeberiaLlamarRepo() {
        int id = 1;
        servicio.eliminar(id);
        verify(repo).deleteById(id);
    }

    // --- PRUEBAS DE LECTURA ---

    @Test
    @DisplayName("ObtenerPorId: Debería retornar factor si existe")
    void obtenerPorId_DeberiaRetornarFactor() {
        int id = 1;
        Factor f = new Factor();
        f.setIdFac(id);
        
        when(repo.findById(id)).thenReturn(Optional.of(f));

        Optional<Factor> resultado = servicio.obtenerPorId(id);

        assertTrue(resultado.isPresent());
        assertEquals(id, resultado.get().getIdFac());
    }

    @Test
    @DisplayName("ObtenerTodos: Debería retornar lista")
    void obtenerTodos_DeberiaRetornarLista() {
        when(repo.findAll()).thenReturn(Arrays.asList(new Factor(), new Factor()));
        List<Factor> lista = servicio.obtenerTodos();
        assertEquals(2, lista.size());
    }

    // --- PRUEBAS DE ACTUALIZACIÓN ---

    @Test
    @DisplayName("Actualizar: Debería actualizar todos los campos si existe")
    void actualizarPorId_DeberiaActualizarCampos() {
        // Arrange
        int id = 10;
        
        // Objeto original en BD
        Factor existente = new Factor();
        existente.setIdFac(id);
        existente.setCarnes("No");
        existente.setFrutas("A veces");

        // Objeto con nuevos datos
        Factor nuevosDatos = new Factor();
        nuevosDatos.setCarnes("Sí");
        nuevosDatos.setFrutas("Siempre");
        nuevosDatos.setSalados("Nunca");
        nuevosDatos.setFrituras("Frecuente");
        nuevosDatos.setQuimicos("No");
        nuevosDatos.setHumoLena("Sí");
        nuevosDatos.setPesticidas("No");
        nuevosDatos.setFuenteAgua("Pozo");
        nuevosDatos.setTratamientoAgua("Hervida");
        nuevosDatos.setDetalleQuimicos("Cloro");

        when(repo.findById(id)).thenReturn(Optional.of(existente));
        // Mockeamos save para verificar qué objeto le llega
        when(repo.save(any(Factor.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        servicio.actualizarPorId(id, nuevosDatos);

        // Assert
        // Verificamos que el objeto 'existente' haya mutado con los nuevos valores
        assertEquals("Sí", existente.getCarnes());
        assertEquals("Siempre", existente.getFrutas());
        assertEquals("Nunca", existente.getSalados());
        assertEquals("Pozo", existente.getFuenteAgua());
        assertEquals("Cloro", existente.getDetalleQuimicos());
        
        // Verificamos que se haya guardado
        verify(repo).save(existente);
    }

    @Test
    @DisplayName("Actualizar: No debería hacer nada si ID no existe")
    void actualizarPorId_NoDeberiaHacerNada_SiNoExiste() {
        int id = 99;
        when(repo.findById(id)).thenReturn(Optional.empty());

        servicio.actualizarPorId(id, new Factor());

        // Verificamos que NUNCA se llame a save
        verify(repo, never()).save(any());
    }
}