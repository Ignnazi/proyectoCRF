package ingsof;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import org.springframework.web.server.ResponseStatusException;

import ingsof.entidad.Antropometria;
import ingsof.entidad.Histopatologia;
import ingsof.entidad.Participantecrf;
import ingsof.entidad.Sociodemo;
import ingsof.repositorio.AntropometriaR;
import ingsof.repositorio.HistopatologiaR;
import ingsof.repositorio.ParticipantecrfR;
import ingsof.repositorio.SociodemoR;
import ingsof.servicio.AntropometriaS;
import ingsof.servicio.HistopatologiaS;
import ingsof.servicio.SociodemoS;

@ExtendWith(MockitoExtension.class)
class ReglasNegocioST {

    @Mock SociodemoR socioRepo;
    @InjectMocks SociodemoS socioServicio;

    @Mock AntropometriaR antropRepo;
    @Mock ParticipantecrfR partRepo;
    @InjectMocks AntropometriaS antropServicio;

    @Mock HistopatologiaR histoRepo;

    // --- CASO 1: Validación de Edad en Sociodemográficos ---
    @Test
    void testGuardarSociodemo_EdadMenor18_LanzaExcepcion() {
        Sociodemo s = new Sociodemo();
        s.setEdad(15); // Menor de edad

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            socioServicio.guardar(s);
        });

        assertEquals("La edad debe ser mayor o igual a 18", exception.getMessage());
        verify(socioRepo, never()).save(any());
    }

    // --- CASO 2: Guardado Correcto Sociodemográficos ---
    @Test
    void testGuardarSociodemo_EdadValida_GuardaCorrectamente() {
        Sociodemo s = new Sociodemo();
        s.setEdad(25);

        socioServicio.guardar(s);

        verify(socioRepo, times(1)).save(s);
    }

    // --- CASO 3: Cálculo Automático de IMC en Antropometría ---
    @Test
    void testCrearAntropometria_CalculaImcCorrectamente() {
        // Datos: Peso 80kg, Altura 1.80m -> IMC debería ser ~24.7
        Antropometria a = new Antropometria();
        a.setCodPart("CS001");
        a.setPeso(80.0);
        a.setEstatura(1.80);

        when(partRepo.existsById("CS001")).thenReturn(true);
        when(antropRepo.save(any(Antropometria.class))).thenAnswer(i -> i.getArguments()[0]);

        Antropometria guardado = antropServicio.crear(a);

        assertNotNull(guardado.getImc());
        assertEquals(24.7, guardado.getImc(), 0.1); // Delta de 0.1 para flotantes
    }

    // --- CASO 4: Restricción de Histopatología para Controles ---
    @Test
    void testCrearHistopatologia_GrupoControl_LanzaError() {
        // Setup manual del servicio para asegurar inyección correcta de mocks compartidos
        HistopatologiaS histoServicio = new HistopatologiaS(histoRepo, partRepo);

        Histopatologia h = new Histopatologia();
        h.setCodPart("CT001"); // Control

        Participantecrf control = new Participantecrf();
        control.setGrupo("Control");

        when(partRepo.findById("CT001")).thenReturn(Optional.of(control));

        assertThrows(ResponseStatusException.class, () -> {
            histoServicio.crear(h);
        });
        
        // Verifica que no se guardó nada
        verify(histoRepo, never()).save(any());
    }
}