package ingsof;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import ingsof.entidad.Antropometria;
import ingsof.repositorio.AntropometriaR;
import ingsof.repositorio.ParticipantecrfR;
import ingsof.servicio.AntropometriaS;

@ExtendWith(MockitoExtension.class)
class AntropometriaST {

    @Mock private AntropometriaR repo;
    @Mock private ParticipantecrfR partRepo;

    @InjectMocks private AntropometriaS servicio;

    @Test
    void crear_DeberiaCalcularIMC() {
        // DATOS
        Antropometria a = new Antropometria();
        a.setCodPart("CS001");
        a.setPeso(80.0);
        a.setEstatura(1.80); // IMC esperado: 80 / 3.24 = 24.691... -> Redondeado 24.7

        when(partRepo.existsById("CS001")).thenReturn(true);
        when(repo.save(any(Antropometria.class))).thenAnswer(i -> i.getArguments()[0]);

        // EJECUCIÓN
        Antropometria guardado = servicio.crear(a);

        // VERIFICACIÓN
        assertEquals(24.7, guardado.getImc());
    }

    @Test
    void crear_DeberiaLanzarErrorSiPesoEsCeroONegativo() {
        Antropometria a = new Antropometria();
        a.setCodPart("CS001");
        a.setPeso(0.0);
        a.setEstatura(1.80);

        when(partRepo.existsById("CS001")).thenReturn(true);

        assertThrows(ResponseStatusException.class, () -> {
            servicio.crear(a);
        });
    }

    @Test
    void crear_DeberiaFallarSiParticipanteNoExiste() {
        Antropometria a = new Antropometria();
        a.setCodPart("XX999");
        
        when(partRepo.existsById("XX999")).thenReturn(false);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            servicio.crear(a);
        });
        assertEquals("400 BAD_REQUEST \"participante no encontrado\"", ex.getMessage());
    }
}