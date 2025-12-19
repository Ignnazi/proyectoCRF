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
        when(repo.existsById(1)).thenReturn(true);
        assertDoesNotThrow(() -> servicio.eliminar(1));
        verify(repo).deleteById(1);
    }

    @Test
    void eliminar_DeberiaLanzar404SiNoExiste() {
        when(repo.existsById(99)).thenReturn(false);
        assertThrows(ResponseStatusException.class, () -> servicio.eliminar(99));
        verify(repo, never()).deleteById(anyInt());
    }


    @Test
    void actualizar_DeberiaActualizarCampos() {
        // DATOS
        Helicobacter db = new Helicobacter();
        
        db.setResultadoExam("Negativo");
        
        Helicobacter cambios = new Helicobacter();
        cambios.setResultadoExam("Positivo");

        // MOCK
        when(repo.findById(1)).thenReturn(Optional.of(db));
        when(repo.save(any(Helicobacter.class))).thenReturn(db);

        // EJECUCIÓN
        
        servicio.actualizar(1, cambios);
        
        // VERIFICACIÓN
        
        assertEquals("Positivo", db.getResultadoExam());
        verify(repo).save(db);
    }
}