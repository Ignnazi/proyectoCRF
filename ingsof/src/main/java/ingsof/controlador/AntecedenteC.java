package ingsof.controlador;

import ingsof.entidad.Antecedente;
import ingsof.servicio.AntecedenteS;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/antecedente")
public class AntecedenteC {
    private final AntecedenteS servicio;
    public AntecedenteC(AntecedenteS servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<Antecedente> listar() {
        return this.servicio.listar();
    }

    @GetMapping("/{id}")
    public Optional<Antecedente> obtener(@PathVariable int id) {
        return this.servicio.obtenerPorId(id);
    }

    @PostMapping
    public void guardar(@RequestBody Antecedente antecedente) {
        this.servicio.guardar(antecedente);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable int id) {
        this.servicio.eliminar(id);
    }

    @PutMapping("/{id}")
    public void actualizar(@PathVariable int id, @RequestBody Antecedente antecedente) {
        this.servicio.actualizar(id, antecedente);
    }


}
