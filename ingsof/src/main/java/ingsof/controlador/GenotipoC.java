package ingsof.controlador;

import ingsof.servicio.GenotipoS;
import ingsof.entidad.Genotipo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/genotipo")
public class GenotipoC {
    private final GenotipoS servicio;
    public GenotipoC(GenotipoS servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<Genotipo> listar() {
        return this.servicio.listar();
    }

    @GetMapping("/{id}")
    public Optional<Genotipo> obtener(@PathVariable int id) {
        return this.servicio.obtenerPorId(id);
    }

    @PostMapping
    public ResponseEntity<?> guardar(@RequestBody Genotipo genotipo) {
        try {
            this.servicio.guardar(genotipo);
            return ResponseEntity.ok("Guardado Exitosamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable int id) {
        this.servicio.eliminar(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable int id, @RequestBody Genotipo genotipo) {
        try {
            Genotipo genotipoActualizado = this.servicio.actualizar(id, genotipo);
            return ResponseEntity.ok(genotipoActualizado);
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
