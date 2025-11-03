package ingsof.servicio;

import ingsof.entidad.Histopatologia;
import ingsof.repositorio.HistopatologiaR;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HistopatologiaS {
    private final HistopatologiaR repo;

    public HistopatologiaS(HistopatologiaR repo) {
        this.repo = repo;
    }

    public void guardar(Histopatologia histopatologia) {
        repo.save(histopatologia);
    }
    public void eliminar(int id) {
        repo.deleteById(id);
    }
    public void actualizar(int id, Histopatologia histopatologiaActualizado) {
        Optional<Histopatologia> histopatologiaExistente = repo.findById(id);
        if (histopatologiaExistente.isPresent()) {
            histopatologiaExistente.get().setTipo(histopatologiaActualizado.getTipo());
            histopatologiaExistente.get().setEstadio(histopatologiaActualizado.getEstadio());
            histopatologiaExistente.get().setCodPart(histopatologiaActualizado.getCodPart());
            repo.save(histopatologiaExistente.get());
        }
    }
    public Optional<Histopatologia> obtener(int id) {
        return repo.findById(id);
    }
    public List<Histopatologia> listar() {
        return repo.findAll();
    }


}
