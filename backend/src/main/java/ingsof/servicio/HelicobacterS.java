package ingsof.servicio;

import ingsof.entidad.Helicobacter;
import ingsof.repositorio.HelicobacterR;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HelicobacterS {

    private final HelicobacterR repo;

    public HelicobacterS(HelicobacterR repo) {
        this.repo = repo;
    }

    public void guardar(Helicobacter helicobacter) {
        repo.save(helicobacter);
    }

    public void eliminar(int id) {
        repo.deleteById(id);
    }

    public void actualizar(int id, Helicobacter helicobacterActualizado) {
        Optional<Helicobacter> helicobacterExistente = repo.findById(id);
        if (helicobacterExistente.isPresent()) {
            helicobacterExistente.get().setAntiguedad(helicobacterActualizado.getAntiguedad());
            helicobacterExistente.get().setResultado(helicobacterActualizado.getResultado());
            helicobacterExistente.get().setPrueba(helicobacterActualizado.getPrueba());
            repo.save(helicobacterExistente.get());
        }

    }

    public Optional<Helicobacter> obtener(int id) {
        return repo.findById(id);
    }

    public List<Helicobacter> listar() {
        return repo.findAll();
    }
}
