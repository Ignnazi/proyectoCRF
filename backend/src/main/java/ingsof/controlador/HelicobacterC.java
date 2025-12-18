package ingsof.controlador;

import ingsof.entidad.Helicobacter;
import ingsof.servicio.HelicobacterS;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3002")
@RestController
@RequestMapping("/api/helicobacter")
public class HelicobacterC {

    private final HelicobacterS servicio;

    public HelicobacterC(HelicobacterS servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public ResponseEntity<List<Helicobacter>> listar() {
        return ResponseEntity.ok(servicio.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Helicobacter> porId(@PathVariable int id) {
        return ResponseEntity.ok(servicio.porId(id));
    }

    @PostMapping
    public ResponseEntity<Helicobacter> crear(@RequestBody Helicobacter body) {
        return ResponseEntity.ok(servicio.crear(body));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Helicobacter> actualizar(@PathVariable int id, @RequestBody Helicobacter body) {
        return ResponseEntity.ok(servicio.actualizar(id, body));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable int id) {
        servicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
