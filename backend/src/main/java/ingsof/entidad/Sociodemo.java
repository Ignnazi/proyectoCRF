package ingsof.entidad;

import jakarta.persistence.*;

@Entity
@Table(name = "sociodemo")
public class Sociodemo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_socdemo")
    private int idSocdemo;
    private Integer edad;
    private String sexo;              // 'Hombre' o 'Mujer'
    private String nacionalidad;
    private String direccion;
    private String zona;              // 'Urbana' o 'Rural'

    @Column(name = "anios_res")
    private String aniosRes;          // '<5', '5–10', '>10'

    private String educacion;         // 'Básico', 'Medio', 'Superior'
    private String ocupacion;

    @Column(name = "cod_part")
    private String codPart;

    // Relación 1:1 con Participante
    @OneToOne
    @JoinColumn(name = "cod_part", referencedColumnName = "cod_part", insertable = false, updatable = false)
    private Participantecrf participante;

    public Sociodemo() { }

    // Getters y setters
    public int getIdSocdemo() {
        return idSocdemo;
    }

    public void setIdSocdemo(int idSocdemo) {
        this.idSocdemo = idSocdemo;
    }

    public Integer getEdad() {
        return edad;
    }

    public void setEdad(Integer edad) {
        this.edad = edad;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getNacionalidad() {
        return nacionalidad;
    }

    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getZona() {
        return zona;
    }

    public void setZona(String zona) {
        this.zona = zona;
    }

    public String getAniosRes() {
        return aniosRes;
    }

    public void setAniosRes(String aniosRes) {
        this.aniosRes = aniosRes;
    }

    public String getEducacion() {
        return educacion;
    }

    public void setEducacion(String educacion) {
        this.educacion = educacion;
    }

    public String getOcupacion() {
        return ocupacion;
    }

    public void setOcupacion(String ocupacion) {
        this.ocupacion = ocupacion;
    }

    public String getCodPart() {
        return codPart;
    }

    public void setCodPart(String codPart) {
        this.codPart = codPart;
    }

    public Participantecrf getParticipante() {
        return participante;
    }

    public void setParticipante(Participantecrf participante) {
        this.participante = participante;
    }
}
