package ingsof.controlador;

import ingsof.entidad.Antropometria;
import ingsof.servicio.AntropometriaS;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/antropometria")
public class AntropometriaC {
    private final AntropometriaS servicio;
    public AntropometriaC(AntropometriaS servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<Antropometria> listar() {
        return this.servicio.listar();
    }

    @GetMapping("/{id}")
    public Optional<Antropometria> obtener(@PathVariable int id) {
        return this.servicio.obtenerPorId(id);
    }

    @PostMapping
    public void guardar(@RequestBody Antropometria antropometria) {
        this.servicio.guardar(antropometria);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable int id) {
        this.servicio.eliminar(id);
    }

    @PutMapping("/{id}")
    public void actualizar(@PathVariable int id, @RequestBody Antropometria antropometria) {
        this.servicio.actualizar(id, antropometria);
    }
}
