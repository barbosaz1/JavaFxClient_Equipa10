package gestaoeventos.client.controller;

import gestaoeventos.client.model.UserSession;
import gestaoeventos.client.service.EventoService;
import gestaoeventos.client.service.InscricaoService;
import gestaoeventos.client.service.NotificacaoClientService;
import gestaoeventos.dto.EventoDTO;
import gestaoeventos.dto.InscricaoDTO;
import gestaoeventos.dto.NotificacaoDTO;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class OverviewController implements Initializable {

    @FXML
    private Label lblTotalEventos;
    @FXML
    private Label lblInscricoes;
    @FXML
    private Label lblWelcome;
    @FXML
    private VBox vboxAnuncios;
    @FXML
    private VBox containerAnuncios;
    @FXML
    private Label lblCarregandoAnuncios;

    private final EventoService eventoService = new EventoService();
    private final InscricaoService inscricaoService = new InscricaoService();
    private final NotificacaoClientService notificacaoService = new NotificacaoClientService();

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (UserSession.getInstance().isLoggedIn()) {
            String nome = UserSession.getInstance().getUser().getNome();
            String primeiroNome = nome.contains(" ") ? nome.split(" ")[0] : nome;
            lblWelcome.setText("Ola, " + primeiroNome);
        }

        carregarMetricas();
        carregarAnuncios();
    }

    private void carregarMetricas() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                List<EventoDTO> eventos = eventoService.listarTodos();
                long proximos = eventos.stream()
                        .filter(e -> "PUBLICADO".equals(e.getEstado().toString()))
                        .filter(e -> e.getDataInicio() != null && e.getDataInicio().isAfter(LocalDateTime.now()))
                        .count();

                Integer userId = UserSession.getInstance().getUser().getNumero();
                List<InscricaoDTO> inscricoes = inscricaoService.listarPorUtilizador(userId);
                long ativas = inscricoes.stream()
                        .filter(i -> "ATIVA".equals(i.getEstado().toString()))
                        .count();

                Platform.runLater(() -> {
                    lblTotalEventos.setText(String.valueOf(proximos));
                    lblInscricoes.setText(String.valueOf(ativas));
                });
                return null;
            }
        };

        task.setOnFailed(e -> {
            System.err.println("Erro ao carregar métricas: " + task.getException().getMessage());
            lblTotalEventos.setText("-");
            lblInscricoes.setText("-");
        });

        new Thread(task).start();
    }

    private void carregarAnuncios() {
        Task<List<NotificacaoDTO>> task = new Task<>() {
            @Override
            protected List<NotificacaoDTO> call() throws Exception {
                return notificacaoService.listarAnunciosVisiveis();
            }
        };

        task.setOnSucceeded(e -> {
            List<NotificacaoDTO> anuncios = task.getValue();
            containerAnuncios.getChildren().clear();

            if (anuncios.isEmpty()) {
                Label lblVazio = new Label("Nao existem anuncios de momento.");
                lblVazio.setStyle("-fx-text-fill: rgba(255,255,255,0.7);");
                containerAnuncios.getChildren().add(lblVazio);
                return;
            }

            for (NotificacaoDTO anuncio : anuncios) {
                VBox cardAnuncio = criarCardAnuncio(anuncio);
                containerAnuncios.getChildren().add(cardAnuncio);
            }
        });

        task.setOnFailed(e -> {
            if (lblCarregandoAnuncios != null) {
                lblCarregandoAnuncios.setText("Erro ao carregar anúncios.");
            }
        });

        new Thread(task).start();
    }

    private VBox criarCardAnuncio(NotificacaoDTO anuncio) {
        VBox card = new VBox(5);
        card.setStyle("-fx-background-color: rgba(255,255,255,0.15); -fx-background-radius: 8; -fx-padding: 12;");

        Label lblConteudo = new Label(anuncio.getConteudo());
        lblConteudo.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        lblConteudo.setWrapText(true);

        StringBuilder info = new StringBuilder();
        if (anuncio.getDataCriacao() != null) {
            info.append("Publicado: ").append(anuncio.getDataCriacao().format(DTF));
        }
        if (anuncio.getDataFimExibicao() != null) {
            info.append(" | Valido ate: ").append(anuncio.getDataFimExibicao().format(DTF));
        }

        Label lblInfo = new Label(info.toString());
        lblInfo.setStyle("-fx-text-fill: rgba(255,255,255,0.5); -fx-font-size: 11px;");

        card.getChildren().add(lblConteudo);
        if (info.length() > 0) {
            card.getChildren().add(lblInfo);
        }

        return card;
    }
}