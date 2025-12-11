package gestaoeventos.client.controller;

import gestaoeventos.client.model.UserSession;
import gestaoeventos.client.service.EventoService;
import gestaoeventos.client.service.InscricaoService;
import gestaoeventos.dto.EventoDTO;
import gestaoeventos.dto.InscricaoDTO;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class DetalhesEventoController {

    @FXML private Label lblTitulo, lblTipo, lblEstado, lblData, lblLocal, lblVagas, lblDescricao, lblFeedback;
    @FXML private Button btnInscrever, btnCancelar;

    private EventoDTO evento;
    private final EventoService eventoService = new EventoService();
    private final InscricaoService inscricaoService = new InscricaoService();
    private Runnable onUpdateCallback; // Para atualizar o calendário ao fechar

    public void setEvento(EventoDTO evento, Runnable onUpdateCallback) {
        this.evento = evento;
        this.onUpdateCallback = onUpdateCallback;
        preencherDados();
        verificarInscricao();
    }

    private void preencherDados() {
        lblTitulo.setText(evento.getTitulo());
        lblDescricao.setText(evento.getDescricao() != null ? evento.getDescricao() : "Sem descrição.");
        lblTipo.setText(evento.getTipo() != null ? evento.getTipo().toString() : "EVENTO");
        lblEstado.setText(evento.getEstado().toString());
        
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        lblData.setText(evento.getDataInicio().format(dtf) + " até " + evento.getDataFim().format(dtf));
        
        lblLocal.setText("Local ID: " + evento.getLocalId()); // Idealmente buscar nome do local
        lblVagas.setText("Máx: " + evento.getMaxParticipantes());

        // CSS Badges
        lblEstado.getStyleClass().removeAll("badge-publicado", "badge-cancelado", "badge-rascunho");
        switch (evento.getEstado()) {
            case PUBLICADO -> lblEstado.getStyleClass().add("badge-publicado");
            case CANCELADO -> lblEstado.getStyleClass().add("badge-cancelado");
            default -> lblEstado.getStyleClass().add("badge-rascunho");
        }
    }

    private void verificarInscricao() {
        Integer userId = UserSession.getInstance().getUser().getNumero();
        btnInscrever.setDisable(true);
        btnCancelar.setManaged(false);
        btnCancelar.setVisible(false);

        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                // Verifica se o user já está inscrito neste evento
                List<InscricaoDTO> inscricoes = inscricaoService.listarPorUtilizador(userId);
                return inscricoes.stream()
                        .anyMatch(i -> i.getEventoId().equals(evento.getId()) && "ATIVA".equals(i.getEstado().toString()));
            }
        };

        task.setOnSucceeded(e -> {
            boolean inscrito = task.getValue();
            if (inscrito) {
                btnInscrever.setManaged(false);
                btnInscrever.setVisible(false);
                btnCancelar.setManaged(true);
                btnCancelar.setVisible(true);
            } else {
                btnInscrever.setManaged(true);
                btnInscrever.setVisible(true);
                btnInscrever.setDisable(false);
                btnCancelar.setManaged(false);
                btnCancelar.setVisible(false);
                
                //Se cancelado ou passado, não pode inscrever
                if(!"PUBLICADO".equals(evento.getEstado().toString())) {
                    btnInscrever.setDisable(true);
                    lblFeedback.setText("Evento não aceita inscrições.");
                }
            }
        });
        new Thread(task).start();
    }

    @FXML
    void inscrever() {
        btnInscrever.setDisable(true);
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                return eventoService.inscrever(evento.getId(), UserSession.getInstance().getUser().getNumero());
            }
        };
        
        task.setOnSucceeded(e -> {
            lblFeedback.setText("✅ " + task.getValue());
            lblFeedback.setStyle("-fx-text-fill: #22c55e;");
            verificarInscricao();
            if(onUpdateCallback != null) onUpdateCallback.run();
        });
        
        task.setOnFailed(e -> {
            lblFeedback.setText("❌ " + e.getSource().getException().getMessage());
            lblFeedback.setStyle("-fx-text-fill: #ef4444;");
            btnInscrever.setDisable(false);
        });
        new Thread(task).start();
    }

    @FXML
    void cancelar() {
        // para cancelar precisa do ID da inscrição.
        // Como aqui só temos evento e user, teríamos de procurar o ID da inscrição primeiro.
        // Para simplificar, vou assumir que o fluxo de cancelamento principal é na view.
        lblFeedback.setText("⚠️ Cancele através do menu 'Minhas Inscrições'.");
        lblFeedback.setStyle("-fx-text-fill: orange;");
    }

    @FXML void fechar() {
        ((Stage) lblTitulo.getScene().getWindow()).close();
    }
}