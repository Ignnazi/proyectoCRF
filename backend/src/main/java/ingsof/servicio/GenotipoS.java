package ingsof.servicio;

import ingsof.entidad.Genotipo;
import ingsof.repositorio.GenotipoR;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GenotipoS {
    private final GenotipoR repo;
    public GenotipoS(GenotipoR repo) {
        this.repo = repo;
    }

    @SuppressWarnings("null")
    public void guardar(Genotipo genotipo) {
        repo.save(genotipo);
    }

    public void eliminar(int id) {
        repo.deleteById(id);
    }
    @SuppressWarnings("null")
    public void actualizar(int id, Genotipo genotipoActualizado) {
        Optional<Genotipo> genotipoExistente = repo.findById(id);
        if (genotipoExistente.isPresent()) {
            genotipoExistente.get().setFechaToma(genotipoActualizado.getFechaToma());
            genotipoExistente.get().setCodPart(genotipoActualizado.getCodPart());
            genotipoExistente.get().setTlr9Rs187084( genotipoActualizado.getTlr9Rs187084());
            genotipoExistente.get().setTlr9Rs5743836(genotipoActualizado.getTlr9Rs5743836());
            genotipoExistente.get().setMir146aRs2910164(genotipoActualizado.getMir146aRs2910164());
            genotipoExistente.get().setMir196a2Rs11614913(genotipoActualizado.getMir196a2Rs11614913());
            genotipoExistente.get().setMthfrRs1801133(genotipoActualizado.getMthfrRs1801133());
            genotipoExistente.get().setDnmt3bRs1569686(genotipoActualizado.getDnmt3bRs1569686());
            repo.save(genotipoExistente.get());
        }
    }

    public Optional<Genotipo> obtenerPorId(int id) {
        return repo.findById(id);
    }

    public List<Genotipo> listar() {
        return repo.findAll();
    }
}
