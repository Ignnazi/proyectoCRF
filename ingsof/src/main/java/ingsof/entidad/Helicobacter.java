package ingsof.entidad;

import jakarta.persistence.*;

@Entity
@Table(name = "helicobacter")
public class Helicobacter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_helic")
    private int idHelic;

    private String prueba;         // 'Aliento', 'Antígeno', 'Endoscopía'
    private String resultado;      // 'Positivo' o 'Negativo'
    private String antiguedad;     // '<1 año', '1–5 años', '>5 años'

    @Column(name = "cod_part")
    private String codPart;

    // Relación 1:1 con Participante
    @OneToOne
    @JoinColumn(name = "cod_part", referencedColumnName = "cod_part", insertable = false, updatable = false)
    private Participantecrf participante;

    public Helicobacter() { }

    // Getters y setters
    public int getIdHelic() {
        return idHelic;
    }

    public void setIdHelic(int idHelic) {
        this.idHelic = idHelic;
    }

    public String getPrueba() {
        return prueba;
    }

    public void setPrueba(String prueba) {
        this.prueba = prueba;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public String getAntiguedad() {
        return antiguedad;
    }

    public void setAntiguedad(String antiguedad) {
        this.antiguedad = antiguedad;
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

