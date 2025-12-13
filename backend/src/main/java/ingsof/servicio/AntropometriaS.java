package ingsof.servicio;

import ingsof.entidad.Antropometria;
import ingsof.repositorio.AntropometriaR;
import ingsof.repositorio.ParticipantecrfR;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AntropometriaS {

    private final AntropometriaR repo;
    private final ParticipantecrfR partRepo;

    public AntropometriaS(AntropometriaR repo, ParticipantecrfR partRepo) {
        this.repo = repo; this.partRepo = partRepo;
    }

    private double calcImc(Double peso, Double est) {
        if (peso == null || est == null || peso <= 0 || est <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "peso/estatura inválidos");
        return Math.round((peso / (est * est)) * 10.0) / 10.0;
    }

    @Transactional(readOnly = true)
    public List<Antropometria> listar(){ return repo.findAll(); }

    @SuppressWarnings("null")
    @Transactional(readOnly = true)
    public Antropometria porId(Integer id){
        return repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "no existe antropometría"));
    }

    @Transactional(readOnly = true)
    public List<Antropometria> porParticipante(String codPart){ return repo.findAllByCodPart(codPart); }

    @SuppressWarnings("null")
    @Transactional
    public Antropometria crear(Antropometria a){
        if(!partRepo.existsById(a.getCodPart())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"participante no encontrado");
        a.setImc(calcImc(a.getPeso(), a.getEstatura()));
        return repo.save(a);
    }

    @SuppressWarnings("null")
    @Transactional
    public Antropometria actualizar(Integer id, Antropometria c){
        Antropometria db = porId(id);
        if(c.getPeso()!=null) db.setPeso(c.getPeso());
        if(c.getEstatura()!=null) db.setEstatura(c.getEstatura());
        if(c.getCodPart()!=null){
            if(!partRepo.existsById(c.getCodPart())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"participante no encontrado");
            db.setCodPart(c.getCodPart());
        }
        db.setImc(calcImc(db.getPeso(), db.getEstatura()));
        return repo.save(db);
    }
}
