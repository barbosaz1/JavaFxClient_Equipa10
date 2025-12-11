package gestaoeventos.dto;

import java.time.LocalDateTime;

public class LogAuditoriaDTO {

    private Integer id;
    private String acao;
    private String entidade;
    private Integer entidadeId;
    private String motivo;
    private String ipOrigem;
    private LocalDateTime dataHora;
    private Integer autorNumero;

    public LogAuditoriaDTO() {}

    // getters e setters ...
    
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getAcao() {
		return acao;
	}

	public void setAcao(String acao) {
		this.acao = acao;
	}

	public String getEntidade() {
		return entidade;
	}

	public void setEntidade(String entidade) {
		this.entidade = entidade;
	}

	public Integer getEntidadeId() {
		return entidadeId;
	}

	public void setEntidadeId(Integer entidadeId) {
		this.entidadeId = entidadeId;
	}

	public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}

	public String getIpOrigem() {
		return ipOrigem;
	}

	public void setIpOrigem(String ipOrigem) {
		this.ipOrigem = ipOrigem;
	}

	public LocalDateTime getDataHora() {
		return dataHora;
	}

	public void setDataHora(LocalDateTime dataHora) {
		this.dataHora = dataHora;
	}

	public Integer getAutorNumero() {
		return autorNumero;
	}

	public void setAutorNumero(Integer autorNumero) {
		this.autorNumero = autorNumero;
	}
    
}

