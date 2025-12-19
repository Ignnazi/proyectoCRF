package ingsof.controlador;

import ingsof.entidad.Helicobacter;
import ingsof.servicio.HelicobacterS;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://pacheco.chillan.ubiobio.cl:8026")
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
    public ResponseEntity<Helicobacter> porId(@PathVariable Integer id) {
        return ResponseEntity.ok(servicio.porId(id));
    }

    @GetMapping("/por-participante/{codPart}")
    public ResponseEntity<Helicobacter> porCodPart(@PathVariable String codPart) {
        Helicobacter h = servicio.porCodPart(codPart);
        if (h == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(h);
    }

    @PostMapping
    public ResponseEntity<Helicobacter> crear(@RequestBody Helicobacter body) {
        return ResponseEntity.ok(servicio.crear(body));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Helicobacter> actualizar(@PathVariable Integer id, @RequestBody Helicobacter body) {
        return ResponseEntity.ok(servicio.actualizar(id, body));
    }
}
