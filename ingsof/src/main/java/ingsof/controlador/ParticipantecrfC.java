package ingsof.controlador;

import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

import ingsof.entidad.Participantecrf;
import ingsof.servicio.ParticipantecrfS;

@RestController
@RequestMapping("/api/participante")
@CrossOrigin(origins = "*")
public class ParticipantecrfC {

    private final ParticipantecrfS servicio;

    public ParticipantecrfC(ParticipantecrfS servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<Participantecrf> listar() {
        return this.servicio.listar();
    }

    @GetMapping("/{codPart}")
    public Optional<Participantecrf> obtener(@PathVariable String codPart) {
        return this.servicio.obtenerPorCodigo(codPart);
    }

    @PostMapping
    public Participantecrf guardar(@RequestBody Participantecrf participante) {
        return this.servicio.guardar(participante);
    }

    @PutMapping("/{codPart}")
    public void actualizarPorId(@PathVariable String codPart, @RequestBody Participantecrf participante) {
        this.servicio.actualizarPorId(codPart, participante);
    }

    @DeleteMapping("/{codPart}")
    public void eliminar(@PathVariable  String codPart) {
        this.servicio.eliminar(codPart);
    }
}
