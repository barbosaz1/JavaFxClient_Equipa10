package gestaoeventos.client.service;

import com.fasterxml.jackson.core.type.TypeReference;
import gestaoeventos.dto.NotificacaoDTO;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Serviço cliente para operações de Notificações na API REST.
 */
public class NotificacaoClientService extends ApiClient {

    /**
     * Lista todas as notificações de um utilizador.
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
     * Lista todos os anúncios visíveis no momento atual.
     */
    public List<NotificacaoDTO> listarAnunciosVisiveis() {
        try {
            HttpRequest request = getBuilder("/notificacoes/anuncios/visiveis").build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return mapper.readValue(response.body(), new TypeReference<List<NotificacaoDTO>>() {
                });
            }
            logError("listarAnunciosVisiveis", response.statusCode(), response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    /**
     * Marca uma notificação como lida.
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
     * Envia um anuncio para todos os utilizadores do sistema.
     */
    public String enviarAnuncioBroadcast(String conteudo, Integer autorNumero,
            LocalDateTime dataInicio, LocalDateTime dataFim) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("conteudo", conteudo);
            body.put("autorNumero", autorNumero);

            if (dataInicio != null) {
                body.put("dataInicioExibicao", dataInicio.toString());
            }
            if (dataFim != null) {
                body.put("dataFimExibicao", dataFim.toString());
            }

            String json = mapper.writeValueAsString(body);

            HttpRequest request = postBuilder("/notificacoes/broadcast")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return response.body();
            }
            logError("enviarAnuncioBroadcast", response.statusCode(), response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Erro ao enviar anuncio";
    }

    /**
     * Lista todos os anuncios do sistema.
     */
    public List<NotificacaoDTO> listarTodosAnuncios() {
        try {
            HttpRequest request = getBuilder("/notificacoes/anuncios").build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return mapper.readValue(response.body(), new TypeReference<List<NotificacaoDTO>>() {
                });
            }
            logError("listarTodosAnuncios", response.statusCode(), response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    /**
     * Atualiza um anuncio existente.
     */
    public NotificacaoDTO atualizarAnuncio(Integer id, String conteudo,
            LocalDateTime dataInicio, LocalDateTime dataFim) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("conteudo", conteudo);

            if (dataInicio != null) {
                body.put("dataInicioExibicao", dataInicio.toString());
            }
            if (dataFim != null) {
                body.put("dataFimExibicao", dataFim.toString());
            }

            String json = mapper.writeValueAsString(body);

            HttpRequest request = putBuilder("/notificacoes/" + id)
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return mapper.readValue(response.body(), NotificacaoDTO.class);
            }
            logError("atualizarAnuncio", response.statusCode(), response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Apaga um anuncio pelo ID.
     */
    public boolean apagarAnuncio(Integer id) {
        try {
            HttpRequest request = deleteBuilder("/notificacoes/" + id)
                    .DELETE()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 204;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
