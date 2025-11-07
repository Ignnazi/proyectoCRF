package ingsof;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import ingsof.entidad.Helicobacter;
import ingsof.entidad.Participantecrf;

@ExtendWith(MockitoExtension.class)
public class HelicobacterTest {

    @Mock
    private Participantecrf participanteMock;

    @InjectMocks
    private Helicobacter helicobacter;

    @Test
    void testAsignacionYLecturaDeCampos() {
        // Simular comportamiento del participante
        when(participanteMock.getCodPart()).thenReturn("H001");

        // Asignar valores a la entidad
        helicobacter.setIdHelic(10);
        helicobacter.setPrueba("Aliento");
        helicobacter.setResultado("Positivo");
        helicobacter.setAntiguedad("<1 año");
        helicobacter.setCodPart("H001");
        helicobacter.setParticipante(participanteMock);

        // Verificar que los valores se guardan correctamente
        assertEquals(10, helicobacter.getIdHelic());
        assertEquals("Aliento", helicobacter.getPrueba());
        assertEquals("Positivo", helicobacter.getResultado());
        assertEquals("<1 año", helicobacter.getAntiguedad());
        assertEquals("H001", helicobacter.getCodPart());
        assertEquals(participanteMock, helicobacter.getParticipante());

        // Verificar que el mock funciona correctamente
        assertEquals("H001", helicobacter.getParticipante().getCodPart());
        verify(participanteMock, times(1)).getCodPart();
    }

    @Test
    void testConstructorVacio() {
        Helicobacter nuevo = new Helicobacter();
        assertNotNull(nuevo);
        assertEquals(0, nuevo.getIdHelic()); // tipo primitivo int -> valor por defecto = 0
        assertNull(nuevo.getPrueba());
        assertNull(nuevo.getResultado());
        assertNull(nuevo.getAntiguedad());
        assertNull(nuevo.getCodPart());
        assertNull(nuevo.getParticipante());
    }
}
