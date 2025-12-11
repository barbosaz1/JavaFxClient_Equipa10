package gestaoeventos.client.controller;

import gestaoeventos.client.model.UserSession;
import gestaoeventos.client.service.EventoService;
import gestaoeventos.dto.EventoDTO;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class EventosController implements Initializable {

    @FXML private TableView<EventoDTO> tabelaEventos;
    @FXML private TableColumn<EventoDTO, String> colTitulo, colData, colLocal, colVagas, colEstado;
    @FXML private Button btnInscrever;
    @FXML private Label lblStatus; // Label para feedback (Toasts)

    private final EventoService eventoService = new EventoService();
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        carregarEventos();

        tabelaEventos.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> {
                btnInscrever.setDisable(newVal == null);
            }
        );
    }

    private void setupTable() {
        colTitulo.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTitulo()));
        
        // Formatar data
        colData.setCellValueFactory(cell -> {
            if(cell.getValue().getDataInicio() != null) 
                return new SimpleStringProperty(cell.getValue().getDataInicio().format(dtf));
            return new SimpleStringProperty("-");
        });
        
        // Tratar Local ID (Simplificado, idealmente seria o nome)
        colLocal.setCellValueFactory(cell -> new SimpleStringProperty("Local #" + cell.getValue().getLocalId()));
        
        colVagas.setCellValueFactory(cell -> new SimpleStringProperty(String.valueOf(cell.getValue().getMaxParticipantes())));
        colEstado.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getEstado().toString()));
    }

    @FXML
    void carregarEventos() {
        btnInscrever.setDisable(true);
        
        Task<List<EventoDTO>> task = new Task<>() {
            @Override
            protected List<EventoDTO> call() throws Exception {
                // Filtra apenas eventos PUBLICADOS para o participante
                return eventoService.listarTodos().stream()
                        .filter(e -> "PUBLICADO".equals(e.getEstado().toString()))
                        .collect(Collectors.toList());
            }
        };

        task.setOnSucceeded(e -> tabelaEventos.getItems().setAll(task.getValue()));
        task.setOnFailed(e -> showFeedback("Erro ao carregar eventos.", true));
        
        new Thread(task).start();
    }

    @FXML
    void handleInscricao() {
        EventoDTO evento = tabelaEventos.getSelectionModel().getSelectedItem();
        if (evento == null) return;

        Integer userId = UserSession.getInstance().getUser().getNumero();
        btnInscrever.setDisable(true);
        btnInscrever.setText("A processar...");

        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                return eventoService.inscrever(evento.getId(), userId);
            }
        };

        task.setOnSucceeded(e -> {
            String resultado = task.getValue();
            if ("EVENTO_LOTADO_LISTA_ESPERA".equals(resultado)) {
                showFeedback("Evento cheio. Entrou na Lista de Espera.", false);
            } else {
                showFeedback("Inscrição realizada com sucesso!", false);
            }
            resetButton();
        });

        task.setOnFailed(e -> {
            showFeedback("Erro: " + task.getException().getMessage(), true);
            resetButton();
        });

        new Thread(task).start();
    }

    private void resetButton() {
        btnInscrever.setDisable(false);
        btnInscrever.setText("Inscrever-se");
    }

    private void showFeedback(String msg, boolean isError) {
        lblStatus.setText(msg);
        lblStatus.setStyle(isError ? "-fx-text-fill: #ef4444;" : "-fx-text-fill: #22c55e;");
        // Limpar após 3s
        new Thread(() -> {
            try { Thread.sleep(3000); } catch (Exception e) {}
            Platform.runLater(() -> lblStatus.setText(""));
        }).start();
    }
}