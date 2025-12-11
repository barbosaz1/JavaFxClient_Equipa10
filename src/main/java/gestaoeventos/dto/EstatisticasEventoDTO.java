package gestaoeventos.dto;

public class EstatisticasEventoDTO {
    private Integer eventoId;
    private String eventoTitulo;
    private Integer totalInscricoes;
    private Integer inscricoesAtivas;
    private Integer inscricoesCanceladas;
    private Integer checkInsRealizados;
    private Integer maxParticipantes;
    private Integer vagasDisponiveis;
    private Double percentualOcupacao;
    private Integer certificadosEmitidos;

    public EstatisticasEventoDTO() {
    }

    // GETTERS E SETTERS

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

    public Integer getTotalInscricoes() {
        return totalInscricoes;
    }

    public void setTotalInscricoes(Integer totalInscricoes) {
        this.totalInscricoes = totalInscricoes;
    }

    public Integer getInscricoesAtivas() {
        return inscricoesAtivas;
    }

    public void setInscricoesAtivas(Integer inscricoesAtivas) {
        this.inscricoesAtivas = inscricoesAtivas;
    }

    public Integer getInscricoesCanceladas() {
        return inscricoesCanceladas;
    }

    public void setInscricoesCanceladas(Integer inscricoesCanceladas) {
        this.inscricoesCanceladas = inscricoesCanceladas;
    }

    public Integer getCheckInsRealizados() {
        return checkInsRealizados;
    }

    public void setCheckInsRealizados(Integer checkInsRealizados) {
        this.checkInsRealizados = checkInsRealizados;
    }

    public Integer getMaxParticipantes() {
        return maxParticipantes;
    }

    public void setMaxParticipantes(Integer maxParticipantes) {
        this.maxParticipantes = maxParticipantes;
    }

    public Integer getVagasDisponiveis() {
        return vagasDisponiveis;
    }

    public void setVagasDisponiveis(Integer vagasDisponiveis) {
        this.vagasDisponiveis = vagasDisponiveis;
    }

    public Double getPercentualOcupacao() {
        return percentualOcupacao;
    }

    public void setPercentualOcupacao(Double percentualOcupacao) {
        this.percentualOcupacao = percentualOcupacao;
    }

    public Integer getCertificadosEmitidos() {
        return certificadosEmitidos;
    }

    public void setCertificadosEmitidos(Integer certificadosEmitidos) {
        this.certificadosEmitidos = certificadosEmitidos;
    }
}
