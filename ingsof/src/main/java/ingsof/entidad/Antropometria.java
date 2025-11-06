package ingsof.entidad;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
@Table(name = "antropometria")
public class Antropometria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_antrop")
    private Integer idAntrop;

    private Double peso;
    private Double estatura;
    private Double imc;

    @Column(name = "cod_part", nullable = false, length = 5)
    private String codPart;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "cod_part", referencedColumnName = "cod_part", insertable = false, updatable = false)
    private Participantecrf participante;

    public Antropometria() { }

    public Integer getIdAntrop() {
        return idAntrop;
    }
    public void setIdAntrop(Integer idAntrop) {
        this.idAntrop = idAntrop;
    }
    public Double getPeso() {
        return peso;
    }
    public void setPeso(Double peso) {
        this.peso = peso;
    }
    public Double getEstatura() {
        return estatura;
    }
    public void setEstatura(Double estatura) {
        this.estatura = estatura;
    }
    public Double getImc() {
        return imc;
    }
    public void setImc(Double imc) {
        this.imc = imc;
    }
    public String getCodPart() {
        return codPart;
    }
    public void setCodPart(String codPart) { t
    his.codPart = codPart;
    }
    public Participantecrf getParticipante() {
        return participante;
    }
    public void setParticipante(Participantecrf participante) {
        this.participante = participante;
    }
}
