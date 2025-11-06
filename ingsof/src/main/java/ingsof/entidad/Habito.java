package ingsof.entidad;

import jakarta.persistence.*;

@Entity
@Table(name = "habito")
public class Habito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_habit")
    private int idHabit;

    private String tipo;              // 'Fumar', 'Beber', 'Ejercicio', 'Otro'
    private String estado;            // 'Nunca', 'Ex', 'Actual'
    private String frecuencia;
    private String cantidad;

    @Column(name = "anios_consumo")
    private String aniosConsumo;

    @Column(name = "tiempo_dejado")
    private String tiempoDejado;

    @Column(name = "cod_part")
    private String codPart;

     @Column(name = "edad_inicio")
    private Integer edadInicio;

    // Relación N:1 con Participante (un participante puede tener varios hábitos)
    @ManyToOne
    @JoinColumn(name = "cod_part", referencedColumnName = "cod_part", insertable = false, updatable = false)
    private Participantecrf participante;

    public Habito() { }

    // Getters y setters

    public int getIdHabit() {
        return idHabit;
    }

    public void setIdHabit(int idHabit) {
        this.idHabit = idHabit;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getFrecuencia() {
        return frecuencia;
    }

    public void setFrecuencia(String frecuencia) {
        this.frecuencia = frecuencia;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public String getAniosConsumo() {
        return aniosConsumo;
    }

    public void setAniosConsumo(String aniosConsumo) {
        this.aniosConsumo = aniosConsumo;
    }

    public String getTiempoDejado() {
        return tiempoDejado;
    }

    public void setTiempoDejado(String tiempoDejado) {
        this.tiempoDejado = tiempoDejado;
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

      public Integer getEdadInicio() {
        return edadInicio;
    }

    public void setEdadInicio(Integer edadInicio) {
        this.edadInicio = edadInicio;
    }
    
}
