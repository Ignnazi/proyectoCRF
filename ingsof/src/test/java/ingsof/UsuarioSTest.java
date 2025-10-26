package ingsof;

import ingsof.entidad.Usuario;
import ingsof.servicio.UsuarioS;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UsuarioSTest {

    @Autowired
    private UsuarioS servicio;

    @Test
    void crearUser() {
        Usuario u = new Usuario();
        u.setNombre("Nami");
        u.setRol("Cartografa");

        Usuario guardado = servicio.guardar(u);
        assertNotNull(guardado.getIdUser());

        List<Usuario> lista = servicio.listar();
        System.out.println("Usuarios en la base de datos:");
        assertTrue(lista.size() > 0);
    }

    @Test
    void modificarUserID() { 
    int id=10; //busca por ID existente

    Usuario existente = servicio.obtenerPorId(id);
    assertNotNull(existente, "El usuario con ID " + id + " no existe");
    existente.setNombre("Chopper");
    existente.setRol("Doctor");

    Usuario actualizado = servicio.guardar(existente);
    assertEquals("Doctor", actualizado.getRol());//verifica que se actualizo el rol
    assertEquals(id, actualizado.getIdUser());//verifica que el ID sigue siendo el mismo
}


   @Test
    void eliminarUserID() {

    int id=11;//busca por ID existente y k no este asociado a otra entidad
    Usuario existente = servicio.obtenerPorId(id);
    assertNotNull(existente, "El usuario con ID " + id + " no existe");
    servicio.eliminar(existente.getIdUser());
    assertNull(servicio.obtenerPorId(id), "El usuario con ID " + id + " no fue eliminado");

}

}
