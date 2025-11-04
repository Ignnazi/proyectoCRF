package ingsof.servicio;

import ingsof.entidad.Factor;
import ingsof.repositorio.FactoresR;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FactorS {
    private final FactoresR repo;

    public FactorS(FactoresR repo) {
        this.repo = repo;
    }

    public void guardar(Factor factor) {
        repo.save(factor);
    }

    public void eliminar(int id) {
        repo.deleteById(id);
    }

    public Optional<Factor> obtenerPorId(int id) {
        return repo.findById(id);
    }

    public List<Factor> obtenerTodos() {
        return repo.findAll();
    }

    public void actualizarPorId(int id, Factor factorActualizado) {
        Optional<Factor> factorExistente = repo.findById(id);
        if (factorExistente.isPresent()) {
            factorExistente.get().setCarnes(factorActualizado.getCarnes());
            factorExistente.get().setFrutas(factorActualizado.getFrutas());
            factorExistente.get().setSalados(factorActualizado.getSalados());
            factorExistente.get().setFrituras(factorActualizado.getFrituras());
            factorExistente.get().setQuimicos(factorActualizado.getQuimicos());
            factorExistente.get().setHumoLena(factorActualizado.getHumoLena());
            factorExistente.get().setPesticidas(factorActualizado.getPesticidas());
            factorExistente.get().setFuenteAgua(factorActualizado.getFuenteAgua());
            factorExistente.get().setTratamientoAgua(factorActualizado.getTratamientoAgua());
            factorExistente.get().setDetalleQuimicos(factorActualizado.getDetalleQuimicos());
            factorExistente.get().setHumoLena(factorActualizado.getHumoLena());
            repo.save(factorExistente.get());
        }
    }
}
