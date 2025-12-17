package ingsof.controlador;

import ingsof.entidad.Sociodemo;
import ingsof.servicio.SociodemoS;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
@CrossOrigin(origins = "http://localhost:3002")
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
    public ResponseEntity<?> guardar(@RequestBody Sociodemo sociodemo) {
        try{
            this.servicio.guardar(sociodemo);
            return ResponseEntity.ok("Guardado Exitosamente");
        } catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

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
