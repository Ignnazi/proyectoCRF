package ingsof.servicio;

import ingsof.entidad.Antecedente;
import ingsof.repositorio.AntecedenteR;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AntecedenteS {

    private final AntecedenteR repo;

    public AntecedenteS(AntecedenteR repo) {
        this.repo = repo;
    }

    @SuppressWarnings("null")
    public void guardar(Antecedente antecedente) {
        validarCodPart(antecedente);
        validarDiagnosticoFecha(antecedente);
        validarFamOtro(antecedente);
        validarMedGastro(antecedente);
        repo.save(antecedente);
    }

    public void eliminar(int id) {
        repo.deleteById(id);
    }

    public void actualizar(int id, Antecedente a) {
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

            validarDiagnosticoFecha(x);
            validarFamOtro(x);
            validarMedGastro(x);

            repo.save(x);
        }
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
