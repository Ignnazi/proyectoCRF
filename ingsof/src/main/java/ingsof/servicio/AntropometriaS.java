package ingsof.servicio;

import ingsof.entidad.Antropometria;
import ingsof.repositorio.AntropometriaR;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AntropometriaS {
    private final AntropometriaR repo;
    public AntropometriaS(AntropometriaR repo) {
        this.repo = repo;
    }

    public List<Antropometria> listar() {
        return repo.findAll();
    }

    public Optional<Antropometria> obtenerPorId(int id) {
        return repo.findById(id);
    }

    public Antropometria guardar(Antropometria antropometria) {
        return repo.save(antropometria);
    }

    public void eliminar(int id) {
        repo.deleteById(id);
    }

    public void actualizarPorId(int id, Antropometria antropometriaActualizado) {
        Optional<Antropometria> antropometriaExistente = repo.findById(id);
        if (antropometriaExistente.isPresent()) {
            antropometriaExistente.get().setEstatura(antropometriaActualizado.getEstatura());
            antropometriaExistente.get().setPeso(antropometriaActualizado.getPeso());
            antropometriaExistente.get().setImc(antropometriaActualizado.getImc());
            repo.save(antropometriaExistente.get());
        }

    }
}
