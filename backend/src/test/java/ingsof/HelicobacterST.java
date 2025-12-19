package ingsof;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import ingsof.entidad.Helicobacter;
import ingsof.repositorio.HelicobacterR;
import ingsof.servicio.HelicobacterS;

@ExtendWith(MockitoExtension.class)
class HelicobacterST {

    @Mock private HelicobacterR repo;
    @InjectMocks private HelicobacterS servicio;

    @Test
    void eliminar_DeberiaBorrarSiExiste() {
        // MOCK: Simulamos que el ID 1 existe
        when(repo.existsById(1)).thenReturn(true);
        
        // EJECUCIÓN
        // Si tu método se llama 'borrar' o 'delete', cambia '.eliminar' aquí abajo:
        assertDoesNotThrow(() -> servicio.porId(1));
        
        // VERIFICACIÓN: Comprobamos que se llamó al repositorio
        verify(repo).deleteById(1);
    }

    @Test
    void eliminar_DeberiaLanzar404SiNoExiste() {
        // MOCK: Simulamos que el ID 99 NO existe
        when(repo.existsById(99)).thenReturn(false);

        // EJECUCIÓN: Esperamos un error
        assertThrows(ResponseStatusException.class, () -> servicio.porId(99));
        
        // VERIFICACIÓN: Aseguramos que NUNCA se intentó borrar nada en la BD
        verify(repo, never()).deleteById(anyInt());
    }

    @Test
    void actualizar_DeberiaActualizarCampos() {
        // DATOS
        Helicobacter db = new Helicobacter();
        // Si .setResultado te marca error, verifica tu entidad Helicobacter.java
        // Asegúrate que tenga los métodos get/set públicos.
        db.setResultadoExam("Negativo");
        
        Helicobacter cambios = new Helicobacter();
        cambios.setResultadoExam("Positivo");

        // MOCK
        when(repo.findById(1)).thenReturn(Optional.of(db));
        // Nota: No necesitamos el return del save() para esta prueba simplificada
        when(repo.save(any(Helicobacter.class))).thenReturn(db);

        // EJECUCIÓN
        // Si tu servicio devuelve void, simplemente llamamos al método sin asignar a variable
        servicio.actualizar(1, cambios);
        
        // VERIFICACIÓN
        // Verificamos que el objeto 'db' se haya modificado con el valor nuevo
        assertEquals("Positivo", db.getResultadoExam());
        
        // Verificamos que se haya llamado a guardar
        verify(repo).save(db);
    }
}