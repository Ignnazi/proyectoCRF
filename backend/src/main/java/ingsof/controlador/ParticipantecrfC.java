package ingsof.controlador;

import ingsof.entidad.Participantecrf;
import ingsof.servicio.ParticipantecrfS;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://pacheco.chillan.ubiobio.cl:8026")
@RestController
@RequestMapping("/api/participantecrf")
public class ParticipantecrfC {

    private final ParticipantecrfS servicio;

    public ParticipantecrfC(ParticipantecrfS servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public ResponseEntity<List<Participantecrf>> listar() {
        return ResponseEntity.ok(servicio.listar());
    }

    @SuppressWarnings("null")
    @GetMapping("/{codPart}")
    public ResponseEntity<Participantecrf> porCodigo(@PathVariable String codPart) {
        return ResponseEntity.of(servicio.buscar(codPart));
    }

    @PostMapping
    public ResponseEntity<Participantecrf> crear(@RequestBody Participantecrf body) {
        return ResponseEntity.ok(servicio.crear(body));
    }

    @PutMapping("/{codPart}")
    public ResponseEntity<Participantecrf> actualizar(@PathVariable String codPart,
                                                      @RequestBody Participantecrf body) {
        return ResponseEntity.ok(servicio.actualizar(codPart, body));
    }

    @DeleteMapping("/{codPart}")
    public ResponseEntity<Void> eliminar(@PathVariable String codPart) {
        servicio.eliminar(codPart);
        return ResponseEntity.noContent().build();
    }
}
