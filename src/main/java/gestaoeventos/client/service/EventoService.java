package gestaoeventos.client.service;

import com.fasterxml.jackson.core.type.TypeReference;
import gestaoeventos.dto.EventoDTO;
import gestaoeventos.dto.EventoCreateDTO;
import gestaoeventos.dto.EstatisticasEventoDTO;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;

/**
 * Serviço cliente para comunicação com a API de eventos.
 */
public class EventoService extends ApiClient {

    /**
     * Lista todos os eventos do sistema.
     */
    public List<EventoDTO> listarTodos() {
        try {
            HttpRequest request = getBuilder("/eventos").build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return mapper.readValue(response.body(), new TypeReference<List<EventoDTO>>() {
                });
            }
            throw new RuntimeException("Erro ao listar eventos: " + response.statusCode());
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * Lista eventos de um organizador específico.
     */
    public List<EventoDTO> listarPorOrganizador(Integer organizadorNumero) {
        try {
            HttpRequest request = getBuilder("/eventos/search?organizadorNumero=" + organizadorNumero).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return mapper.readValue(response.body(), new TypeReference<List<EventoDTO>>() {
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    /**
     * Obtém as estatísticas de um evento.
     */
    public EstatisticasEventoDTO obterEstatisticas(Integer eventoId) {
        try {
            HttpRequest request = getBuilder("/eventos/" + eventoId + "/estatisticas").build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return mapper.readValue(response.body(), EstatisticasEventoDTO.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Inscreve um utilizador num evento.
     * Devolve o resultado com o QR code para check-in.
     */
    public gestaoeventos.client.model.InscricaoResultado inscrever(Integer eventoId, Integer userNumero) {
        try {
            HttpRequest request = postBuilder("/eventos/" + eventoId + "/inscrever?utilizadorNumero=" + userNumero)
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return mapper.readValue(response.body(), gestaoeventos.client.model.InscricaoResultado.class);
            }
            gestaoeventos.client.model.InscricaoResultado erro = new gestaoeventos.client.model.InscricaoResultado();
            erro.setResultado("ERRO: " + response.statusCode());
            return erro;
        } catch (Exception e) {
            e.printStackTrace();
            gestaoeventos.client.model.InscricaoResultado erro = new gestaoeventos.client.model.InscricaoResultado();
            erro.setResultado("ERRO: " + e.getMessage());
            return erro;
        }
    }

    /**
     * Cria um novo evento.
     */
    public EventoDTO criar(EventoCreateDTO dto) {
        try {
            String json = mapper.writeValueAsString(dto);
            HttpRequest request = postBuilder("/eventos")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200 || response.statusCode() == 201) {
                return mapper.readValue(response.body(), EventoDTO.class);
            }
            System.err.println("Erro ao criar evento: " + response.statusCode() + " - " + response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Atualiza um evento existente.
     */
    public EventoDTO atualizar(Integer id, EventoCreateDTO dto) {
        try {
            String json = mapper.writeValueAsString(dto);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(java.net.URI.create(BASE_URL + "/eventos/" + id))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return mapper.readValue(response.body(), EventoDTO.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Obtém os dados de um evento pelo ID.
     */
    public EventoDTO obterPorId(Integer id) {
        try {
            HttpRequest request = getBuilder("/eventos/" + id).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return mapper.readValue(response.body(), EventoDTO.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Apaga um evento.
     */
    public boolean apagar(Integer id) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(java.net.URI.create(BASE_URL + "/eventos/" + id))
                    .DELETE()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 204 || response.statusCode() == 200;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
