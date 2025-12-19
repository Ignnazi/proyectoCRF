package ingsof;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import ingsof.entidad.Sociodemo;
import ingsof.repositorio.SociodemoR;
import ingsof.servicio.SociodemoS;

@ExtendWith(MockitoExtension.class)
class SociodemoST {

    @Mock private SociodemoR repo;
    @InjectMocks private SociodemoS servicio;

    @Test
    void guardar_DeberiaAceptarMayoresDe18() {
        Sociodemo s = new Sociodemo();
        s.setEdad(20);

        when(repo.save(any(Sociodemo.class))).thenReturn(s);

        servicio.guardar(s);
        verify(repo).save(s);
    }

    @Test
    void guardar_DeberiaRechazarMenoresDe18() {
        Sociodemo s = new Sociodemo();
        s.setEdad(17);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            servicio.guardar(s);
        });
        assertEquals("La edad debe ser mayor o igual a 18", ex.getMessage());
    }
}