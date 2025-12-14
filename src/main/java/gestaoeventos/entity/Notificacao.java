package gestaoeventos.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notificacao")
public class Notificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "destinatario_numero", nullable = false)
    private Utilizador destinatario;

    @ManyToOne
    @JoinColumn(name = "evento_id")
    private Evento evento;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 40)
    private TipoNotificacao tipo;

    @Column(name = "conteudo", nullable = false, length = 500)
    private String conteudo;

    @Column(name = "canal", length = 40)
    private String canal;

    @Column(name = "lida", nullable = false)
    private boolean lida = false;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @Column(name = "data_inicio_exibicao")
    private LocalDateTime dataInicioExibicao;

    @Column(name = "data_fim_exibicao")
    private LocalDateTime dataFimExibicao;

    public Notificacao() {
    }

    // GETTERS & SETTERS

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Utilizador getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(Utilizador destinatario) {
        this.destinatario = destinatario;
    }

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
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

    public LocalDateTime getDataInicioExibicao() {
        return dataInicioExibicao;
    }

    public void setDataInicioExibicao(LocalDateTime dataInicioExibicao) {
        this.dataInicioExibicao = dataInicioExibicao;
    }

    public LocalDateTime getDataFimExibicao() {
        return dataFimExibicao;
    }

    public void setDataFimExibicao(LocalDateTime dataFimExibicao) {
        this.dataFimExibicao = dataFimExibicao;
    }

    public boolean isVisivelAgora() {
        LocalDateTime agora = LocalDateTime.now();
        if (dataInicioExibicao != null && agora.isBefore(dataInicioExibicao)) {
            return false;
        }
        if (dataFimExibicao != null && agora.isAfter(dataFimExibicao)) {
            return false;
        }
        return true;
    }
}
