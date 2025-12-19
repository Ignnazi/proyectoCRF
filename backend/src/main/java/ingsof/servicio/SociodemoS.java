package ingsof.servicio;

import ingsof.entidad.Sociodemo;
import ingsof.repositorio.SociodemoR;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SociodemoS {
    private final SociodemoR repo;

    public SociodemoS(SociodemoR repo) {
        this.repo = repo;
    }

    @SuppressWarnings("null")
    public Sociodemo guardar(Sociodemo sociodemo) {
        validarEdad(sociodemo);
        validarCodPart(sociodemo);
        validarPrevision(sociodemo);
        return repo.save(sociodemo);
    }

    public void eliminar(int id) {
        repo.deleteById(id);
    }

    @SuppressWarnings("null")
    public void actualizar(int id, Sociodemo sociodemoActualizado) {
        Optional<Sociodemo> sociodemoExistente = repo.findById(id);
        if (sociodemoExistente.isPresent()) {
            Sociodemo s = sociodemoExistente.get();

            s.setEdad(sociodemoActualizado.getEdad());
            s.setSexo(sociodemoActualizado.getSexo());
            s.setNacionalidad(sociodemoActualizado.getNacionalidad());
            s.setDireccion(sociodemoActualizado.getDireccion());
            s.setComuna(sociodemoActualizado.getComuna());
            s.setCiudad(sociodemoActualizado.getCiudad());
            s.setZona(sociodemoActualizado.getZona());
            s.setViveMas5(sociodemoActualizado.getViveMas5());
            s.setEducacion(sociodemoActualizado.getEducacion());
            s.setOcupacion(sociodemoActualizado.getOcupacion());
            s.setPrevisionSalud(sociodemoActualizado.getPrevisionSalud());
            s.setPrevisionOtra(sociodemoActualizado.getPrevisionOtra());

            validarEdad(s);
            validarPrevision(s);

            repo.save(s);
        }
    }

    public Optional<Sociodemo> obtener(int id) {
        return repo.findById(id);
    }

    public List<Sociodemo> listar() {
        return repo.findAll();
    }

    private void validarEdad(Sociodemo sociodemo) {
        if (sociodemo.getEdad() == null){
            throw new IllegalArgumentException("La edad no puede ser nula");
        }
        if (sociodemo.getEdad() < 18) {
            throw new IllegalArgumentException("La edad debe ser mayor o igual a 18");
        }
    }

    private void validarCodPart(Sociodemo sociodemo) {
        if (isBlank(sociodemo.getCodPart())) {
            throw new IllegalArgumentException("cod_part es obligatorio");
        }
    }

    private void validarPrevision(Sociodemo sociodemo) {
        String p = trimToNull(sociodemo.getPrevisionSalud());
        if (p == null) return;

        if ("Otra".equalsIgnoreCase(p)) {
            if (isBlank(sociodemo.getPrevisionOtra())) {
                throw new IllegalArgumentException("Si selecciona 'Otra' debe indicar cuál.");
            }
        } else {
            sociodemo.setPrevisionOtra(null);
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
