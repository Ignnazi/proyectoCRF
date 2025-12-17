package ingsof.controlador;

import ingsof.entidad.Factor;
import ingsof.servicio.FactorS;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
@CrossOrigin(origins = "http://localhost:3002")
@RestController
@RequestMapping("/api/factor")
public class FactorC {
    private final FactorS servicio;

    public FactorC(FactorS servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List <Factor> listar() {
        return this.servicio.obtenerTodos();
    }

    @GetMapping("/{id}")
    public Optional<Factor> obtener(@PathVariable int id) {
        return this.servicio.obtenerPorId(id);
    }

    @PostMapping
    public void guardar(@RequestBody Factor factor) {
        this.servicio.guardar(factor);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable int id) {
        this.servicio.eliminar(id);
    }

    @PutMapping("/{id}")
    public void actualizar(@PathVariable int id, @RequestBody Factor factor) {
        this.servicio.actualizarPorId(id, factor);
    }
}
