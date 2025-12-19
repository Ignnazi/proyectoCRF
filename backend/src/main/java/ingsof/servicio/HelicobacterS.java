package ingsof.servicio;

import ingsof.entidad.Helicobacter;
import ingsof.repositorio.HelicobacterR;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class HelicobacterS {

    private final HelicobacterR repo;

    public HelicobacterS(HelicobacterR repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public List<Helicobacter> listar() {
        return repo.findAll();
    }

    @SuppressWarnings("null")
    @Transactional(readOnly = true)
    public Helicobacter porId(int id) {
        return repo.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe helicobacter con ese ID"));
    }

    @Transactional
    public Helicobacter crear(Helicobacter helicobacter) {
        return repo.save(helicobacter);
    }

    @SuppressWarnings("null")
    @Transactional
    public Helicobacter actualizar(int id, Helicobacter cambios) {
        Helicobacter db = porId(id);
        if (cambios.getPrueba() != null) db.setPrueba(cambios.getPrueba());
        if (cambios.getResultado() != null) db.setResultado(cambios.getResultado());
        if (cambios.getAntiguedad() != null) db.setAntiguedad(cambios.getAntiguedad());
        if (cambios.getCodPart() != null) db.setCodPart(cambios.getCodPart());
        return repo.save(db);
    }

    @Transactional
    public void eliminar(int id) {
        if (!repo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe helicobacter con ese ID");
        }
        repo.deleteById(id);
    }

}
