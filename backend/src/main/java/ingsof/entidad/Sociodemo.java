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
    private String sexo;
    private String nacionalidad;
    private String direccion;
    private String comuna;
    private String ciudad;
    private String zona;

    @Column(name = "vive_mas_5")
    private String viveMas5;

    private String educacion;
    private String ocupacion;

    @Column(name = "prevision_salud")
    private String previsionSalud;

    @Column(name = "prevision_otra")
    private String previsionOtra;

    @Column(name = "cod_part")
    private String codPart;

    @OneToOne
    @JoinColumn(name = "cod_part", referencedColumnName = "cod_part", insertable = false, updatable = false)
    private Participantecrf participante;

    public Sociodemo() { }

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

    public String getComuna() {
        return comuna;
    }

    public void setComuna(String comuna) {
        this.comuna = comuna;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getZona() {
        return zona;
    }

    public void setZona(String zona) {
        this.zona = zona;
    }

    public String getViveMas5() {
        return viveMas5;
    }

    public void setViveMas5(String viveMas5) {
        this.viveMas5 = viveMas5;
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

    public String getPrevisionSalud() {
        return previsionSalud;
    }

    public void setPrevisionSalud(String previsionSalud) {
        this.previsionSalud = previsionSalud;
    }

    public String getPrevisionOtra() {
        return previsionOtra;
    }

    public void setPrevisionOtra(String previsionOtra) {
        this.previsionOtra = previsionOtra;
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
