package gestaoeventos.client.service;

import com.fasterxml.jackson.core.type.TypeReference;
import gestaoeventos.dto.NotificacaoDTO;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Serviço cliente para operações de Notificações na API REST.
 * 
 * Gere notificações e anúncios do sistema:
 * - Listar notificações de um utilizador
 * - Marcar notificações como lidas
 * - Enviar anúncios para todos os utilizadores (broadcast)
 * 
 */
public class NotificacaoClientService extends ApiClient {

    /**
     * Lista todas as notificações de um utilizador.
     * 
     */
    public List<NotificacaoDTO> listarPorUtilizador(Integer numero) {
        try {
            HttpRequest request = getBuilder("/notificacoes/utilizador/" + numero).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return mapper.readValue(response.body(), new TypeReference<List<NotificacaoDTO>>() {
                });
            }
            logError("listarPorUtilizador", response.statusCode(), response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    /**
     * Marca uma notificação como lida.
     * 
     */
    public boolean marcarComoLida(Integer id) {
        try {
            HttpRequest request = postBuilder("/notificacoes/" + id + "/lida")
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Envia um anúncio para todos os utilizadores do sistema.
     * Requer permissões de Gestor ou Admin.
     * 
     */
    public String enviarAnuncioBroadcast(String conteudo, Integer autorNumero) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("conteudo", conteudo);
            body.put("autorNumero", autorNumero);
            String json = mapper.writeValueAsString(body);

            HttpRequest request = postBuilder("/notificacoes/broadcast")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return "Anúncio enviado com sucesso!";
            }
            logError("enviarAnuncioBroadcast", response.statusCode(), response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Erro ao enviar anúncio";
    }
}
