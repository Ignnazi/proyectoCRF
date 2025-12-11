package ingsof.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import ingsof.entidad.Usuario;
import ingsof.servicio.UsuarioS;

@RestController
@RequestMapping("/api/usuario")
@CrossOrigin(origins = "*") // Permite llamadas desde Postman o un frontend
public class UsuarioC {

    @Autowired
    private UsuarioS servicio;

    @GetMapping
    public List<Usuario> listar() {
        return servicio.listar();
    }

    @GetMapping("/{id}")
    public Usuario obtener(@PathVariable int id) {
        return servicio.obtenerPorId(id);
    }

    @PostMapping
    public Usuario crear(@RequestBody Usuario usuario) {
        return servicio.guardar(usuario);
    }


    @PutMapping("/{id}")
    public Usuario actualizar(@PathVariable int id, @RequestBody Usuario usuario) {
        usuario.setIdUser(id);
        return servicio.guardar(usuario);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable int id) {
        servicio.eliminar(id);
    }
}

