package ingsof.servicio;

import ingsof.entidad.Helicobacter;
import ingsof.repositorio.HelicobacterR;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HelicobacterS {

    private final HelicobacterR repo;

    public HelicobacterS(HelicobacterR repo) {
        this.repo = repo;
    }

    public List<Helicobacter> listar() {
        return repo.findAll();
    }

    @SuppressWarnings("null")
    public void guardar(Helicobacter helicobacter) {
        repo.save(helicobacter);
    }

    public void eliminar(int id) {
        repo.deleteById(id);
    }

    public Optional<Helicobacter> obtener(int id) {
        return repo.findById(id);
    }

    @SuppressWarnings("null")
    public Helicobacter porId(Integer id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Helicobacter no encontrado"));
    }

    public Helicobacter porCodPart(String codPart) {
        return repo.findByCodPart(codPart).orElse(null);
    }

    @SuppressWarnings("null")
    public Helicobacter crear(Helicobacter body) {
        return repo.save(body);
    }

    public Helicobacter actualizar(Integer id, Helicobacter body) {
        Helicobacter h = porId(id);

        h.setResultadoExam(body.getResultadoExam());
        h.setPasadoPositivo(body.getPasadoPositivo());
        h.setPasadoDetalle(body.getPasadoDetalle());
        h.setTratamiento(body.getTratamiento());
        h.setTratamientoDetalle(body.getTratamientoDetalle());
        h.setTipoExamen(body.getTipoExamen());
        h.setOtroExamen(body.getOtroExamen());
        h.setAntiguedad(body.getAntiguedad());
        h.setUsoIbpAbx(body.getUsoIbpAbx());
        h.setRepetido(body.getRepetido());
        h.setRepetidoFecha(body.getRepetidoFecha());
        h.setRepetidoResultado(body.getRepetidoResultado());
        h.setCodPart(body.getCodPart());

        return repo.save(h);
    }
}
