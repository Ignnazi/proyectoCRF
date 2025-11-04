package ingsof.servicio;

import ingsof.entidad.Sociodemo;
import ingsof.repositorio.SociodemoR;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SociodemoS {
    private final SociodemoR repo;

    public SociodemoS(SociodemoR repo) {
        this.repo = repo;
    }

    public void guardar(Sociodemo sociodemo) {
        repo.save(sociodemo);
    }

    public void eliminar(int id) {
        repo.deleteById(id);
    }

    public void actualizar(int id, Sociodemo sociodemoActualizado) {
        Optional<Sociodemo> sociodemoExistente = repo.findById(id);
        if (sociodemoExistente.isPresent()) {
            sociodemoExistente.get().setEdad(sociodemoActualizado.getEdad());
            sociodemoExistente.get().setSexo(sociodemoActualizado.getSexo());
            sociodemoExistente.get().setZona(sociodemoActualizado.getZona());
            sociodemoExistente.get().setNacionalidad(sociodemoActualizado.getNacionalidad());
            sociodemoExistente.get().setAniosRes(sociodemoActualizado.getAniosRes());
            sociodemoExistente.get().setDireccion(sociodemoActualizado.getDireccion());
            sociodemoExistente.get().setEducacion(sociodemoActualizado.getEducacion());
            sociodemoExistente.get().setOcupacion(sociodemoActualizado.getOcupacion());
            repo.save(sociodemoExistente.get());
        }
    }

    public Optional<Sociodemo> obtener(int id) {
        return repo.findById(id);
    }

    public List<Sociodemo> listar() {
        return repo.findAll();
    }
}
