package ingsof;

import ingsof.entidad.Usuario;
import ingsof.repositorio.UsuarioR;
import ingsof.servicio.UsuarioS;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioST {

    @Mock
    UsuarioR repo;

    // Inyecta en el campo @Autowired del servicio
    @InjectMocks
    UsuarioS servicio;

    @Test
    void listar_ok() {
        when(repo.findAll()).thenReturn(List.of(new Usuario(), new Usuario()));
        var lista = servicio.listar();
        assertEquals(2, lista.size());
        verify(repo).findAll();
    }

    @Test
    void obtenerPorId_encontrado() {
        Usuario u = new Usuario();
        u.setNombre("Operador QA");
        u.setRol("Digitador");
        when(repo.findById(1)).thenReturn(Optional.of(u));

        Usuario out = servicio.obtenerPorId(1);

        assertNotNull(out);
        assertEquals("Operador QA", out.getNombre());
        assertEquals("Digitador", out.getRol());
        verify(repo).findById(1);
    }

    @Test
    void obtenerPorId_noEncontrado() {
        when(repo.findById(99)).thenReturn(Optional.empty());
        assertNull(servicio.obtenerPorId(99));
        verify(repo).findById(99);
    }

    @Test
    void guardar_creaOActualiza() {
        Usuario u = new Usuario();
        u.setNombre("Nuevo");
        u.setRol("Supervisor");

        when(repo.save(u)).thenReturn(u);

        Usuario out = servicio.guardar(u);

        assertNotNull(out);
        assertEquals("Nuevo", out.getNombre());
        assertEquals("Supervisor", out.getRol());
        verify(repo).save(u);
    }

    @Test
    void eliminar_ok() {
        servicio.eliminar(5);
        verify(repo).deleteById(5);
    }
}
