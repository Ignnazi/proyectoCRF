package ingsof;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

import ingsof.entidad.Sociodemo;
import ingsof.repositorio.SociodemoR;
import ingsof.servicio.SociodemoS;

@ExtendWith(MockitoExtension.class)
class SociodemoST {

    @Mock
    private SociodemoR repo;

    @InjectMocks
    private SociodemoS servicio;

    // --- PRUEBAS DE GUARDADO (VALIDACIONES) ---

    @Test
    @DisplayName("Guardar: Debería guardar exitosamente si es mayor de edad")
    void guardar_Exito() {
        // Arrange
        Sociodemo s = new Sociodemo();
        s.setEdad(25); // Edad válida
        
        // Act
        servicio.guardar(s);

        // Assert
        verify(repo).save(s);
    }

    @Test
    @DisplayName("Guardar: Debería fallar si es menor de 18 años")
    void guardar_Falla_MenorEdad() {
        // Arrange
        Sociodemo s = new Sociodemo();
        s.setEdad(17); // Inválido

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, 
            () -> servicio.guardar(s));
            
        assertEquals("La edad debe ser mayor o igual a 18", ex.getMessage());
        verify(repo, never()).save(any());
    }

    @Test
    @DisplayName("Guardar: Debería fallar si la edad es nula")
    void guardar_Falla_EdadNula() {
        // Arrange
        Sociodemo s = new Sociodemo();
        s.setEdad(null);

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, 
            () -> servicio.guardar(s));

        assertEquals("La edad no puede ser nula", ex.getMessage());
        verify(repo, never()).save(any());
    }

    // --- PRUEBAS DE ACTUALIZACIÓN ---

    @Test
    @DisplayName("Actualizar: Debería mapear todos los campos correctamente")
    void actualizar_DeberiaActualizarTodo() {
        // Arrange
        int id = 1;
        
        // Objeto original en BD
        Sociodemo existente = new Sociodemo();
        existente.setIdSocdemo(id);
        existente.setEdad(20);
        existente.setZona("Rural");

        // Objeto con cambios
        Sociodemo cambios = new Sociodemo();
        cambios.setEdad(21);
        cambios.setSexo("Mujer");
        cambios.setZona("Urbana");
        cambios.setNacionalidad("Chilena");
        cambios.setAniosRes(">10");
        cambios.setDireccion("Calle Nueva 123");
        cambios.setEducacion("Superior");
        cambios.setOcupacion("Ingeniera");

        when(repo.findById(id)).thenReturn(Optional.of(existente));
        when(repo.save(any(Sociodemo.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        servicio.actualizar(id, cambios);

        // Assert - Verificamos campo por campo
        assertEquals(21, existente.getEdad());
        assertEquals("Mujer", existente.getSexo());
        assertEquals("Urbana", existente.getZona());
        assertEquals("Chilena", existente.getNacionalidad());
        assertEquals(">10", existente.getAniosRes());
        assertEquals("Calle Nueva 123", existente.getDireccion());
        assertEquals("Superior", existente.getEducacion());
        assertEquals("Ingeniera", existente.getOcupacion());
        
        verify(repo).save(existente);
    }

    @Test
    @DisplayName("Actualizar: No debería hacer nada si ID no existe")
    void actualizar_NoExiste() {
        int id = 99;
        when(repo.findById(id)).thenReturn(Optional.empty());

        servicio.actualizar(id, new Sociodemo());

        verify(repo, never()).save(any());
    }

    // --- PRUEBAS CRUD BÁSICAS ---

    @Test
    @DisplayName("Eliminar: Debería llamar al repositorio")
    void eliminar_Test() {
        servicio.eliminar(1);
        verify(repo).deleteById(1);
    }

    @Test
    @DisplayName("Listar y Obtener: Deberían retornar datos")
    void lectura_Test() {
        // Listar
        when(repo.findAll()).thenReturn(Arrays.asList(new Sociodemo()));
        assertFalse(servicio.listar().isEmpty());

        // Obtener
        when(repo.findById(1)).thenReturn(Optional.of(new Sociodemo()));
        assertTrue(servicio.obtener(1).isPresent());
    }
}