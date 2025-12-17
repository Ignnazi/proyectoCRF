package ingsof.controlador;

import ingsof.entidad.Antecedente;
import ingsof.servicio.AntecedenteS;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = "http://localhost:3002")
@RestController
@RequestMapping("/api/antecedente")
public class AntecedenteC {

    private final AntecedenteS servicio;

    public AntecedenteC(AntecedenteS servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public ResponseEntity<List<Antecedente>> listar() {
        return ResponseEntity.ok(servicio.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Antecedente> porId(@PathVariable Integer id) {
        return ResponseEntity.ok(servicio.porId(id));
    }

    @GetMapping("/por-participante/{codPart}")
    public ResponseEntity<Antecedente> porCodPart(@PathVariable String codPart) {
        return ResponseEntity.ok(servicio.porCodPart(codPart));
    }

    @PostMapping
    public ResponseEntity<Antecedente> crear(@RequestBody Antecedente body) {
        return ResponseEntity.ok(servicio.crear(body));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Antecedente> actualizar(@PathVariable Integer id, @RequestBody Antecedente body) {
        return ResponseEntity.ok(servicio.actualizar(id, body));
    }
}
