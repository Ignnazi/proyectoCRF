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

import ingsof.entidad.Helicobacter;
import ingsof.repositorio.HelicobacterR;
import ingsof.servicio.HelicobacterS;

@ExtendWith(MockitoExtension.class)
class HelicobacterST {

    @Mock
    private HelicobacterR repo;

    @InjectMocks
    private HelicobacterS servicio;

    // --- PRUEBAS DE CREACIÓN Y ELIMINACIÓN ---

    @Test
    @DisplayName("Guardar: Debería llamar al repositorio")
    void guardar_DeberiaLlamarRepo() {
        Helicobacter h = new Helicobacter();
        h.setPrueba("Aliento");
        
        servicio.guardar(h);
        
        verify(repo).save(h);
    }

    @Test
    @DisplayName("Eliminar: Debería borrar por ID")
    void eliminar_DeberiaLlamarRepo() {
        int id = 10;
        servicio.eliminar(id);
        verify(repo).deleteById(id);
    }

    // --- PRUEBAS DE LECTURA ---

    @Test
    @DisplayName("Listar: Debería retornar todos los registros")
    void listar_DeberiaRetornarLista() {
        when(repo.findAll()).thenReturn(Arrays.asList(new Helicobacter(), new Helicobacter()));
        List<Helicobacter> lista = servicio.listar();
        assertEquals(2, lista.size());
    }

    @Test
    @DisplayName("Obtener: Debería retornar registro si existe")
    void obtener_DeberiaRetornarHelicobacter() {
        int id = 1;
        Helicobacter h = new Helicobacter();
        h.setIdHelic(id);
        
        when(repo.findById(id)).thenReturn(Optional.of(h));

        Optional<Helicobacter> resultado = servicio.obtener(id);

        assertTrue(resultado.isPresent());
        assertEquals(id, resultado.get().getIdHelic());
    }

    // --- PRUEBAS DE ACTUALIZACIÓN ---

    @Test
    @DisplayName("Actualizar: Debería actualizar campos específicos si existe")
    void actualizar_DeberiaActualizarCampos() {
        // Arrange
        int id = 1;
        
        // Objeto original en base de datos
        Helicobacter existente = new Helicobacter();
        existente.setIdHelic(id);
        existente.setPrueba("Endoscopía");
        existente.setResultado("Negativo");
        existente.setAntiguedad("Nunca");

        // Objeto con cambios (simulando lo que llega del frontend)
        Helicobacter cambios = new Helicobacter();
        cambios.setPrueba("Aliento");
        cambios.setResultado("Positivo");
        cambios.setAntiguedad("<1 año");

        when(repo.findById(id)).thenReturn(Optional.of(existente));
        
        // Interceptamos el save para ver qué se está guardando
        when(repo.save(any(Helicobacter.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        servicio.actualizar(id, cambios);

        // Assert
        // Verificamos que el objeto 'existente' haya recibido los nuevos valores
        assertEquals("Aliento", existente.getPrueba());
        assertEquals("Positivo", existente.getResultado());
        assertEquals("<1 año", existente.getAntiguedad());
        
        verify(repo).save(existente);
    }

    @Test
    @DisplayName("Actualizar: No debería hacer nada si el ID no existe")
    void actualizar_NoDeberiaHacerNada_SiNoExiste() {
        // Arrange
        int id = 99;
        when(repo.findById(id)).thenReturn(Optional.empty());

        // Act
        servicio.actualizar(id, new Helicobacter());

        // Assert
        // Verificamos que save() NUNCA sea llamado, protegiendo la integridad de datos
        verify(repo, never()).save(any());
    }
}