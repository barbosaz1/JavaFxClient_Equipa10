package gestaoeventos.client.controller;

import gestaoeventos.client.model.UserSession;
import gestaoeventos.client.service.InscricaoService;
import gestaoeventos.dto.InscricaoDTO;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class InscricoesController implements Initializable {

    @FXML
    private TableView<InscricaoDTO> tabelaInscricoes;
    @FXML
    private TableColumn<InscricaoDTO, String> colEvento, colData, colEstado, colCheckIn, colToken;
    @FXML
    private Button btnCancelar;
    @FXML
    private Label lblStatus;

    private final InscricaoService inscricaoService = new InscricaoService();
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        carregarInscricoes();

        tabelaInscricoes.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            // So ativa cancelar se a inscricao estiver ATIVA ou em LISTA_ESPERA
            boolean podeCancelar = newVal != null &&
                    ("ATIVA".equals(newVal.getEstado().toString())
                            || "LISTA_ESPERA".equals(newVal.getEstado().toString()));
            btnCancelar.setDisable(!podeCancelar);
        });
    }

    private void setupTable() {
        // Mostrar nome do evento em vez de "Evento #ID"
        colEvento.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getEventoTitulo() != null ? cell.getValue().getEventoTitulo()
                        : "Evento #" + cell.getValue().getEventoId()));

        colData.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getDataInscricao() != null ? cell.getValue().getDataInscricao().format(dtf) : ""));

        colEstado.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getEstado() != null ? cell.getValue().getEstado().toString() : ""));

        colCheckIn.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().isCheckIn() ? "Sim" : "Nao"));

        // Mostrar token apenas para inscricoes ativas sem check-in
        if (colToken != null) {
            colToken.setCellValueFactory(cell -> {
                InscricaoDTO dto = cell.getValue();
                if (dto.getQrCodeToken() != null && !dto.getQrCodeToken().isEmpty()) {
                    return new SimpleStringProperty(dto.getQrCodeToken());
                } else if (dto.isCheckIn()) {
                    return new SimpleStringProperty("Check-in feito");
                } else if ("ATIVA".equals(dto.getEstado().toString())) {
                    return new SimpleStringProperty("A gerar...");
                } else {
                    return new SimpleStringProperty("-");
                }
            });
        }
    }

    @FXML
    void carregarInscricoes() {
        Integer userId = UserSession.getInstance().getUser().getNumero();
        Task<List<InscricaoDTO>> task = new Task<>() {
            @Override
            protected List<InscricaoDTO> call() throws Exception {
                return inscricaoService.listarPorUtilizador(userId);
            }
        };

        task.setOnSucceeded(e -> tabelaInscricoes.getItems().setAll(task.getValue()));
        task.setOnFailed(e -> showFeedback("Erro ao carregar dados.", true));
        new Thread(task).start();
    }

    @FXML
    void handleCancelar() {
        InscricaoDTO dto = tabelaInscricoes.getSelectionModel().getSelectedItem();
        if (dto == null)
            return;

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                inscricaoService.cancelar(dto.getId());
                return null;
            }
        };

        task.setOnSucceeded(e -> {
            showFeedback("Inscricao cancelada.", false);
            carregarInscricoes();
        });

        task.setOnFailed(e -> showFeedback("Erro ao cancelar: " + task.getException().getMessage(), true));
        new Thread(task).start();
    }

    private void showFeedback(String msg, boolean isError) {
        lblStatus.setText(msg);
        lblStatus.setStyle(isError ? "-fx-text-fill: #ef4444;" : "-fx-text-fill: #22c55e;");
    }
}