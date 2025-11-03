package ingsof.servicio;

import ingsof.entidad.Antecedente;
import ingsof.repositorio.AntecedenteR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class AntecedenteS {

    private final AntecedenteR repo;

    @Autowired
    public AntecedenteS(AntecedenteR repo) {
        this.repo = repo;
    }

    // Obtener todos los antecedentes
    public List<Antecedente> listar() {
        return repo.findAll();
    }

    // Buscar un antecedente por ID
    public Optional<Antecedente> obtenerPorId(int id) {
        return repo.findById(id);
    }

    // Crear o actualizar antecedente
    public Antecedente guardar(Antecedente antecedente) {
        return repo.save(antecedente);
    }

    // Eliminar antecedente por ID
    public void eliminar(int id) {
        repo.deleteById(id);
    }

    // Actualizar antecedente existente
    public void actualizar(int id, Antecedente antecedenteActualizado) {
        Optional<Antecedente> antecedenteExistente = repo.findById(id);
        if (antecedenteExistente.isPresent()) {
            antecedenteExistente.get().setDiagnostico(antecedenteActualizado.getDiagnostico());
            antecedenteExistente.get().setFechaDiag(antecedenteActualizado.getFechaDiag());
            antecedenteExistente.get().setFamCg(antecedenteActualizado.getFamCg());
            antecedenteExistente.get().setFamOtro(antecedenteActualizado.getFamOtro());
            antecedenteExistente.get().setMedicamentos(antecedenteActualizado.getMedicamentos());
            antecedenteExistente.get().setOtroCancer(antecedenteActualizado.getOtroCancer());
            antecedenteExistente.get().setCirugia(antecedenteActualizado.getCirugia());
            repo.save(antecedenteExistente.get());
        }
    }
}
