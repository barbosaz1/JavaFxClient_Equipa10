
package gestaoeventos.dto;

import gestaoeventos.entity.TipoNotificacao;

import java.time.LocalDateTime;

public class NotificacaoDTO {

    private Integer id;
    private Integer destinatarioNumero;
    private Integer eventoId;
    private TipoNotificacao tipo;
    private String conteudo;
    private String canal;
    private boolean lida;
    private LocalDateTime dataCriacao;

    public NotificacaoDTO() {}

    // getters e setters ...
    
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getDestinatarioNumero() {
		return destinatarioNumero;
	}

	public void setDestinatarioNumero(Integer destinatarioNumero) {
		this.destinatarioNumero = destinatarioNumero;
	}

	public Integer getEventoId() {
		return eventoId;
	}

	public void setEventoId(Integer eventoId) {
		this.eventoId = eventoId;
	}

	public TipoNotificacao getTipo() {
		return tipo;
	}

	public void setTipo(TipoNotificacao tipo) {
		this.tipo = tipo;
	}

	public String getConteudo() {
		return conteudo;
	}

	public void setConteudo(String conteudo) {
		this.conteudo = conteudo;
	}

	public String getCanal() {
		return canal;
	}

	public void setCanal(String canal) {
		this.canal = canal;
	}

	public boolean isLida() {
		return lida;
	}

	public void setLida(boolean lida) {
		this.lida = lida;
	}

	public LocalDateTime getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(LocalDateTime dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

    
}
