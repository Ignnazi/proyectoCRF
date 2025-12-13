package ingsof.entidad;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "histopatologia")
public class Histopatologia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_histo")
    private Integer idHisto;

    private String tipo;
    private String localizacion;
    private String estadio;

    @Column(name = "cod_part", nullable = false, length = 5)
    private String codPart;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "cod_part", referencedColumnName = "cod_part", insertable = false, updatable = false)
    private Participantecrf participante;

    public Histopatologia() { }
    public Integer getIdHisto() { return idHisto; }
    public void setIdHisto(Integer idHisto) { this.idHisto = idHisto; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getLocalizacion() { return localizacion; }
    public void setLocalizacion(String localizacion) { this.localizacion = localizacion; }
    public String getEstadio() { return estadio; }
    public void setEstadio(String estadio) { this.estadio = estadio; }
    public String getCodPart() { return codPart; }
    public void setCodPart(String codPart) { this.codPart = codPart; }
    public Participantecrf getParticipante() { return participante; }
    public void setParticipante(Participantecrf participante) { this.participante = participante; }
}
