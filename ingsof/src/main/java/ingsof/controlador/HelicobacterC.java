package ingsof.controlador;

import ingsof.entidad.Helicobacter;
import ingsof.servicio.HelicobacterS;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/helicobacter")
public class HelicobacterC {

    private final HelicobacterS servicio;

    public HelicobacterC(HelicobacterS servicio) {
        this.servicio = servicio;
    }

    @RequestMapping("/listar")
    public String listar() {
        return this.servicio.listar().toString();
    }

    @RequestMapping("/guardar")
    public void guardar(@RequestBody Helicobacter helicobacter) {
        this.servicio.guardar(helicobacter);
    }

    @RequestMapping("/eliminar/{id}")
    public void eliminar(@PathVariable int id) {
        this.servicio.eliminar(id);
    }

    @RequestMapping("/actualizar/{id}")
    public void actualizar(@PathVariable int id, @RequestBody Helicobacter helicobacter) {
        this.servicio.actualizar(id, helicobacter);
    }
}
