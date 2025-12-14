package gestaoeventos.client.controller;

import gestaoeventos.client.model.UserSession;
import gestaoeventos.client.service.*;
import gestaoeventos.client.util.EventoDialogHelper;
import gestaoeventos.client.util.ToastNotification;
import gestaoeventos.dto.*;
import gestaoeventos.entity.TipoCertificado;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Window;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controlador do painel do Docente.
 * Permite criar eventos, registar presenças, emitir certificados e ver
 * estatísticas.
 */
public class DocenteController implements Initializable {

    private final EventoService eventoService = new EventoService();
    private final InscricaoService inscricaoService = new InscricaoService();
    private final CertificadoClientService certificadoService = new CertificadoClientService();
    private final LocalClientService localService = new LocalClientService();

    // Tabela de eventos
    @FXML
    private TableView<EventoDTO> tblEventos;
    @FXML
    private TableColumn<EventoDTO, String> colEventoTitulo;
    @FXML
    private TableColumn<EventoDTO, String> colEventoData;
    @FXML
    private TableColumn<EventoDTO, String> colEventoEstado;
    @FXML
    private TableColumn<EventoDTO, Void> colEventoAcoes;

    // Tabela de presenças
    @FXML
    private ComboBox<EventoDTO> cmbEventosPresencas;
    @FXML
    private TableView<InscricaoDTO> tblInscritos;
    @FXML
    private TableColumn<InscricaoDTO, String> colInscritoNome;
    @FXML
    private TableColumn<InscricaoDTO, String> colInscritoEstado;
    @FXML
    private TableColumn<InscricaoDTO, String> colInscritoCheckIn;
    @FXML
    private TableColumn<InscricaoDTO, Void> colInscritoAcoes;

    // Certificados
    @FXML
    private ComboBox<EventoDTO> cmbEventosCertificados;
    @FXML
    private Label lblResultadoCertificados;
    @FXML
    private TableView<CertificadoDTO> tblCertificadosEmitidos;
    @FXML
    private TableColumn<CertificadoDTO, String> colCertUtilizador;
    @FXML
    private TableColumn<CertificadoDTO, String> colCertData;
    @FXML
    private TableColumn<CertificadoDTO, String> colCertCodigo;

    // Estatísticas
    @FXML
    private ComboBox<EventoDTO> cmbEventosStats;
    @FXML
    private GridPane gridEstatisticas;
    @FXML
    private Label lblTotalInscricoes, lblCheckIns, lblCertificados, lblOcupacao;

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupEventosTable();
        setupInscritosTable();
        setupCertificadosTable();
        carregarEventos();
        setupCombos();
    }

    private void setupEventosTable() {
        colEventoTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colEventoData.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getDataInicio() != null ? c.getValue().getDataInicio().format(DTF) : ""));
        colEventoEstado.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getEstado() != null ? c.getValue().getEstado().toString() : ""));

        colEventoAcoes.setCellFactory(col -> new TableCell<>() {
            private final Button btnEditar = new Button("✏️");
            private final Button btnApagar = new Button("🗑️");
            private final MenuButton btnEstado = new MenuButton("📋");
            private final HBox hbox = new HBox(5, btnEditar, btnEstado, btnApagar);

            {
                btnEditar.getStyleClass().add("btn-icon");
                btnApagar.getStyleClass().add("btn-icon");
                btnEstado.getStyleClass().add("btn-icon");
                btnApagar.setStyle("-fx-text-fill: #ef4444;");
                btnEstado.setTooltip(new Tooltip("Alterar estado"));

                MenuItem itemRascunho = new MenuItem("📝 Rascunho");
                MenuItem itemPublicar = new MenuItem("🟢 Publicar");
                MenuItem itemCancelar = new MenuItem("🔴 Cancelar");
                MenuItem itemConcluir = new MenuItem("✅ Concluir");

                itemRascunho.setOnAction(e -> alterarEstadoEvento(getTableView().getItems().get(getIndex()),
                        gestaoeventos.entity.EstadoEvento.RASCUNHO));
                itemPublicar.setOnAction(e -> alterarEstadoEvento(getTableView().getItems().get(getIndex()),
                        gestaoeventos.entity.EstadoEvento.PUBLICADO));
                itemCancelar.setOnAction(e -> alterarEstadoEvento(getTableView().getItems().get(getIndex()),
                        gestaoeventos.entity.EstadoEvento.CANCELADO));
                itemConcluir.setOnAction(e -> alterarEstadoEvento(getTableView().getItems().get(getIndex()),
                        gestaoeventos.entity.EstadoEvento.CONCLUIDO));

                btnEstado.getItems().addAll(itemRascunho, itemPublicar, itemCancelar, itemConcluir);

                btnEditar.setOnAction(e -> editarEvento(getTableView().getItems().get(getIndex())));
                btnApagar.setOnAction(e -> apagarEvento(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : hbox);
            }
        });
    }

    private void setupInscritosTable() {
        colInscritoNome
                .setCellValueFactory(c -> new SimpleStringProperty("User #" + c.getValue().getUtilizadorNumero()));
        colInscritoEstado.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getEstado() != null ? c.getValue().getEstado().toString() : ""));
        colInscritoCheckIn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().isCheckIn() ? "✓" : "✗"));

        colInscritoAcoes.setCellFactory(col -> new TableCell<>() {
            private final Button btnCheckIn = new Button("✅ Check-in");
            private final Button btnCert = new Button("📜 Certificado");
            private final HBox hbox = new HBox(5, btnCheckIn, btnCert);

            {
                btnCheckIn.getStyleClass().add("btn-small");
                btnCert.getStyleClass().add("btn-small");

                btnCheckIn.setOnAction(e -> fazerCheckIn(getTableView().getItems().get(getIndex())));
                btnCert.setOnAction(e -> emitirCertificado(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : hbox);
            }
        });
    }

    private void setupCertificadosTable() {
        colCertUtilizador.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUtilizadorNome()));
        colCertData.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getDataEmissao() != null ? c.getValue().getDataEmissao().format(DTF) : ""));
        colCertCodigo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCodigoVerificacao()));
    }

    private void setupCombos() {
        try {
            List<EventoDTO> eventos = eventoService.listarTodos();
            javafx.util.StringConverter<EventoDTO> converter = new javafx.util.StringConverter<>() {
                @Override
                public String toString(EventoDTO e) {
                    return e != null ? e.getTitulo() : "";
                }

                @Override
                public EventoDTO fromString(String s) {
                    return null;
                }
            };

            cmbEventosPresencas.setItems(FXCollections.observableArrayList(eventos));
            cmbEventosPresencas.setConverter(converter);
            cmbEventosCertificados.setItems(FXCollections.observableArrayList(eventos));
            cmbEventosCertificados.setConverter(converter);
            cmbEventosStats.setItems(FXCollections.observableArrayList(eventos));
            cmbEventosStats.setConverter(converter);
        } catch (Exception e) {
            mostrarErro("Erro ao carregar lista de eventos: " + e.getMessage());
        }
    }

    @FXML
    public void carregarEventos() {
        if (!UserSession.getInstance().isLoggedIn())
            return;
        try {
            Integer numero = UserSession.getInstance().getUser().getNumero();
            List<EventoDTO> eventos = eventoService.listarPorOrganizador(numero);
            tblEventos.setItems(FXCollections.observableArrayList(eventos));
        } catch (Exception e) {
            mostrarErro("Erro ao carregar eventos: " + e.getMessage());
        }
    }

    @FXML
    public void criarEvento() {
        if (!UserSession.getInstance().isLoggedIn()) {
            mostrarErro("Deve estar autenticado para criar eventos.");
            return;
        }

        try {
            List<LocalDTO> locais = localService.listarTodos();
            if (locais.isEmpty()) {
                mostrarAviso("Não existem locais disponíveis. Contacte o administrador.");
                return;
            }

            Integer criadorNumero = UserSession.getInstance().getUser().getNumero();
            Optional<EventoCreateDTO> resultado = EventoDialogHelper.mostrarDialogoCriarEvento(locais, criadorNumero);

            resultado.ifPresent(dto -> {
                EventoDTO eventoCriado = eventoService.criar(dto);
                if (eventoCriado != null) {
                    mostrarSucesso("Evento '" + eventoCriado.getTitulo() + "' criado com sucesso!");
                    carregarEventos();
                    setupCombos();
                } else {
                    mostrarErro("Falha ao criar o evento. Verifique os dados e tente novamente.");
                }
            });
        } catch (Exception e) {
            mostrarErro("Erro ao criar evento: " + e.getMessage());
        }
    }

    private void editarEvento(EventoDTO evento) {
        if (!UserSession.getInstance().isLoggedIn())
            return;

        try {
            List<LocalDTO> locais = localService.listarTodos();
            Integer docenteNumero = UserSession.getInstance().getUser().getNumero();

            java.util.Optional<EventoCreateDTO> resultado = EventoDialogHelper.mostrarDialogoEditarEvento(
                    evento, locais, docenteNumero);

            resultado.ifPresent(dto -> {
                EventoDTO atualizado = eventoService.atualizar(evento.getId(), dto);
                if (atualizado != null) {
                    mostrarSucesso("Evento '" + atualizado.getTitulo() + "' atualizado com sucesso!");
                    carregarEventos();
                    setupCombos();
                } else {
                    mostrarErro("Falha ao atualizar o evento.");
                }
            });
        } catch (Exception e) {
            mostrarErro("Erro ao editar evento: " + e.getMessage());
        }
    }

    private void alterarEstadoEvento(EventoDTO evento, gestaoeventos.entity.EstadoEvento novoEstado) {
        if (evento.getEstado() == novoEstado) {
            mostrarInfo("O evento já está no estado " + novoEstado + ".");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar Alteração de Estado");
        confirm.setHeaderText("Alterar estado do evento: " + evento.getTitulo());
        confirm.setContentText("O estado atual é " + evento.getEstado() + ". Deseja alterar para " + novoEstado + "?");

        confirm.showAndWait().filter(r -> r == ButtonType.OK).ifPresent(r -> {
            try {
                EventoCreateDTO dto = new EventoCreateDTO();
                dto.setTitulo(evento.getTitulo());
                dto.setDescricao(evento.getDescricao());
                dto.setDataInicio(evento.getDataInicio());
                dto.setDataFim(evento.getDataFim());
                dto.setTipo(evento.getTipo());
                dto.setMaxParticipantes(evento.getMaxParticipantes());
                dto.setLocalId(evento.getLocalId());
                dto.setCriadorNumero(evento.getCriadorNumero());
                dto.setEstado(novoEstado);

                EventoDTO atualizado = eventoService.atualizar(evento.getId(), dto);
                if (atualizado != null) {
                    mostrarSucesso("Estado alterado para " + novoEstado + " com sucesso!");
                    carregarEventos();
                    setupCombos();
                } else {
                    mostrarErro("Falha ao alterar o estado do evento.");
                }
            } catch (Exception e) {
                mostrarErro("Erro ao alterar estado: " + e.getMessage());
            }
        });
    }

    private void apagarEvento(EventoDTO evento) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar Eliminação");
        confirm.setHeaderText("Apagar evento: " + evento.getTitulo());
        confirm.setContentText("Esta ação não pode ser revertida. Tem a certeza?");

        confirm.showAndWait().filter(r -> r == ButtonType.OK).ifPresent(r -> {
            if (eventoService.apagar(evento.getId())) {
                mostrarSucesso("Evento eliminado com sucesso!");
                carregarEventos();
                setupCombos();
            } else {
                mostrarErro("Falha ao eliminar o evento.");
            }
        });
    }

    @FXML
    public void carregarInscritos() {
        EventoDTO selected = cmbEventosPresencas.getValue();
        if (selected == null) {
            mostrarAviso("Selecione um evento para ver os inscritos.");
            return;
        }
        try {
            List<InscricaoDTO> inscritos = inscricaoService.listarPorEvento(selected.getId());
            tblInscritos.setItems(FXCollections.observableArrayList(inscritos));
            mostrarInfo("Carregados " + inscritos.size() + " inscritos.");
        } catch (Exception e) {
            mostrarErro("Erro ao carregar inscritos: " + e.getMessage());
        }
    }

    private void fazerCheckIn(InscricaoDTO inscricao) {
        try {
            inscricaoService.fazerCheckin(inscricao.getId());
            mostrarSucesso("Check-in realizado com sucesso!");
            carregarInscritos();
        } catch (Exception e) {
            mostrarErro("Erro ao realizar check-in: " + e.getMessage());
        }
    }

    private void emitirCertificado(InscricaoDTO inscricao) {
        if (!inscricao.isCheckIn()) {
            mostrarAviso("O participante precisa fazer check-in primeiro.");
            return;
        }
        if (!UserSession.getInstance().isLoggedIn())
            return;

        try {
            Integer emitidoPor = UserSession.getInstance().getUser().getNumero();
            CertificadoDTO cert = certificadoService.emitirComTipo(inscricao.getId(), emitidoPor,
                    TipoCertificado.DOCENTE);

            if (cert != null) {
                mostrarSucesso("Certificado de Docente emitido!\nCódigo: " + cert.getCodigoVerificacao());
            } else {
                mostrarErro("Falha ao emitir certificado.");
            }
        } catch (Exception e) {
            mostrarErro("Erro ao emitir certificado: " + e.getMessage());
        }
    }

    @FXML
    public void emitirCertificadosEmMassa() {
        EventoDTO selected = cmbEventosCertificados.getValue();
        if (selected == null) {
            mostrarAviso("Selecione um evento.");
            return;
        }
        if (!UserSession.getInstance().isLoggedIn())
            return;

        try {
            Integer emitidoPor = UserSession.getInstance().getUser().getNumero();
            String resultado = certificadoService.emitirEmMassaComTipo(selected.getId(), emitidoPor,
                    TipoCertificado.DOCENTE);
            lblResultadoCertificados.setText(resultado);
            mostrarSucesso("Certificados de Docente emitidos!");

            List<CertificadoDTO> certs = certificadoService.listarPorEvento(selected.getId());
            tblCertificadosEmitidos.setItems(FXCollections.observableArrayList(certs));
        } catch (Exception e) {
            mostrarErro("Erro ao emitir certificados: " + e.getMessage());
        }
    }

    @FXML
    public void verEstatisticas() {
        EventoDTO selected = cmbEventosStats.getValue();
        if (selected == null) {
            mostrarAviso("Selecione um evento.");
            return;
        }

        try {
            EstatisticasEventoDTO stats = eventoService.obterEstatisticas(selected.getId());
            if (stats != null) {
                gridEstatisticas.setVisible(true);
                lblTotalInscricoes.setText(String.valueOf(stats.getTotalInscricoes()));
                lblCheckIns.setText(String.valueOf(stats.getCheckInsRealizados()));
                lblCertificados.setText(String.valueOf(stats.getCertificadosEmitidos()));
                lblOcupacao.setText(String.format("%.1f%%", stats.getPercentualOcupacao()));
            } else {
                mostrarErro("Não foi possível obter estatísticas.");
            }
        } catch (Exception e) {
            mostrarErro("Erro ao obter estatísticas: " + e.getMessage());
        }
    }

    // Métodos de notificação
    private Window getWindow() {
        return tblEventos.getScene() != null ? tblEventos.getScene().getWindow() : null;
    }

    private void mostrarSucesso(String mensagem) {
        Window window = getWindow();
        if (window != null)
            ToastNotification.sucesso(window, mensagem);
    }

    private void mostrarErro(String mensagem) {
        Window window = getWindow();
        if (window != null)
            ToastNotification.erro(window, mensagem);
    }

    private void mostrarAviso(String mensagem) {
        Window window = getWindow();
        if (window != null)
            ToastNotification.aviso(window, mensagem);
    }

    private void mostrarInfo(String mensagem) {
        Window window = getWindow();
        if (window != null)
            ToastNotification.info(window, mensagem);
    }
}
