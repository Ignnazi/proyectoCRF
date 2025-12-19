package ingsof;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import ingsof.entidad.Usuario;
import ingsof.repositorio.UsuarioR;
import ingsof.servicio.UsuarioS;

@ExtendWith(MockitoExtension.class)
class UsuarioST {

    @Mock private UsuarioR repo;
    @InjectMocks private UsuarioS servicio;

    @Test
    void buscarPorNombre_DeberiaRetornarUsuario() {
        Usuario u = new Usuario();
        u.setNombre("admin");

        when(repo.findByNombre("admin")).thenReturn(Optional.of(u));

        Optional<Usuario> encontrado = servicio.buscarPorNombre("admin");
        
        assertTrue(encontrado.isPresent());
        assertEquals("admin", encontrado.get().getNombre());
    }

    @Test
    void eliminar_DeberiaLlamarRepo() {
        servicio.eliminar(5);
        verify(repo).deleteById(5);
    }
}