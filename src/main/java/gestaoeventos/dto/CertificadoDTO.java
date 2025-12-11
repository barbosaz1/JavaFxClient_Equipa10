package gestaoeventos.dto;

import gestaoeventos.entity.TipoCertificado;
import java.time.LocalDateTime;

/**
 * 
 * Usado para enviar/receber dados de certificados entre o cliente e o servidor
 * 
 */
public class CertificadoDTO {

    /** Identificador único do certificado */
    private Integer id;

    /** ID da inscrição associada */
    private Integer inscricaoId;

    /** ID do evento onde o certificado foi emitido */
    private Integer eventoId;

    /** Título do evento */
    private String eventoTitulo;

    /** Número do utilizador que recebeu o certificado */
    private Integer utilizadorNumero;

    /** Nome do utilizador que recebeu o certificado */
    private String utilizadorNome;

    /** Data e hora de emissão */
    private LocalDateTime dataEmissao;

    /** Código de verificação */
    private String codigoVerificacao;

    /** Número do utilizador que emitiu o certificado */
    private Integer emitidoPorNumero;

    /** Nome do utilizador que emitiu o certificado */
    private String emitidoPorNome;

    /** Tipo de certificado */
    private TipoCertificado tipo;

    /** Descriçãodo tipo de certificado */
    private String tipoDescricao;

    /** Nível de autoridade */
    private Integer nivelAutoridade;

    public CertificadoDTO() {
    }

    public CertificadoDTO(Integer id, Integer inscricaoId, Integer eventoId, String eventoTitulo,
            Integer utilizadorNumero, String utilizadorNome, LocalDateTime dataEmissao,
            String codigoVerificacao, Integer emitidoPorNumero, TipoCertificado tipo) {
        this.id = id;
        this.inscricaoId = inscricaoId;
        this.eventoId = eventoId;
        this.eventoTitulo = eventoTitulo;
        this.utilizadorNumero = utilizadorNumero;
        this.utilizadorNome = utilizadorNome;
        this.dataEmissao = dataEmissao;
        this.codigoVerificacao = codigoVerificacao;
        this.emitidoPorNumero = emitidoPorNumero;
        this.tipo = tipo;
        if (tipo != null) {
            this.tipoDescricao = tipo.getDescricao();
            this.nivelAutoridade = tipo.getNivelAutoridade();
        }
    }

    // ==================== GETTERS & SETTERS ====================

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getInscricaoId() {
        return inscricaoId;
    }

    public void setInscricaoId(Integer inscricaoId) {
        this.inscricaoId = inscricaoId;
    }

    public Integer getEventoId() {
        return eventoId;
    }

    public void setEventoId(Integer eventoId) {
        this.eventoId = eventoId;
    }

    public String getEventoTitulo() {
        return eventoTitulo;
    }

    public void setEventoTitulo(String eventoTitulo) {
        this.eventoTitulo = eventoTitulo;
    }

    public Integer getUtilizadorNumero() {
        return utilizadorNumero;
    }

    public void setUtilizadorNumero(Integer utilizadorNumero) {
        this.utilizadorNumero = utilizadorNumero;
    }

    public String getUtilizadorNome() {
        return utilizadorNome;
    }

    public void setUtilizadorNome(String utilizadorNome) {
        this.utilizadorNome = utilizadorNome;
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

    public String getEmitidoPorNome() {
        return emitidoPorNome;
    }

    public void setEmitidoPorNome(String emitidoPorNome) {
        this.emitidoPorNome = emitidoPorNome;
    }

    public TipoCertificado getTipo() {
        return tipo;
    }

    public void setTipo(TipoCertificado tipo) {
        this.tipo = tipo;
        if (tipo != null) {
            this.tipoDescricao = tipo.getDescricao();
            this.nivelAutoridade = tipo.getNivelAutoridade();
        }
    }

    public String getTipoDescricao() {
        return tipoDescricao;
    }

    public void setTipoDescricao(String tipoDescricao) {
        this.tipoDescricao = tipoDescricao;
    }

    public Integer getNivelAutoridade() {
        return nivelAutoridade;
    }

    public void setNivelAutoridade(Integer nivelAutoridade) {
        this.nivelAutoridade = nivelAutoridade;
    }
}
