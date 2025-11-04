package ingsof.controlador;

import ingsof.entidad.Histopatologia;
import ingsof.servicio.HistopatologiaS;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/histopatologia")
public class HistopatologiaC {

    private final HistopatologiaS servicio;

    public HistopatologiaC(HistopatologiaS servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<Histopatologia> listar() {
        return this.servicio.listar();
    }

    @GetMapping("/{id}")
    public Optional<Histopatologia> obtener(@PathVariable int id) {
        return this.servicio.obtener(id);
    }

    @PostMapping
    public void guardar(@RequestBody Histopatologia histopatologia) {
        this.servicio.guardar(histopatologia);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable int id) {
        this.servicio.eliminar(id);
    }

    @PutMapping("/{id}")
    public void actualizar(@PathVariable int id, @RequestBody Histopatologia histopatologia) {
        this.servicio.actualizar(id, histopatologia);
    }
}
