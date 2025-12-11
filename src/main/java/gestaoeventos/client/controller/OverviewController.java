package gestaoeventos.client.controller;

import gestaoeventos.client.model.UserSession;
import gestaoeventos.client.service.EventoService;
import gestaoeventos.client.service.InscricaoService;
import gestaoeventos.dto.EventoDTO;
import gestaoeventos.dto.InscricaoDTO;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;

public class OverviewController implements Initializable {

    @FXML private Label lblTotalEventos;
    @FXML private Label lblInscricoes;
    @FXML private Label lblWelcome;

    private final EventoService eventoService = new EventoService();
    private final InscricaoService inscricaoService = new InscricaoService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Boas vindas personalizada
        if (UserSession.getInstance().isLoggedIn()) {
            String nome = UserSession.getInstance().getUser().getNome();
            // Apenas o primeiro nome
            String primeiroNome = nome.contains(" ") ? nome.split(" ")[0] : nome;
            lblWelcome.setText("Olá, " + primeiroNome + " 👋");
        }

        carregarMétricasReais();
    }

    private void carregarMétricasReais() {
        // Tarefa em background
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                // Calcular Próximos Eventos
                List<EventoDTO> eventos = eventoService.listarTodos();
                long proximos = eventos.stream()
                        .filter(e -> "PUBLICADO".equals(e.getEstado().toString()))
                        .filter(e -> e.getDataInicio() != null && e.getDataInicio().isAfter(LocalDateTime.now()))
                        .count();

                // Calcular Minhas Inscrições Ativas
                Integer userId = UserSession.getInstance().getUser().getNumero();
                List<InscricaoDTO> inscricoes = inscricaoService.listarPorUtilizador(userId);
                long ativas = inscricoes.stream()
                        .filter(i -> "ATIVA".equals(i.getEstado().toString()))
                        .count();

                // Atualizar UI
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
}