package gestaoeventos.client.controller;

import gestaoeventos.client.model.UserSession;
import gestaoeventos.client.service.EventoService;
import gestaoeventos.client.service.InscricaoService;
import gestaoeventos.client.util.PageNavigator;
import gestaoeventos.dto.EventoDTO;
import gestaoeventos.dto.InscricaoDTO;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class CalendarioController implements Initializable {

    @FXML private Label lblMesAno;
    @FXML private GridPane gridCalendario;
    @FXML private Label lblDiaSelecionado;
    @FXML private ListView<EventoDTO> listaEventosDia;
    @FXML private Button btnAcaoEvento;

    private YearMonth currentYearMonth;
    private final EventoService eventoService = new EventoService();
    private final InscricaoService inscricaoService = new InscricaoService();
    
    // Cache de dados
    private List<EventoDTO> todosEventos = new ArrayList<>();
    private Set<Integer> meusEventosIds = new HashSet<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currentYearMonth = YearMonth.now();
        
        // --- OTIMIZAÇÃO: Configurar a CellFactory apenas uma vez ---
        listaEventosDia.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(EventoDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                } else {
                    // Formatar Hora e Título
                    String hora = (item.getDataInicio() != null) 
                        ? item.getDataInicio().format(DateTimeFormatter.ofPattern("HH:mm")) 
                        : "--:--";
                        
                    String titulo = (item.getTitulo() != null) ? item.getTitulo() : "Sem Título";
                    
                    // Verificar se está inscrito
                    String status = meusEventosIds.contains(item.getId()) ? " (Inscrito ✅)" : "";
                    
                    setText(hora + " - " + titulo + status);
                    
                    // Estilo do texto (garante contraste no tema dark)
                    getStyleClass().add("filled"); 
                }
            }
        });

        // Carregar dados iniciais
        carregarDadosEAtualizar();
    }

    private void carregarDadosEAtualizar() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                // 1. Buscar todos os eventos
                todosEventos = eventoService.listarTodos();
                
                // 2. Buscar minhas inscrições para destacar no calendário
                Integer userId = UserSession.getInstance().getUser().getNumero();
                List<InscricaoDTO> inscricoes = inscricaoService.listarPorUtilizador(userId);
                
                meusEventosIds = inscricoes.stream()
                        .filter(i -> "ATIVA".equals(i.getEstado().toString()))
                        .map(InscricaoDTO::getEventoId)
                        .collect(Collectors.toSet());
                
                Platform.runLater(() -> desenharCalendario());
                return null;
            }
        };
        
        task.setOnFailed(e -> System.err.println("Erro ao carregar calendário: " + task.getException().getMessage()));
        
        new Thread(task).start();
    }

    private void desenharCalendario() {
        gridCalendario.getChildren().clear();
        lblMesAno.setText(currentYearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("pt", "PT"))).toUpperCase());

        LocalDate calendarDate = LocalDate.of(currentYearMonth.getYear(), currentYearMonth.getMonth(), 1);
        int dayOfWeek = calendarDate.getDayOfWeek().getValue(); 
        int startOffset = dayOfWeek - 1; // Ajuste para começar na Segunda-feira

        for (int i = 0; i < 42; i++) {
            LocalDate date = calendarDate.minusDays(startOffset).plusDays(i);
            boolean isCurrentMonth = date.getMonth() == currentYearMonth.getMonth();

            VBox cell = criarCelulaDia(date, isCurrentMonth);
            gridCalendario.add(cell, i % 7, i / 7);
        }
    }

    private VBox criarCelulaDia(LocalDate date, boolean isCurrentMonth) {
        VBox cell = new VBox(5);
        cell.setAlignment(Pos.TOP_LEFT);
        cell.getStyleClass().add("calendar-cell");
        cell.setPrefHeight(80);
        cell.setPrefWidth(100);

        Label lblDay = new Label(String.valueOf(date.getDayOfMonth()));
        lblDay.getStyleClass().add("day-label");
        if (!isCurrentMonth) lblDay.getStyleClass().add("other-month");
        cell.getChildren().add(lblDay);

        // Verificar eventos neste dia para desenhar os "dots"
        List<EventoDTO> eventosDoDia = getEventosDoDia(date);
        
        if (!eventosDoDia.isEmpty()) {
            HBox dots = new HBox(3);
            dots.setAlignment(Pos.CENTER);
            
            for (EventoDTO ev : eventosDoDia) {
                Circle dot = new Circle(3);
                dot.getStyleClass().add("event-dot");
                
                if (meusEventosIds.contains(ev.getId())) {
                    dot.getStyleClass().add("dot-my-event"); 
                } else {
                    dot.getStyleClass().add("dot-has-event"); 
                }
                dots.getChildren().add(dot);
                if (dots.getChildren().size() >= 5) break; 
            }
            cell.getChildren().add(dots);
        }

        // Evento de clique na célula
        cell.setOnMouseClicked(e -> selecionarDia(date, eventosDoDia));
        
        return cell;
    }

    private List<EventoDTO> getEventosDoDia(LocalDate date) {
        return todosEventos.stream()
                .filter(e -> e.getDataInicio() != null && e.getDataInicio().toLocalDate().equals(date))
                .collect(Collectors.toList());
    }

    private void selecionarDia(LocalDate date, List<EventoDTO> eventos) {
        lblDiaSelecionado.setText(date.format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
        
        listaEventosDia.getItems().clear();

        if (eventos.isEmpty()) {
            btnAcaoEvento.setDisable(true);
            // Opcional: adicionar um item placeholder na lista, ou deixar vazio
        } else {
            listaEventosDia.getItems().addAll(eventos);
            btnAcaoEvento.setDisable(false);
        }
    }
    
    @FXML
    void verDetalhes() {
        EventoDTO selected = listaEventosDia.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        try {
            FXMLLoader loader = PageNavigator.getLoader("DetalhesEvento.fxml");
            Scene scene = new Scene(loader.load());
            
            DetalhesEventoController controller = loader.getController();
            // Passa callback para atualizar calendário (caso o user se inscreva)
            controller.setEvento(selected, this::carregarDadosEAtualizar); 

            Stage modal = new Stage();
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.setTitle("Detalhes do Evento");
            
            // Garantir que CSS é aplicado ao Modal também
            if (getClass().getResource("/css/app-theme.css") != null) {
                scene.getStylesheets().add(getClass().getResource("/css/app-theme.css").toExternalForm());
            }
            
            modal.setScene(scene);
            modal.showAndWait();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML void mesAnterior() { currentYearMonth = currentYearMonth.minusMonths(1); desenharCalendario(); }
    @FXML void mesSeguinte() { currentYearMonth = currentYearMonth.plusMonths(1); desenharCalendario(); }
}