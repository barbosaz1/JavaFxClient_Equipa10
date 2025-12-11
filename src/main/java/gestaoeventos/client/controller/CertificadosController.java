package gestaoeventos.client.controller;

import gestaoeventos.client.model.UserSession;
import gestaoeventos.client.service.ApiClient;
import gestaoeventos.dto.CertificadoDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

public class CertificadosController extends ApiClient implements Initializable {

    @FXML
    private TableView<CertificadoDTO> tblCertificados;
    @FXML
    private TableColumn<CertificadoDTO, String> colEvento;
    @FXML
    private TableColumn<CertificadoDTO, String> colData;
    @FXML
    private TableColumn<CertificadoDTO, String> colCodigo;

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colEvento.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEventoTitulo()));
        colData.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getDataEmissao() != null ? c.getValue().getDataEmissao().format(DTF) : ""));
        colCodigo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCodigoVerificacao()));

        carregarCertificados();
    }

    @FXML
    public void carregarCertificados() {
        if (!UserSession.getInstance().isLoggedIn())
            return;

        Integer numero = UserSession.getInstance().getUser().getNumero();
        List<CertificadoDTO> certificados = listarCertificados(numero);
        tblCertificados.setItems(FXCollections.observableArrayList(certificados));
    }

    private List<CertificadoDTO> listarCertificados(Integer utilizadorNumero) {
        try {
            HttpRequest request = getBuilder("/certificados/utilizador/" + utilizadorNumero).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return mapper.readValue(response.body(), new TypeReference<List<CertificadoDTO>>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
