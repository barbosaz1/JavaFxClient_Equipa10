package gestaoeventos.client.controller;

import gestaoeventos.client.model.UserSession;
import gestaoeventos.client.service.EventoService;
import gestaoeventos.client.service.InscricaoService;
import gestaoeventos.dto.EventoDTO;
import gestaoeventos.dto.InscricaoDTO;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class DetalhesEventoController {

    @FXML
    private Label lblTitulo, lblTipo, lblEstado, lblData, lblLocal, lblVagas, lblDescricao, lblFeedback;
    @FXML
    private Button btnInscrever, btnCancelar;

    private EventoDTO evento;
    private final EventoService eventoService = new EventoService();
    private final InscricaoService inscricaoService = new InscricaoService();
    private Runnable onUpdateCallback;

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

        lblLocal.setText("Local ID: " + evento.getLocalId());
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
                        .anyMatch(i -> i.getEventoId().equals(evento.getId())
                                && "ATIVA".equals(i.getEstado().toString()));
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

                // Se evento não está publicado, bloquear inscrição
                if (!"PUBLICADO".equals(evento.getEstado().toString())) {
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
        Task<gestaoeventos.client.model.InscricaoResultado> task = new Task<>() {
            @Override
            protected gestaoeventos.client.model.InscricaoResultado call() throws Exception {
                return eventoService.inscrever(evento.getId(), UserSession.getInstance().getUser().getNumero());
            }
        };

        task.setOnSucceeded(e -> {
            gestaoeventos.client.model.InscricaoResultado resultado = task.getValue();
            if (resultado.isSucesso()) {
                lblFeedback.setText("✅ Inscrição realizada! QR code gerado para check-in.");
                lblFeedback.setStyle("-fx-text-fill: #22c55e;");
                // Mostrar diálogo com o QR code
                mostrarQrCode(resultado);
            } else if (resultado.isListaEspera()) {
                lblFeedback.setText("⚠️ Evento lotado. Adicionado à lista de espera.");
                lblFeedback.setStyle("-fx-text-fill: #f59e0b;");
            } else {
                lblFeedback.setText("❌ " + resultado.getResultado());
                lblFeedback.setStyle("-fx-text-fill: #ef4444;");
            }
            verificarInscricao();
            if (onUpdateCallback != null)
                onUpdateCallback.run();
        });

        task.setOnFailed(e -> {
            lblFeedback.setText("❌ " + e.getSource().getException().getMessage());
            lblFeedback.setStyle("-fx-text-fill: #ef4444;");
            btnInscrever.setDisable(false);
        });
        new Thread(task).start();
    }

    private void mostrarQrCode(gestaoeventos.client.model.InscricaoResultado resultado) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("QR Code para Check-in");
        alert.setHeaderText("Guarda este QR code para fazer check-in no evento");

        javafx.scene.layout.VBox content = new javafx.scene.layout.VBox(10);
        content.setAlignment(javafx.geometry.Pos.CENTER);

        // Carregar imagem do QR code
        if (resultado.getQrCodeUrl() != null) {
            javafx.scene.image.ImageView qrImage = new javafx.scene.image.ImageView(
                    new javafx.scene.image.Image(resultado.getQrCodeUrl(), 200, 200, true, true));
            content.getChildren().add(qrImage);
        }

        javafx.scene.control.Label tokenLabel = new javafx.scene.control.Label("Token: " + resultado.getQrCodeToken());
        tokenLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #6b7280;");
        content.getChildren().add(tokenLabel);

        alert.getDialogPane().setContent(content);
        alert.showAndWait();
    }

    @FXML
    void cancelar() {
        // Cancelamento feito através do menu 'Minhas Inscrições'
        lblFeedback.setText("⚠️ Cancele através do menu 'Minhas Inscrições'.");
        lblFeedback.setStyle("-fx-text-fill: orange;");
    }

    @FXML
    void fechar() {
        ((Stage) lblTitulo.getScene().getWindow()).close();
    }
}