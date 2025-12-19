package ingsof.entidad;

import jakarta.persistence.*;

@Entity
@Table(name = "factor")
public class Factor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_factor")
    private int idFac;

    private String carnes;
    private String salados;
    private String frutas;
    private String frituras;

    private String condimentados;

    @Column(name = "bebidas_calientes")
    private String bebidasCalientes;

    private String pesticidas;
    private String quimicos;

    @Column(name = "detalle_quimicos")
    private String detalleQuimicos;

    @Column(name = "humo_lena")
    private String humoLena;

    @Column(name = "fuente_agua")
    private String fuenteAgua;

    @Column(name = "fuente_agua_otra")
    private String fuenteAguaOtra;

    @Column(name = "tratamiento_agua")
    private String tratamientoAgua;

    @Column(name = "cod_part")
    private String codPart;

    // Relación con participante
    @OneToOne
    @JoinColumn(name = "cod_part", referencedColumnName = "cod_part", insertable = false, updatable = false)
    private Participantecrf participante;

    public Factor() {}

    // Getters y setters
    public int getIdFac() { return idFac; }
    public void setIdFac(int idFac) {
        this.idFac = idFac;
    }

    public String getCarnes() {
        return carnes;
    }
    public void setCarnes(String carnes) {
        this.carnes = carnes;
    }

    public String getSalados() {
        return salados;
    }
    public void setSalados(String salados) {
        this.salados = salados;
    }

    public String getFrutas() {
        return frutas;
    }
    public void setFrutas(String frutas) {
        this.frutas = frutas;
    }

    public String getFrituras() {
        return frituras;
    }
    public void setFrituras(String frituras) {
        this.frituras = frituras;
    }

    public String getCondimentados() {
        return condimentados;
    }
    public void setCondimentados(String condimentados) {
        this.condimentados = condimentados;
    }

    public String getBebidasCalientes() {
        return bebidasCalientes;
    }
    public void setBebidasCalientes(String bebidasCalientes) {
        this.bebidasCalientes = bebidasCalientes;
    }

    public String getPesticidas() {
        return pesticidas;
    }
    public void setPesticidas(String pesticidas) {
        this.pesticidas = pesticidas;
    }

    public String getQuimicos() {
        return quimicos;
    }
    public void setQuimicos(String quimicos) {
        this.quimicos = quimicos;
    }

    public String getDetalleQuimicos() {
        return detalleQuimicos;
    }
    public void setDetalleQuimicos(String detalleQuimicos) {
        this.detalleQuimicos = detalleQuimicos;
    }

    public String getHumoLena() {
        return humoLena;
    }
    public void setHumoLena(String humoLena) {
        this.humoLena = humoLena;
    }

    public String getFuenteAgua() {
        return fuenteAgua;
    }
    public void setFuenteAgua(String fuenteAgua) {
        this.fuenteAgua = fuenteAgua;
    }

    public String getFuenteAguaOtra() {
        return fuenteAguaOtra;
    }
    public void setFuenteAguaOtra(String fuenteAguaOtra) {
        this.fuenteAguaOtra = fuenteAguaOtra;
    }

    public String getTratamientoAgua() {
        return tratamientoAgua;
    }
    public void setTratamientoAgua(String tratamientoAgua) {
        this.tratamientoAgua = tratamientoAgua;
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
