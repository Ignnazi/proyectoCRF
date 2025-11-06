package ingsof.entidad;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "antecedente")
public class Antecedente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_antec")
    private int idAntec;

    private String diagnostico;

    @Column(name = "fecha_diag")
    private LocalDate fechaDiag;

    @Column(name = "fam_cg")
    private String famCg;

    @Column(name = "fam_otro")
    private String famOtro;

    @Column(name = "otro_cancer")
    private String otroCancer;

    @Column(name = "otras_enfermedades")
    private String otrasEnfermedades;

    private String medicamentos;
    private String cirugia;

    @Column(name = "cod_part")
    private String codPart;

    // Relación con participante (1:1)
    @OneToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_part", referencedColumnName = "cod_part", insertable = false, updatable = false)
    private Participantecrf participante;

    public Antecedente() { }

    // Getters y setters
    public int getIdAntec() {
        return idAntec;
    }

    public void setIdAntec(int idAntec) {
        this.idAntec = idAntec;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public LocalDate getFechaDiag() {
        return fechaDiag;
    }

    public void setFechaDiag(LocalDate fechaDiag) {
        this.fechaDiag = fechaDiag;
    }

    public String getFamCg() {
        return famCg;
    }

    public void setFamCg(String famCg) {
        this.famCg = famCg;
    }

    public String getFamOtro() {
        return famOtro;
    }

    public void setFamOtro(String famOtro) {
        this.famOtro = famOtro;
    }

    public String getOtroCancer() {
        return otroCancer;
    }

    public void setOtroCancer(String otroCancer) {
        this.otroCancer = otroCancer;
    }

    public String getOtrasEnfermedades() {
        return otrasEnfermedades;
    }

    public void setOtrasEnfermedades(String otrasEnfermedades) {
        this.otrasEnfermedades = otrasEnfermedades;
    }

    public String getMedicamentos() {
        return medicamentos;
    }

    public void setMedicamentos(String medicamentos) {
        this.medicamentos = medicamentos;
    }

    public String getCirugia() {
        return cirugia;
    }

    public void setCirugia(String cirugia) {
        this.cirugia = cirugia;
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


