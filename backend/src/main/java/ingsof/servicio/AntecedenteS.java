package ingsof.servicio;

import ingsof.entidad.Antecedente;
import ingsof.entidad.Participantecrf;
import ingsof.repositorio.AntecedenteR;
import ingsof.repositorio.ParticipantecrfR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AntecedenteS {

    private final AntecedenteR repo;
    private final ParticipantecrfR partRepo;

    @Autowired
    public AntecedenteS(AntecedenteR repo, ParticipantecrfR partRepo) {
        this.repo = repo;
        this.partRepo = partRepo;
    }

    @Transactional(readOnly = true)
    public List<Antecedente> listar() {
        return repo.findAll();
    }

    @SuppressWarnings("null")
    @Transactional(readOnly = true)
    public Antecedente porId(Integer id) {
        return repo.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe antecedente"));
    }

    @Transactional(readOnly = true)
    public Antecedente porCodPart(String codPart) {
        return repo.findByCodPart(codPart).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe para ese participante"));
    }

    @Transactional
    public Antecedente crear(Antecedente a) {
        @SuppressWarnings("null")
        Participantecrf p = partRepo.findById(a.getCodPart())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Participante no encontrado"));
        if (!"Caso".equalsIgnoreCase(p.getGrupo()) && "Sí".equalsIgnoreCase(a.getDiagnostico()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Solo los casos pueden tener diagnóstico histológico.");
        if (!"Caso".equalsIgnoreCase(p.getGrupo()) && a.getFechaDiag() != null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Solo los casos pueden tener fecha de diagnóstico.");
        return repo.save(a);
    }

    @SuppressWarnings("null")
    @Transactional
    public Antecedente actualizar(Integer id, Antecedente cambios) {
        Antecedente db = porId(id);
        String destino = cambios.getCodPart() != null ? cambios.getCodPart() : db.getCodPart();
        Participantecrf p = partRepo.findById(destino)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Participante no encontrado"));
        String diag = cambios.getDiagnostico() != null ? cambios.getDiagnostico() : db.getDiagnostico();
        var fecha = cambios.getFechaDiag() != null ? cambios.getFechaDiag() : db.getFechaDiag();
        if (!"Caso".equalsIgnoreCase(p.getGrupo()) && "Sí".equalsIgnoreCase(diag))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Solo los casos pueden tener diagnóstico histológico.");
        if (!"Caso".equalsIgnoreCase(p.getGrupo()) && fecha != null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Solo los casos pueden tener fecha de diagnóstico.");

        if (cambios.getDiagnostico() != null) db.setDiagnostico(cambios.getDiagnostico());
        if (cambios.getFechaDiag() != null) db.setFechaDiag(cambios.getFechaDiag());
        if (cambios.getFamCg() != null) db.setFamCg(cambios.getFamCg());
        if (cambios.getFamOtro() != null) db.setFamOtro(cambios.getFamOtro());
        if (cambios.getOtroCancer() != null) db.setOtroCancer(cambios.getOtroCancer());
        if (cambios.getOtrasEnfermedades() != null) db.setOtrasEnfermedades(cambios.getOtrasEnfermedades());
        if (cambios.getMedicamentos() != null) db.setMedicamentos(cambios.getMedicamentos());
        if (cambios.getCirugia() != null) db.setCirugia(cambios.getCirugia());
        if (cambios.getCodPart() != null) db.setCodPart(cambios.getCodPart());
        return repo.save(db);
    }
}