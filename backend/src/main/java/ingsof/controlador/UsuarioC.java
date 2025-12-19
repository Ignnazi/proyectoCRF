package ingsof.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import ingsof.entidad.Usuario;
import ingsof.servicio.UsuarioS;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/usuario")
public class UsuarioC {

    @Autowired
    private UsuarioS servicio;

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

    // Endpoint de login (comparación rudimentaria por id y contraseña en texto plano)
    public static class Credenciales {
        public Integer id;
        public String password;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Credenciales cred) {
        if (cred == null || cred.id == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario o contraseña inválidos");
        }

        Usuario u = servicio.obtenerPorId(cred.id);
        if (u == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario o contraseña inválidos");
        }

        String stored = u.getPassword();
        if (stored == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario o contraseña inválidos");
        }

        boolean ok = stored.equals(cred.password);

        if (!ok) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario o contraseña inválidos");
        }

        // Autenticación exitosa — devolver usuario (sin password)
        u.setPassword(null);
        return ResponseEntity.ok(u);
    }
}
