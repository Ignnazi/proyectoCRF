package ingsof.servicio;

import ingsof.entidad.Participantecrf;
import ingsof.repositorio.ParticipantecrfR;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ParticipantecrfS {

    private final ParticipantecrfR repo;

    public ParticipantecrfS(ParticipantecrfR repo) { this.repo = repo; }

    private String prefijoPorGrupo(String grupo) {
        if (grupo == null) return "UN";
        if ("Caso".equalsIgnoreCase(grupo)) return "CS";
        if ("Control".equalsIgnoreCase(grupo)) return "CT";
        return "UN";
    }

    private String siguienteCodigo(String grupo) {
        String pref = prefijoPorGrupo(grupo);
        int last = repo.maxNumeroPorPrefijo(pref);
        return String.format("%s%03d", pref, last + 1); // 001..999
    }

    private void validarObligatorios(Participantecrf p) {
        if (p.getNombre() == null || p.getNombre().isBlank())
            throw new IllegalArgumentException("nombre es requerido");
        if (p.getGrupo() == null || p.getGrupo().isBlank())
            throw new IllegalArgumentException("grupo es requerido");
    }

    @Transactional(readOnly = true)
    public List<Participantecrf> listar() { return repo.findAll(); }

    @SuppressWarnings("null")
    @Transactional(readOnly = true)
    public Optional<Participantecrf> buscar(String codPart) { return repo.findById(codPart); }

    @Transactional
    public Participantecrf crear(Participantecrf p) {
        validarObligatorios(p);

        if (p.getFechaInclusion() == null) p.setFechaInclusion(LocalDateTime.now());

        if (p.getCodPart() == null || p.getCodPart().isBlank()) {
            for (int i = 0; i < 3; i++) {
                p.setCodPart(siguienteCodigo(p.getGrupo()));
                try {
                    return repo.save(p);
                } catch (DataIntegrityViolationException dup) {
                }
            }
            throw new IllegalStateException("No se pudo generar cod_part único tras 3 intentos");
        }

        if (repo.existsByCodPart(p.getCodPart()))
            throw new IllegalArgumentException("cod_part ya existe: " + p.getCodPart());

        return repo.save(p);
    }

    @SuppressWarnings("null")
    @Transactional
    public Participantecrf actualizar(String codActual, Participantecrf cambios) {
        Participantecrf db = repo.findById(codActual)
                .orElseThrow(() -> new IllegalArgumentException("No existe: " + codActual));

        boolean cambiaGrupo = cambios.getGrupo() != null &&
                              !cambios.getGrupo().equalsIgnoreCase(db.getGrupo());

        if (cambiaGrupo) {
            String nuevoCodigo = siguienteCodigo(cambios.getGrupo());
            int filas = repo.actualizarCodigo(nuevoCodigo, codActual);
            if (filas != 1) throw new IllegalStateException("Falló actualización de PK");

            Participantecrf nuevo = repo.findById(nuevoCodigo)
                    .orElseThrow(() -> new IllegalStateException("No se encontró nuevo PK"));

            if (cambios.getNombre() != null) nuevo.setNombre(cambios.getNombre());
            if (cambios.getIdUser() != null) nuevo.setIdUser(cambios.getIdUser());
            nuevo.setGrupo(cambios.getGrupo());
            if (cambios.getFechaInclusion() != null) nuevo.setFechaInclusion(cambios.getFechaInclusion());

            return repo.save(nuevo);
        }

        // sin cambio de grupo
        if (cambios.getNombre() != null) db.setNombre(cambios.getNombre());
        if (cambios.getIdUser() != null) db.setIdUser(cambios.getIdUser());
        if (cambios.getGrupo() != null) db.setGrupo(cambios.getGrupo());
        if (cambios.getFechaInclusion() != null) db.setFechaInclusion(cambios.getFechaInclusion());

        return repo.save(db);
    }

    @SuppressWarnings("null")
    @Transactional
    public void eliminar(String codPart) { repo.deleteById(codPart); }
}
