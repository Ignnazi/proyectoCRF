package ingsof.controlador;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ingsof.entidad.Participantecrf;
import ingsof.servicio.ParticipantecrfS;
@CrossOrigin(origins = "*")
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

