package ingsof.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import ingsof.entidad.Usuario;
import ingsof.servicio.UsuarioS;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

@CrossOrigin(origins = "http://pacheco.chillan.ubiobio.cl:8026")
@RestController
@RequestMapping("/api/usuario")
public class UsuarioC {

    @Autowired
    private final UsuarioS servicio;

    public UsuarioC(UsuarioS servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<Usuario> listar() {
        return servicio.listar();
    }

    @GetMapping("/{id}")
    public Usuario obtener(@PathVariable int id) {
        Usuario u = servicio.obtenerPorId(id);
        return u;
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Usuario usuario) {
        try {
            Usuario u = servicio.guardar(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(u);
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            // Constraint violation (por ejemplo columna inexistente/unique/not null)
            String msg = "Constraint violation: " + (ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(msg);
        } catch (Exception ex) {
            String msg = "Error creating user: " + ex.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(msg);
        }
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

