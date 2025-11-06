package ingsof.entidad;

import jakarta.persistence.*;

@Entity
@Table(name = "histopatologia")
public class Histopatologia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_histo")
    private int idHisto;

    private String tipo;          // 'Intestinal', 'Difuso', 'Mixto', 'Otro'
    private String localizacion;  // 'Cardias', 'Cuerpo', 'Antro', 'Difuso'
    private String estadio;

    @Column(name = "cod_part")
    private String codPart;

    // Relación 1:1 con Participante
    @OneToOne
    @JoinColumn(name = "cod_part", referencedColumnName = "cod_part", insertable = false, updatable = false)
    private Participantecrf participante;

    public Histopatologia() { }

    // Getters y setters
    public int getIdHisto() {
        return idHisto;
    }

    public void setIdHisto(int idHisto) {
        this.idHisto = idHisto;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getLocalizacion() {
        return localizacion;
    }

    public void setLocalizacion(String localizacion) {
        this.localizacion = localizacion;
    }

    public String getEstadio() {
        return estadio;
    }

    public void setEstadio(String estadio) {
        this.estadio = estadio;
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



