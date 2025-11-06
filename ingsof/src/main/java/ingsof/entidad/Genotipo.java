package ingsof.entidad;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "genotipo")
public class Genotipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_genotip")
    private int idGenotip;

    @Column(name = "fecha_toma")
    private LocalDate fechaToma;

    @Column(name = "tlr9_rs5743836")
    private String tlr9Rs5743836;

    @Column(name = "tlr9_rs187084")
    private String tlr9Rs187084;

    @Column(name = "mir146a_rs2910164")
    private String mir146aRs2910164;

    @Column(name = "mir196a2_rs11614913")
    private String mir196a2Rs11614913;

    @Column(name = "mthfr_rs1801133")
    private String mthfrRs1801133;

    @Column(name = "dnmt3b_rs1569686")
    private String dnmt3bRs1569686;

    @Column(name = "cod_part")
    private String codPart;

    // Relación 1:1 con Participante
    @OneToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_part", referencedColumnName = "cod_part", insertable = false, updatable = false)
    private Participantecrf participante;

    public Genotipo() { }
    // Getters y setters
    public int getIdGenotip() {
        return idGenotip;
    }

    public void setIdGenotip(int idGenotip) {
        this.idGenotip = idGenotip;
    }

    public LocalDate getFechaToma() {
        return fechaToma;
    }

    public void setFechaToma(LocalDate fechaToma) {
        this.fechaToma = fechaToma;
    }

    public String getTlr9Rs5743836() {
        return tlr9Rs5743836;
    }

    public void setTlr9Rs5743836(String tlr9Rs5743836) {
        this.tlr9Rs5743836 = tlr9Rs5743836;
    }

    public String getTlr9Rs187084() {
        return tlr9Rs187084;
    }

    public void setTlr9Rs187084(String tlr9Rs187084) {
        this.tlr9Rs187084 = tlr9Rs187084;
    }

    public String getMir146aRs2910164() {
        return mir146aRs2910164;
    }

    public void setMir146aRs2910164(String mir146aRs2910164) {
        this.mir146aRs2910164 = mir146aRs2910164;
    }

    public String getMir196a2Rs11614913() {
        return mir196a2Rs11614913;
    }

    public void setMir196a2Rs11614913(String mir196a2Rs11614913) {
        this.mir196a2Rs11614913 = mir196a2Rs11614913;
    }

    public String getMthfrRs1801133() {
        return mthfrRs1801133;
    }

    public void setMthfrRs1801133(String mthfrRs1801133) {
        this.mthfrRs1801133 = mthfrRs1801133;
    }

    public String getDnmt3bRs1569686() {
        return dnmt3bRs1569686;
    }

    public void setDnmt3bRs1569686(String dnmt3bRs1569686) {
        this.dnmt3bRs1569686 = dnmt3bRs1569686;
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
