package gestaoeventos.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "utilizador",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_utilizador_email",
                columnNames = "email"
        )
)

public class Utilizador {
    @Id
    @Column(name = "numero", nullable = false)
    private Integer numero;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Column(name = "email", nullable = false, length = 150)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "perfil", nullable = false, length = 20)
    private PerfilUtilizador perfil;

    @Column(name = "data_registo", nullable = false)
    private LocalDateTime dataRegisto = LocalDateTime.now();

    @Column(name = "ativo", nullable = false)
    private boolean ativo = true;
    
    //relacoes
    
    @JsonIgnore
    @OneToMany(mappedBy = "criador")
    private Set<Evento> eventosCriados = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "utilizador")
    private Set<Inscricao> inscricoes = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "utilizador")
    private Set<ListaEspera> entradasListaEspera = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "destinatario", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Notificacao> notificacoes = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "autor")
    private Set<LogAuditoria> logsCriados = new HashSet<>();
    
    // GETTERS E SETTERS

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
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

    public LocalDateTime getDataRegisto() {
        return dataRegisto;
    }

    public void setDataRegisto(LocalDateTime dataRegisto) {
        this.dataRegisto = dataRegisto;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public Set<Evento> getEventosCriados() {
        return eventosCriados;
    }

    public void setEventosCriados(Set<Evento> eventosCriados) {
        this.eventosCriados = eventosCriados;
    }

    public Set<Inscricao> getInscricoes() {
        return inscricoes;
    }

    public void setInscricoes(Set<Inscricao> inscricoes) {
        this.inscricoes = inscricoes;
    }

    public Set<ListaEspera> getEntradasListaEspera() {
        return entradasListaEspera;
    }

    public void setEntradasListaEspera(Set<ListaEspera> entradasListaEspera) {
        this.entradasListaEspera = entradasListaEspera;
    }

    public Set<Notificacao> getNotificacoes() {
        return notificacoes;
    }

    public void setNotificacoes(Set<Notificacao> notificacoes) {
        this.notificacoes = notificacoes;
    }

    public Set<LogAuditoria> getLogsCriados() {
        return logsCriados;
    }

    public void setLogsCriados(Set<LogAuditoria> logsCriados) {
        this.logsCriados = logsCriados;
    }
    
}
