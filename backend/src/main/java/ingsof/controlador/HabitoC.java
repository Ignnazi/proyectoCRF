package ingsof.controlador;

import ingsof.entidad.Habito;
import ingsof.servicio.HabitoS;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
@CrossOrigin(origins = "http://localhost:3002")
@RestController
@RequestMapping("/api/habito")
public class HabitoC {

    private final HabitoS servicio;

    public HabitoC(HabitoS servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<Habito> listar() {
        return this.servicio.listar();
    }

    @GetMapping("/{id}")
    public Optional<Habito> obtener(@PathVariable int id) {
        return this.servicio.obtenerPorId(id);
    }

    @PostMapping
    public ResponseEntity<String> guardar(@RequestBody Habito habito) {
        this.servicio.guardar(habito);
        return ResponseEntity.ok("Hábito guardado correctamente");
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable int id) {
        this.servicio.eliminar(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> actualizar(@PathVariable int id, @RequestBody Habito habitoActualizado) {
        this.servicio.actualizar(id, habitoActualizado);
        return ResponseEntity.ok("Hábito actualizado correctamente");
    }
}
