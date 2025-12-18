package ingsof.entidad;

import jakarta.persistence.*;

@Entity
@Table(name = "helicobacter")
public class Helicobacter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_helic")
    private Integer idHelic;

    @Column(name = "resultado_exam")
    private String resultadoExam; // Positivo / Negativo / Desconocido

    @Column(name = "pasado_positivo")
    private String pasadoPositivo; // Sí / No / No recuerda

    @Column(name = "pasado_detalle")
    private String pasadoDetalle; // texto libre

    private String tratamiento; // Sí / No / No recuerda

    @Column(name = "tratamiento_detalle")
    private String tratamientoDetalle; // texto libre

    @Column(name = "tipo_examen")
    private String tipoExamen; // "Aliento|Antígeno|Serología|..."

    @Column(name = "otro_examen")
    private String otroExamen; // si marcó "Otro"

    private String antiguedad; // <1 año / 1-5 años / >5 años

    @Column(name = "uso_ibp_abx")
    private String usoIbpAbx; // Sí / No / No recuerda

    private String repetido; // Sí / No

    @Column(name = "repetido_fecha")
    private java.sql.Date repetidoFecha;

    @Column(name = "repetido_resultado")
    private String repetidoResultado; // Positivo / Negativo / Desconocido

    @Column(name = "cod_part")
    private String codPart;

    // Relación 1:1 con Participante
    @OneToOne
    @JoinColumn(name = "cod_part", referencedColumnName = "cod_part", insertable = false, updatable = false)
    private Participantecrf participante;

    public Helicobacter() {}

    public Integer getIdHelic() { return idHelic; }
    public void setIdHelic(Integer idHelic) { this.idHelic = idHelic; }

    public String getResultadoExam() { return resultadoExam; }
    public void setResultadoExam(String resultadoExam) { this.resultadoExam = resultadoExam; }

    public String getPasadoPositivo() { return pasadoPositivo; }
    public void setPasadoPositivo(String pasadoPositivo) { this.pasadoPositivo = pasadoPositivo; }

    public String getPasadoDetalle() { return pasadoDetalle; }
    public void setPasadoDetalle(String pasadoDetalle) { this.pasadoDetalle = pasadoDetalle; }

    public String getTratamiento() { return tratamiento; }
    public void setTratamiento(String tratamiento) { this.tratamiento = tratamiento; }

    public String getTratamientoDetalle() { return tratamientoDetalle; }
    public void setTratamientoDetalle(String tratamientoDetalle) { this.tratamientoDetalle = tratamientoDetalle; }

    public String getTipoExamen() { return tipoExamen; }
    public void setTipoExamen(String tipoExamen) { this.tipoExamen = tipoExamen; }

    public String getOtroExamen() { return otroExamen; }
    public void setOtroExamen(String otroExamen) { this.otroExamen = otroExamen; }

    public String getAntiguedad() { return antiguedad; }
    public void setAntiguedad(String antiguedad) { this.antiguedad = antiguedad; }

    public String getUsoIbpAbx() { return usoIbpAbx; }
    public void setUsoIbpAbx(String usoIbpAbx) { this.usoIbpAbx = usoIbpAbx; }

    public String getRepetido() { return repetido; }
    public void setRepetido(String repetido) { this.repetido = repetido; }

    public java.sql.Date getRepetidoFecha() { return repetidoFecha; }
    public void setRepetidoFecha(java.sql.Date repetidoFecha) { this.repetidoFecha = repetidoFecha; }

    public String getRepetidoResultado() { return repetidoResultado; }
    public void setRepetidoResultado(String repetidoResultado) { this.repetidoResultado = repetidoResultado; }

    public String getCodPart() { return codPart; }
    public void setCodPart(String codPart) { this.codPart = codPart; }

    public Participantecrf getParticipante() { return participante; }
    public void setParticipante(Participantecrf participante) { this.participante = participante; }
}
