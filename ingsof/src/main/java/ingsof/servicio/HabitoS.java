package ingsof.servicio;

import ingsof.entidad.Habito;
import ingsof.repositorio.HabitoR;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HabitoS {
    private final HabitoR repo;
    public HabitoS(HabitoR repo) {
        this.repo = repo;
    }

    public void guardar(Habito habito) {
        repo.save(habito);
    }

    public void eliminar(int id) {
        repo.deleteById(id);
    }

    public void actualizar(int id, Habito habitoActualizado) {
        Optional<Habito> habitoExistente = repo.findById(id);
        if (habitoExistente.isPresent()) {
            habitoExistente.get().setTipo(habitoActualizado.getTipo());
            habitoExistente.get().setEstado(habitoActualizado.getEstado());
            habitoExistente.get().setAniosConsumo(habitoActualizado.getAniosConsumo());
            habitoExistente.get().setCantidad(habitoActualizado.getCantidad());
            habitoExistente.get().setTiempoDejado(habitoActualizado.getTiempoDejado());
            habitoExistente.get().setFrecuencia(habitoActualizado.getFrecuencia());
            habitoExistente.get().setEdadInicio(habitoActualizado.getEdadInicio());
            repo.save(habitoExistente.get());
        }
    }

    public Optional<Habito> obtenerPorId(int id) {
        return repo.findById(id);
    }

    public List<Habito> listar() {
        return repo.findAll();
    }
}
