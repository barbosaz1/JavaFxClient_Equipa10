package gestaoeventos.dto;

import gestaoeventos.entity.EstadoInscricao;

import java.time.LocalDateTime;

public class InscricaoDTO {

	private Integer id;
	private Integer eventoId;
	private String eventoTitulo;
	private Integer utilizadorNumero;
	private LocalDateTime dataInscricao;
	private EstadoInscricao estado;
	private boolean checkIn;
	private LocalDateTime dataCheckin;
	private String qrCodeToken;

	public InscricaoDTO() {
	}

	// getters e setters ...

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public LocalDateTime getDataInscricao() {
		return dataInscricao;
	}

	public void setDataInscricao(LocalDateTime dataInscricao) {
		this.dataInscricao = dataInscricao;
	}

	public EstadoInscricao getEstado() {
		return estado;
	}

	public void setEstado(EstadoInscricao estado) {
		this.estado = estado;
	}

	public boolean isCheckIn() {
		return checkIn;
	}

	public void setCheckIn(boolean checkIn) {
		this.checkIn = checkIn;
	}

	public LocalDateTime getDataCheckin() {
		return dataCheckin;
	}

	public void setDataCheckin(LocalDateTime dataCheckin) {
		this.dataCheckin = dataCheckin;
	}

	public String getQrCodeToken() {
		return qrCodeToken;
	}

	public void setQrCodeToken(String qrCodeToken) {
		this.qrCodeToken = qrCodeToken;
	}

}
