package gestaoeventos.client.controller;

import gestaoeventos.client.model.UserSession;
import gestaoeventos.client.service.InscricaoService;
import gestaoeventos.dto.InscricaoDTO;
import javafx.application.Platform;
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

    @FXML private TableView<InscricaoDTO> tabelaInscricoes;
    @FXML private TableColumn<InscricaoDTO, String> colEvento, colData, colEstado, colCheckIn;
    @FXML private Button btnCancelar;
    @FXML private Label lblStatus;

    private final InscricaoService inscricaoService = new InscricaoService();
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        carregarInscricoes();

        tabelaInscricoes.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            // Só ativa cancelar se a inscrição estiver ATIVA ou em LISTA_ESPERA
            boolean podeCancelar = newVal != null && 
                ("ATIVA".equals(newVal.getEstado().toString()) || "LISTA_ESPERA".equals(newVal.getEstado().toString()));
            btnCancelar.setDisable(!podeCancelar);
        });
    }

    private void setupTable() {
        colEvento.setCellValueFactory(cell -> new SimpleStringProperty("Evento #" + cell.getValue().getEventoId()));
        colData.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDataInscricao().format(dtf)));
        colEstado.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getEstado().toString()));
        colCheckIn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().isCheckIn() ? "Sim" : "Não"));
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
        if (dto == null) return;

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                // Assumindo que criaste o método cancelar no InscricaoService
                 inscricaoService.cancelar(dto.getId()); 
                 return null;
            }
        };

        task.setOnSucceeded(e -> {
            showFeedback("Inscrição cancelada.", false);
            carregarInscricoes(); // Refresh
        });
        
        task.setOnFailed(e -> showFeedback("Erro ao cancelar: " + task.getException().getMessage(), true));
        new Thread(task).start();
    }
    
    private void showFeedback(String msg, boolean isError) {
        lblStatus.setText(msg);
        lblStatus.setStyle(isError ? "-fx-text-fill: #ef4444;" : "-fx-text-fill: #22c55e;");
    }
}