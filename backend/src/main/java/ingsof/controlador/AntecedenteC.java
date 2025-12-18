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

import ingsof.entidad.Antecedente;
import ingsof.servicio.AntecedenteS;
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