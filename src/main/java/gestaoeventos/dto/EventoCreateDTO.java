package gestaoeventos.dto;

import gestaoeventos.entity.EstadoEvento;
import gestaoeventos.entity.TipoEvento;

import java.time.LocalDateTime;

public class EventoCreateDTO {

	private String titulo;
	private String descricao;
	private LocalDateTime dataInicio;
	private LocalDateTime dataFim;
	private Integer maxParticipantes;
	private TipoEvento tipo;
	private String areaTematica;
	private Integer criadorNumero;
	private Integer localId;
	private EstadoEvento estado;

	public EventoCreateDTO() {
	}

	// getters e setters ...

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public LocalDateTime getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(LocalDateTime dataInicio) {
		this.dataInicio = dataInicio;
	}

	public LocalDateTime getDataFim() {
		return dataFim;
	}

	public void setDataFim(LocalDateTime dataFim) {
		this.dataFim = dataFim;
	}

	public Integer getMaxParticipantes() {
		return maxParticipantes;
	}

	public void setMaxParticipantes(Integer maxParticipantes) {
		this.maxParticipantes = maxParticipantes;
	}

	public TipoEvento getTipo() {
		return tipo;
	}

	public void setTipo(TipoEvento tipo) {
		this.tipo = tipo;
	}

	public String getAreaTematica() {
		return areaTematica;
	}

	public void setAreaTematica(String areaTematica) {
		this.areaTematica = areaTematica;
	}

	public Integer getCriadorNumero() {
		return criadorNumero;
	}

	public void setCriadorNumero(Integer criadorNumero) {
		this.criadorNumero = criadorNumero;
	}

	public Integer getLocalId() {
		return localId;
	}

	public void setLocalId(Integer localId) {
		this.localId = localId;
	}

	public EstadoEvento getEstado() {
		return estado;
	}

	public void setEstado(EstadoEvento estado) {
		this.estado = estado;
	}

}
