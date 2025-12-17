package ingsof.controlador;

import ingsof.entidad.Participantecrf;
import ingsof.servicio.ParticipantecrfS;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = "http://localhost:3002")
@RestController
@RequestMapping("/api/participantecrf")
public class ParticipantecrfC {

    private final ParticipantecrfS servicio;

    public ParticipantecrfC(ParticipantecrfS servicio) {
        this.servicio = servicio;
    }

    // GET: lista todos
    @GetMapping
    public ResponseEntity<List<Participantecrf>> listar() {
        return ResponseEntity.ok(servicio.listar());
    }

    // GET: obtiene uno por cod_part
    @SuppressWarnings("null")
    @GetMapping("/{codPart}")
    public ResponseEntity<Participantecrf> porCodigo(@PathVariable String codPart) {
        return ResponseEntity.of(servicio.buscar(codPart));
    }

    // POST: crea (genera cod_part y fecha si faltan)
    @PostMapping
    public ResponseEntity<Participantecrf> crear(@RequestBody Participantecrf body) {
        return ResponseEntity.ok(servicio.crear(body));
    }

    // PUT: actualiza. Si cambia grupo, cambia también el cod_part.
    @PutMapping("/{codPart}")
    public ResponseEntity<Participantecrf> actualizar(@PathVariable String codPart,
                                                      @RequestBody Participantecrf body) {
        return ResponseEntity.ok(servicio.actualizar(codPart, body));
    }

    // DELETE: elimina padre e hijas por cascada
    @DeleteMapping("/{codPart}")
    public ResponseEntity<Void> eliminar(@PathVariable String codPart) {
        servicio.eliminar(codPart);
        return ResponseEntity.noContent().build();
    }
}

