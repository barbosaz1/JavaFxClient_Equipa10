package gestaoeventos.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidade que representa um Certificado emitido no sistema de gestão de
 * eventos.
 * 
 * - PRESENCA: Certificado básico automático
 * - DOCENTE: Certificado emitido por docente (maior valor)
 * - ORGANIZADOR: Certificado emitido pelo organizador
 * 
 */
@Entity
@Table(name = "certificado")
public class Certificado {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @ManyToOne(optional = false)
    @JoinColumn(name = "inscricao_id", nullable = false)
    private Inscricao inscricao;

    @Column(name = "data_emissao", nullable = false)
    private LocalDateTime dataEmissao = LocalDateTime.now();

    @Column(name = "codigo_verificacao", nullable = false, unique = true, length = 64)
    private String codigoVerificacao;

    @Column(name = "emitido_por_numero")
    private Integer emitidoPorNumero;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoCertificado tipo = TipoCertificado.PRESENCA;

    public Certificado() {
    }

    // GETTERS & SETTERS

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Inscricao getInscricao() {
        return inscricao;
    }

    public void setInscricao(Inscricao inscricao) {
        this.inscricao = inscricao;
    }

    public LocalDateTime getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(LocalDateTime dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public String getCodigoVerificacao() {
        return codigoVerificacao;
    }

    public void setCodigoVerificacao(String codigoVerificacao) {
        this.codigoVerificacao = codigoVerificacao;
    }

    public Integer getEmitidoPorNumero() {
        return emitidoPorNumero;
    }

    public void setEmitidoPorNumero(Integer emitidoPorNumero) {
        this.emitidoPorNumero = emitidoPorNumero;
    }

    public TipoCertificado getTipo() {
        return tipo;
    }

    public void setTipo(TipoCertificado tipo) {
        this.tipo = tipo;
    }
}
