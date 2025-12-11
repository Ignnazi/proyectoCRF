package ingsof.controlador;

import ingsof.entidad.Sociodemo;
import ingsof.servicio.SociodemoS;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/sociodemo")
public class SociodemoC {

    private final SociodemoS servicio;

    public SociodemoC(SociodemoS servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<Sociodemo> listar() {
        return this.servicio.listar();
    }

    @GetMapping("/{id}")
    public Optional<Sociodemo> obtener(@PathVariable int id) {
        return this.servicio.obtener(id);
    }

    @PostMapping
    public void guardar(@RequestBody Sociodemo sociodemo) {
        this.servicio.guardar(sociodemo);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable int id) {
        this.servicio.eliminar(id);
    }

    @PutMapping("/{id}")
    public void actualizar(@PathVariable int id, @RequestBody Sociodemo sociodemo) {
        this.servicio.actualizar(id, sociodemo);
    }
}
