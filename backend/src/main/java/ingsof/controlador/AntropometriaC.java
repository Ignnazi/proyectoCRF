package ingsof.controlador;

import ingsof.entidad.Antropometria;
import ingsof.servicio.AntropometriaS;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = "http://localhost:3002")
@RestController
@RequestMapping("/api/antropometria")
public class AntropometriaC {

    private final AntropometriaS servicio;

    public AntropometriaC(AntropometriaS servicio) { this.servicio = servicio; }

    @GetMapping
    public ResponseEntity<List<Antropometria>> listar(){
        return ResponseEntity.ok(servicio.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Antropometria> porId(@PathVariable Integer id){
        return ResponseEntity.ok(servicio.porId(id));
    }

    @GetMapping("/por-participante/{codPart}")
    public ResponseEntity<List<Antropometria>> porParticipante(@PathVariable String codPart){ return ResponseEntity.ok(servicio.porParticipante(codPart)); }

    @PostMapping
    public ResponseEntity<Antropometria> crear(@RequestBody Antropometria body){ return ResponseEntity.ok(servicio.crear(body)); }

    @PutMapping("/{id}")
    public ResponseEntity<Antropometria> actualizar(@PathVariable Integer id, @RequestBody Antropometria body){ return ResponseEntity.ok(servicio.actualizar(id, body)); }
}
