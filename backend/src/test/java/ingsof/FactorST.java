package ingsof;

import java.util.Optional;

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

    @Mock private FactoresR repo;
    @InjectMocks private FactorS servicio;

    @Test
    void actualizarPorId_DeberiaActualizarSiExiste() {
        Factor existente = new Factor();
        existente.setCarnes("Poco");

        Factor nuevosDatos = new Factor();
        nuevosDatos.setCarnes("Mucho");
        nuevosDatos.setFrutas("A veces");

        when(repo.findById(1)).thenReturn(Optional.of(existente));

        servicio.actualizarPorId(1, nuevosDatos);

        verify(repo).save(existente);
        // Verificar que el objeto existente mutó
        assert(existente.getCarnes().equals("Mucho"));
    }

    @Test
    void actualizarPorId_NoHacerNadaSiNoExiste() {
        when(repo.findById(99)).thenReturn(Optional.empty());

        servicio.actualizarPorId(99, new Factor());

        verify(repo, never()).save(any());
    }
}