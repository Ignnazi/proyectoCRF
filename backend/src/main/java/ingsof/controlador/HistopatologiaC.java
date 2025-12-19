package ingsof.controlador;

import ingsof.entidad.Histopatologia;
import ingsof.servicio.HistopatologiaS;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = "http://localhost:3002")
@RestController
@RequestMapping("/api/histopatologia")
public class HistopatologiaC {

    private final HistopatologiaS servicio;

    public HistopatologiaC(HistopatologiaS servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public ResponseEntity<List<Histopatologia>> listar() {
        return ResponseEntity.ok(servicio.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Histopatologia> porId(@PathVariable Integer id) {
        return ResponseEntity.ok(servicio.porId(id));
    }

    @GetMapping("/por-participante/{codPart}")
    public ResponseEntity<Histopatologia> porCodPart(@PathVariable String codPart) {
        return ResponseEntity.ok(servicio.porCodPart(codPart));
    }

    @PostMapping
    public ResponseEntity<String> crear(@RequestBody Histopatologia body) {
        servicio.crear(body);
        return ResponseEntity.ok("Datos histopatológicos guardados correctamente");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> actualizar(@PathVariable Integer id, @RequestBody Histopatologia body) {
        servicio.actualizar(id, body);
        return ResponseEntity.ok("Datos histopatológicos actualizados correctamente");
    }

    @PutMapping("/sync/{codPart}")
    public ResponseEntity<Void> borrarControl(@PathVariable String codPart) {
        servicio.borrarControl(codPart);
        return ResponseEntity.noContent().build();
    }
}
