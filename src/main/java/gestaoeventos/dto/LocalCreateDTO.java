
package gestaoeventos.dto;

public class LocalCreateDTO {

    private String nome;
    private String morada;
    private Integer capacidade;
    private String disponibilidadeHoraria;
    private Boolean ativo;

    public LocalCreateDTO() {}

    // getters e setters ...
    
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getMorada() {
		return morada;
	}

	public void setMorada(String morada) {
		this.morada = morada;
	}

	public Integer getCapacidade() {
		return capacidade;
	}

	public void setCapacidade(Integer capacidade) {
		this.capacidade = capacidade;
	}

	public String getDisponibilidadeHoraria() {
		return disponibilidadeHoraria;
	}

	public void setDisponibilidadeHoraria(String disponibilidadeHoraria) {
		this.disponibilidadeHoraria = disponibilidadeHoraria;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

    
}

