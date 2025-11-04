package ingsof.controlador;

import ingsof.servicio.GenotipoS;
import ingsof.entidad.Genotipo;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/genotipo")
public class GenotipoC {
    private final GenotipoS servicio;
    public GenotipoC(GenotipoS servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<Genotipo> listar() {
        return this.servicio.listar();
    }

    @GetMapping("/{id}")
    public Optional<Genotipo> obtener(@PathVariable int id) {
        return this.servicio.obtenerPorId(id);
    }

    @PostMapping
    public void guardar(@RequestBody Genotipo genotipo) {
        this.servicio.guardar(genotipo);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable int id) {
        this.servicio.eliminar(id);
    }

    @PutMapping("/{id}")
    public void actualizar(@PathVariable int id, @RequestBody Genotipo genotipo) {
        this.servicio.actualizar(id, genotipo);
    }
}
