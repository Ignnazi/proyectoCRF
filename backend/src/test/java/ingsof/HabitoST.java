package ingsof;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import ingsof.entidad.Habito;
import ingsof.repositorio.HabitoR;
import ingsof.servicio.HabitoS;

@ExtendWith(MockitoExtension.class)
class HabitoST {

    @Mock private HabitoR repo;
    @InjectMocks private HabitoS servicio;

    @Test
    void actualizar_DeberiaActualizarValores() {
        Habito db = new Habito();
        db.setTipo("Fumar");
        db.setEstado("Actual");

        Habito cambios = new Habito();
        cambios.setEstado("Ex");
        cambios.setTiempoDejado("1 año");

        when(repo.findById(10)).thenReturn(Optional.of(db));

        servicio.actualizar(10, cambios);

        verify(repo).save(db);
        assertEquals("Ex", db.getEstado());
        assertEquals("1 año", db.getTiempoDejado());
    }
}
