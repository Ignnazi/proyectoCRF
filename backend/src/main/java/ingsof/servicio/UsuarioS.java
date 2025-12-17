package ingsof.servicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import ingsof.entidad.Usuario;
import ingsof.repositorio.UsuarioR;

@Service
public class UsuarioS {

    @Autowired
    private UsuarioR repo;

    // Listar todos los usuarios
    public List<Usuario> listar() {
        return repo.findAll();
    }

    // Buscar un usuario por id
    public Usuario obtenerPorId(int id) {
        return repo.findById(id).orElse(null);
    }

    // Crear o actualizar usuario
    @SuppressWarnings("null")
    public Usuario guardar(Usuario usuario) {
    return repo.save(usuario);
    }

    // Buscar por nombre
    public Optional<Usuario> buscarPorNombre(String nombre) {
        return repo.findByNombre(nombre);
    }


    // Eliminar usuario por id
    public void eliminar(int id) {
        repo.deleteById(id);
    }
}
