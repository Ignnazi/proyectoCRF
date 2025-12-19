package ingsof.servicio;

import ingsof.entidad.Antecedente;
import ingsof.entidad.Participantecrf;
import ingsof.repositorio.AntecedenteR;
import ingsof.repositorio.ParticipantecrfR;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class AntecedenteS {

    private final AntecedenteR repo;
    private final ParticipantecrfR partRepo;

    public AntecedenteS(AntecedenteR repo, ParticipantecrfR partRepo) {
        this.repo = repo;
        this.partRepo = partRepo;
    }

    @SuppressWarnings("null")
    public Antecedente guardar(Antecedente antecedente) {
        validarCodPart(antecedente);
        validarDiagnosticoFecha(antecedente);
        validarFamOtro(antecedente);
        validarMedGastro(antecedente);
        return repo.save(antecedente);
    }

    @SuppressWarnings("null")
    public Antecedente crear(Antecedente antecedente) {
        validarCodPart(antecedente);
        validarGrupoParticipante(antecedente);
        validarDiagnosticoFecha(antecedente);
        validarFamOtro(antecedente);
        validarMedGastro(antecedente);
        return repo.save(antecedente);
    }

    public Antecedente porId(Integer id) {
        return repo.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Antecedente no encontrado con id: " + id));
    }

    public void eliminar(int id) {
        repo.deleteById(id);
    }

    public Antecedente actualizar(int id, Antecedente a) {
        Optional<Antecedente> existente = repo.findById(id);
        if (existente.isPresent()) {
            Antecedente x = existente.get();

            x.setDiagnostico(a.getDiagnostico());
            x.setFechaDiag(a.getFechaDiag());
            x.setFamCg(a.getFamCg());
            x.setFamOtro(a.getFamOtro());
            x.setOtroCancer(a.getOtroCancer());
            x.setOtrasEnfermedades(a.getOtrasEnfermedades());
            x.setMedGastro(a.getMedGastro());
            x.setMedGastroCual(a.getMedGastroCual());
            x.setCirugia(a.getCirugia());

            validarGrupoParticipante(x);
            validarDiagnosticoFecha(x);
            validarFamOtro(x);
            validarMedGastro(x);

            return repo.save(x);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Antecedente no encontrado con id: " + id);
    }

    public Optional<Antecedente> obtener(int id) {
        return repo.findById(id);
    }

    public List<Antecedente> listar() {
        return repo.findAll();
    }

    private void validarCodPart(Antecedente a) {
        if (isBlank(a.getCodPart())) {
            throw new IllegalArgumentException("cod_part es obligatorio");
        }
    }

    private void validarGrupoParticipante(Antecedente a) {
        String codPart = a.getCodPart();
        if (isBlank(codPart)) return;

        Optional<Participantecrf> participanteOpt = partRepo.findById(codPart);
        if (participanteOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Participante no encontrado: " + codPart);
        }

        Participantecrf participante = participanteOpt.get();
        String grupo = participante.getGrupo();

        // Si NO es Caso, no puede tener diagnóstico ni fecha
        if (!"Caso".equalsIgnoreCase(grupo)) {
            if ("Sí".equalsIgnoreCase(a.getDiagnostico())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Un participante del grupo '" + grupo + "' no puede tener diagnóstico de cáncer gástrico");
            }
            if (a.getFechaDiag() != null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Un participante del grupo '" + grupo + "' no puede tener fecha de diagnóstico");
            }
        }
    }

    private void validarDiagnosticoFecha(Antecedente a) {
        String d = trimToNull(a.getDiagnostico());
        if (d == null) return;

        if ("Sí".equalsIgnoreCase(d)) {
            if (a.getFechaDiag() == null) {
                throw new IllegalArgumentException("Si diagnóstico es 'Sí', debe ingresar fecha de diagnóstico.");
            }
        } else {
            a.setFechaDiag(null);
        }
    }

    private void validarFamOtro(Antecedente a) {
        String f = trimToNull(a.getFamOtro());
        if (f == null) return;

        if ("Sí".equalsIgnoreCase(f)) {
            if (isBlank(a.getOtroCancer())) {
                throw new IllegalArgumentException("Si antecedentes familiares de otros cánceres es 'Sí', debe indicar cuál(es).");
            }
        } else {
            a.setOtroCancer(null);
        }
    }

    private void validarMedGastro(Antecedente a) {
        String m = trimToNull(a.getMedGastro());
        if (m == null) return;

        if ("Sí".equalsIgnoreCase(m)) {
            if (isBlank(a.getMedGastroCual())) {
                throw new IllegalArgumentException("Si uso crónico de medicamentos es 'Sí', debe especificar cuál.");
            }
        } else {
            a.setMedGastroCual(null);
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private String trimToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
