package ingsof.servicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

import ingsof.entidad.Participantecrf;
import ingsof.repositorio.ParticipantecrfR;

@Service
public class ParticipantecrfS {

    private final ParticipantecrfR repo;

    public ParticipantecrfS(ParticipantecrfR repo) {
        this.repo = repo;
    }

    // Listar todos los participantes
    public List<Participantecrf> listar() {
        return repo.findAll();
    }

    // Buscar un participante por código
    public Optional<Participantecrf> obtenerPorCodigo(String codPart) {
        return repo.findById(codPart);
    }

    // Crear o actualizar participante
    public Participantecrf guardar(Participantecrf participantecrf) {
        return repo.save(participantecrf);
    }

    // Eliminar participante por código
    public void eliminar(String codPart) {
        repo.deleteById(codPart);
    }

    public void actualizarPorId(String codPart, Participantecrf participante) {
        Optional<Participantecrf> participanteExistente = repo.findById(codPart);
        if (participanteExistente.isPresent()) {
            participanteExistente.get().setNombre(participante.getNombre());
            participanteExistente.get().setGrupo(participante.getGrupo());
            repo.save(participanteExistente.get());
        }
    }
}
