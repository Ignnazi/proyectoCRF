package ingsof.controlador;

import java.util.List;
import java.util.Optional;

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

import ingsof.entidad.Sociodemo;
import ingsof.servicio.SociodemoS;
@CrossOrigin(origins = "*")
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
