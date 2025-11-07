package ingsof.servicio;

import ingsof.entidad.Genotipo;
import ingsof.repositorio.GenotipoR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class GenotipoS {

    private final GenotipoR repo;

    private static final List<String> VALORES_PERMITIDOS =
        Arrays.asList("TT", "TC", "CC", "GG", "GC", "GT");

    @Autowired
    public GenotipoS(GenotipoR repo) {
        this.repo = repo;
    }


    public Optional<Genotipo> obtenerPorId(int id) {
        return repo.findById(id);
    }

    public List<Genotipo> listar() {
        return repo.findAll();
    }

    public void guardar(Genotipo genotipo) {
        validarCamposGenotipo(genotipo);
        
        // Si la fecha de toma no está establecida, usar la fecha actual
        if (genotipo.getFechaToma() == null) {
            genotipo.setFechaToma(LocalDate.now());
        }
        
        repo.save(genotipo);
    }
    
    public void eliminar(int id) {
        repo.deleteById(id);
    }

    public Genotipo actualizar(int id, Genotipo genotipoActualizado) {
        return repo.findById(id).map(genotipo -> {
            // Actualizar solo los campos que se permiten modificar
            if (genotipoActualizado.getTlr9Rs5743836() != null) {
                genotipo.setTlr9Rs5743836(genotipoActualizado.getTlr9Rs5743836());
            }
            if (genotipoActualizado.getTlr9Rs187084() != null) {
                genotipo.setTlr9Rs187084(genotipoActualizado.getTlr9Rs187084());
            }
            if (genotipoActualizado.getMir146aRs2910164() != null) {
                genotipo.setMir146aRs2910164(genotipoActualizado.getMir146aRs2910164());
            }
            if (genotipoActualizado.getMir196a2Rs11614913() != null) {
                genotipo.setMir196a2Rs11614913(genotipoActualizado.getMir196a2Rs11614913());
            }
            if (genotipoActualizado.getMthfrRs1801133() != null) {
                genotipo.setMthfrRs1801133(genotipoActualizado.getMthfrRs1801133());
            }
            if (genotipoActualizado.getDnmt3bRs1569686() != null) {
                genotipo.setDnmt3bRs1569686(genotipoActualizado.getDnmt3bRs1569686());
            }

            // Validar los campos actualizados
            validarCamposGenotipo(genotipo);

            return repo.save(genotipo);
        }).orElseThrow(() -> new RuntimeException("Genotipo no encontrado con id: " + id));
    }

    private void validarCamposGenotipo(Genotipo genotipo) {
        if (genotipo == null) {
            throw new IllegalArgumentException("El objeto Genotipo no puede ser nulo");
        }

        // Validar cada campo genotípico
        validarCampo(genotipo.getTlr9Rs5743836(), "tlr9Rs5743836");
        validarCampo(genotipo.getTlr9Rs187084(), "tlr9Rs187084");
        validarCampo(genotipo.getMir146aRs2910164(), "mir146aRs2910164");
        validarCampo(genotipo.getMir196a2Rs11614913(), "mir196a2Rs11614913");
        validarCampo(genotipo.getMthfrRs1801133(), "mthfrRs1801133");
        validarCampo(genotipo.getDnmt3bRs1569686(), "dnmt3bRs1569686");
    }
    
    private void validarCampo(String valor, String nombreCampo) {
        // Si el valor es nulo o vacío, no es necesario validar (asumiendo que es opcional)
        if (valor == null || valor.trim().isEmpty()) {
            return;
        }
        
        String valorUpper = valor.toUpperCase();
        
        if (!VALORES_PERMITIDOS.contains(valorUpper)) {
            throw new IllegalArgumentException(String.format(
                "El campo %s debe tener uno de los siguientes valores: %s. Valor recibido: %s",
                nombreCampo,
                String.join(", ", VALORES_PERMITIDOS),
                valor
            ));
        }
    }
}
