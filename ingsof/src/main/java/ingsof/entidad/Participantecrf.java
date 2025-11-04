package ingsof.entidad;

import jakarta.persistence.*;
//import java.time.LocalDateTime;

@Entity
@Table(name = "participantecrf")
public class Participantecrf {

    @Id
    @Column(name = "cod_part", insertable = false, updatable = false)
    private String codPart;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "grupo")
    private String grupo;

    //@Column(name = "fecha_inclusion", insertable = false, updatable = false)
    //private LocalDateTime fechaInclusion;

    @Column(name = "id_user")
    private int idUser;

    // Getters y setters
    public String getCodPart() { return codPart; }
    public void setCodPart(String codPart) { this.codPart = codPart; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getGrupo() { return grupo; }
    public void setGrupo(String grupo) { this.grupo = grupo; }

    //public LocalDateTime getFechaInclusion() { return fechaInclusion; }
    //public void setFechaInclusion(LocalDateTime fechaInclusion) { this.fechaInclusion = fechaInclusion; }

    public int getIdUser() { return idUser; }
    public void setIdUser(int idUser) { this.idUser = idUser; }
}


