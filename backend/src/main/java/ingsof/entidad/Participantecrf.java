package ingsof.entidad;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "participantecrf")
public class Participantecrf implements Serializable {

    @Id
    @Column(name = "cod_part", nullable = false, length = 5)
    private String codPart;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "correo", length = 120)
    private String correo;

    @Column(name = "grupo", nullable = false, length = 20)
    private String grupo;

    @Column(name = "id_user")
    private Integer idUser;

    @Column(name = "fecha_inclusion", nullable = false)
    private LocalDateTime fechaInclusion;

    public Participantecrf() {}

    public Participantecrf(String codPart, String nombre, String telefono, String correo,
                           String grupo, Integer idUser, LocalDateTime fechaInclusion) {
        this.codPart = codPart;
        this.nombre = nombre;
        this.telefono = telefono;
        this.correo = correo;
        this.grupo = grupo;
        this.idUser = idUser;
        this.fechaInclusion = fechaInclusion;
    }

    public String getCodPart() { return codPart; }
    public void setCodPart(String codPart) { this.codPart = codPart; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getGrupo() { return grupo; }
    public void setGrupo(String grupo) { this.grupo = grupo; }

    public Integer getIdUser() { return idUser; }
    public void setIdUser(Integer idUser) { this.idUser = idUser; }

    public LocalDateTime getFechaInclusion() { return fechaInclusion; }
    public void setFechaInclusion(LocalDateTime fechaInclusion) { this.fechaInclusion = fechaInclusion; }
}
