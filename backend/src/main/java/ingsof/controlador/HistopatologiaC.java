package ingsof.controlador;

import ingsof.entidad.Histopatologia;
import ingsof.servicio.HistopatologiaS;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = "http://pacheco.chillan.ubiobio.cl:8026")
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
    public ResponseEntity<Histopatologia> crear(@RequestBody Histopatologia body) {
        return ResponseEntity.ok(servicio.crear(body));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Histopatologia> actualizar(@PathVariable Integer id, @RequestBody Histopatologia body) {
        return ResponseEntity.ok(servicio.actualizar(id, body));
    }

    @PutMapping("/sync/{codPart}")
    public ResponseEntity<Void> borrarControl(@PathVariable String codPart) {
        servicio.borrarControl(codPart);
        return ResponseEntity.noContent().build();
    }
}
