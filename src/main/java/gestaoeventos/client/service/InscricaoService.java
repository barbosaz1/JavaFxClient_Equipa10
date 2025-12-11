package gestaoeventos.client.service;

import com.fasterxml.jackson.core.type.TypeReference;
import gestaoeventos.dto.InscricaoDTO;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Serviço cliente para operações de Inscrições na API
 * 
 * Esta classe permite gerir inscrições em eventos, incluindo:
 * - Listar inscrições por utilizador ou evento
 * - Cancelar inscrições
 * - Realizar check-in (presença)
 * - Check-in QR Code
 * 
 */
public class InscricaoService extends ApiClient {

    /**
     * Lista todas as inscrições de um utilizador.
     * 
     */
    public List<InscricaoDTO> listarPorUtilizador(Integer numeroUtilizador) {
        try {
            HttpRequest request = getBuilder("/inscricoes/utilizador/" + numeroUtilizador).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return mapper.readValue(response.body(), new TypeReference<List<InscricaoDTO>>() {
                });
            }
            logError("listarPorUtilizador", response.statusCode(), response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    /**
     * Lista todas as inscrições de um evento.
     * 
     */
    public List<InscricaoDTO> listarPorEvento(Integer eventoId) {
        try {
            HttpRequest request = getBuilder("/inscricoes/evento/" + eventoId).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return mapper.readValue(response.body(), new TypeReference<List<InscricaoDTO>>() {
                });
            }
            logError("listarPorEvento", response.statusCode(), response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    /**
     * Cancela uma inscrição existente.
     * 
     */
    public void cancelar(Integer id) {
        try {
            HttpRequest request = postBuilder("/inscricoes/" + id + "/cancelar")
                    .POST(HttpRequest.BodyPublishers.noBody()).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                logError("cancelar", response.statusCode(), response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Realiza o check-in (confirma presença) de uma inscrição.
     * 
     */
    public InscricaoDTO fazerCheckin(Integer inscricaoId) {
        try {
            HttpRequest request = postBuilder("/inscricoes/" + inscricaoId + "/checkin")
                    .POST(HttpRequest.BodyPublishers.noBody()).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return mapper.readValue(response.body(), InscricaoDTO.class);
            }
            logError("fazerCheckin", response.statusCode(), response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Realiza check-in através de um token QR Code.
     * 
     */
    public InscricaoDTO checkinPorQrCode(String token) {
        try {
            Map<String, String> bodyMap = new HashMap<>();
            bodyMap.put("token", token);
            String json = mapper.writeValueAsString(bodyMap);

            HttpRequest request = postBuilder("/inscricoes/checkin/qrcode")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return mapper.readValue(response.body(), InscricaoDTO.class);
            }
            logError("checkinPorQrCode", response.statusCode(), response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
