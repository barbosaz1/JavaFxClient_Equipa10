package gestaoeventos.dto;

import gestaoeventos.entity.PerfilUtilizador;

public class UtilizadorDTO {

    private Integer numero;
    private String nome;
    private String email;
    private PerfilUtilizador perfil;
    private Boolean ativo;

    public UtilizadorDTO() {
    }

    public UtilizadorDTO(Integer numero, String nome, String email,
                         PerfilUtilizador perfil, boolean ativo) {
        this.numero = numero;
        this.nome = nome;
        this.email = email;
        this.perfil = perfil;
        this.ativo = ativo;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public PerfilUtilizador getPerfil() {
        return perfil;
    }

    public void setPerfil(PerfilUtilizador perfil) {
        this.perfil = perfil;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}
