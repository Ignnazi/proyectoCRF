package ingsof.servicio;

import ingsof.entidad.Histopatologia;
import ingsof.entidad.Participantecrf;
import ingsof.repositorio.HistopatologiaR;
import ingsof.repositorio.ParticipantecrfR;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class HistopatologiaS {

    private final HistopatologiaR repo;
    private final ParticipantecrfR partRepo;

    public HistopatologiaS(HistopatologiaR repo, ParticipantecrfR partRepo) {
        this.repo = repo;
        this.partRepo = partRepo;
    }

    @Transactional(readOnly = true)
    public List<Histopatologia> listar() {
        return repo.findAll();
    }

    @SuppressWarnings("null")
    @Transactional(readOnly = true)
    public Histopatologia porId(Integer id) {
        return repo.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe histopatologia"));
    }

    @Transactional(readOnly = true)
    public Histopatologia porCodPart(String codPart) {
        return repo.findByCodPart(codPart).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe para ese participante"));
    }

    @Transactional
    public Histopatologia crear(Histopatologia h) {
        @SuppressWarnings("null")
        Participantecrf p = partRepo.findById(h.getCodPart())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Participante no encontrado"));
        if (!"Caso".equalsIgnoreCase(p.getGrupo()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Solo se permiten registros de casos");
        if (repo.existsByCodPart(h.getCodPart()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe histopatologia para este participante");
        return repo.save(h);
    }

    @SuppressWarnings("null")
    @Transactional
    public Histopatologia actualizar(Integer id, Histopatologia cambios) {
        Histopatologia db = porId(id);
        String destino = cambios.getCodPart() != null ? cambios.getCodPart() : db.getCodPart();
        @SuppressWarnings("null")
        Participantecrf p = partRepo.findById(destino)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Participante no encontrado"));
        if (!"Caso".equalsIgnoreCase(p.getGrupo()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Solo se permiten registros de casos");
        if (cambios.getCodPart() != null && !cambios.getCodPart().equals(db.getCodPart())
                && repo.existsByCodPart(cambios.getCodPart()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe histopatologia para ese participante");
        if (cambios.getTipo() != null) db.setTipo(cambios.getTipo());
        if (cambios.getLocalizacion() != null) db.setLocalizacion(cambios.getLocalizacion());
        if (cambios.getEstadio() != null) db.setEstadio(cambios.getEstadio());
        if (cambios.getCodPart() != null) db.setCodPart(cambios.getCodPart());
        return repo.save(db);
    }

    @Transactional
    public void borrarControl(String codPart) { //Elimina registro automaticamente si en participante se actualiza como Control
        @SuppressWarnings("null")
        Participantecrf p = partRepo.findById(codPart)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Participante no encontrado"));
        if ("Control".equalsIgnoreCase(p.getGrupo()) && repo.existsByCodPart(codPart)) {
            repo.deleteByCodPart(codPart);
        }
    }
}
