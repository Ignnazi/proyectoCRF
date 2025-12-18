package ingsof.controlador;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ingsof.entidad.Histopatologia;
import ingsof.servicio.HistopatologiaS;
@CrossOrigin(origins = "*")
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
