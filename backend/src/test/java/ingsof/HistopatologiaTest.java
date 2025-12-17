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

import ingsof.entidad.Histopatologia;
import ingsof.entidad.Participantecrf;

@ExtendWith(MockitoExtension.class)
public class HistopatologiaTest {

    @Mock
    private Participantecrf participanteMock;

    @InjectMocks
    private Histopatologia histopatologia;

    @Test
    void testAsignacionYLecturaDeCampos() {
        // Configurar mock
        when(participanteMock.getCodPart()).thenReturn("P001");

        // Asignar valores al objeto principal
        histopatologia.setIdHisto(10);
        histopatologia.setTipo("Carcinoma");
        histopatologia.setLocalizacion("Pulmón");
        histopatologia.setEstadio("IIA");
        histopatologia.setCodPart("P001");
        histopatologia.setParticipante(participanteMock);

        // Verificar getters
        assertEquals(10, histopatologia.getIdHisto());
        assertEquals("Carcinoma", histopatologia.getTipo());
        assertEquals("Pulmón", histopatologia.getLocalizacion());
        assertEquals("IIA", histopatologia.getEstadio());
        assertEquals("P001", histopatologia.getCodPart());
        assertEquals(participanteMock, histopatologia.getParticipante());

        // Verificar comportamiento del mock
        assertEquals("P001", histopatologia.getParticipante().getCodPart());
        verify(participanteMock, times(1)).getCodPart();
    }

    @Test
    void testConstructorVacio() {
        Histopatologia nuevaHisto = new Histopatologia();
        assertNotNull(nuevaHisto);
        assertNull(nuevaHisto.getIdHisto());
        assertNull(nuevaHisto.getTipo());
        assertNull(nuevaHisto.getLocalizacion());
        assertNull(nuevaHisto.getEstadio());
        assertNull(nuevaHisto.getCodPart());
        assertNull(nuevaHisto.getParticipante());
    }
}




/*package ingsof;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

import ingsof.entidad.Histopatologia;
import ingsof.entidad.Participantecrf;

public class HistopatologiaTest {

    @Test
    void testGettersAndSetters() {
        // Crear instancia
        Histopatologia histo = new Histopatologia();

        // Crear un participante simulado
        Participantecrf participante = new Participantecrf();
        participante.setCodPart("P001");

        // Asignar valores
        histo.setIdHisto(1);
        histo.setTipo("Carcinoma");
        histo.setLocalizacion("Pulmón");
        histo.setEstadio("IIA");
        histo.setCodPart("P001");
        histo.setParticipante(participante);

        // Verificar getters
        assertEquals(1, histo.getIdHisto());
        assertEquals("Carcinoma", histo.getTipo());
        assertEquals("Pulmón", histo.getLocalizacion());
        assertEquals("IIA", histo.getEstadio());
        assertEquals("P001", histo.getCodPart());
        assertEquals(participante, histo.getParticipante());
    }

    @Test
    void testEmptyConstructor() {
        Histopatologia histo = new Histopatologia();
        assertNotNull(histo);
        assertNull(histo.getIdHisto());
        assertNull(histo.getTipo());
        assertNull(histo.getLocalizacion());
        assertNull(histo.getEstadio());
        assertNull(histo.getCodPart());
        assertNull(histo.getParticipante());
    }
}
*/