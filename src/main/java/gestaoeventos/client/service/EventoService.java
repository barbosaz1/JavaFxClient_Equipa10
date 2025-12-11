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
 * Serviço cliente para operações de Eventos na API REST.
 * 
 * Esta classe comunica com o servidor backend para realizar operações CRUD
 * em eventos, bem como operações específicas como inscrição e estatísticas.
 * 
 */
public class EventoService extends ApiClient {

    /**
     * Lista todos os eventos disponíveis no sistema.
     * 
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
     * Lista eventos organizados por um utilizador específico.
     * 
     */
    public List<EventoDTO> listarPorOrganizador(Integer organizadorNumero) {
        try {
            HttpRequest request = getBuilder("/eventos/pesquisa?organizadorNumero=" + organizadorNumero).build();
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
     * Obtém estatísticas de um evento específico.
     * 
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
     * 
     */
    public String inscrever(Integer eventoId, Integer userNumero) {
        try {
            HttpRequest request = postBuilder("/eventos/" + eventoId + "/inscrever?utilizadorNumero=" + userNumero)
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return response.body();
            }
            return "ERRO: " + response.statusCode();
        } catch (Exception e) {
            e.printStackTrace();
            return "ERRO: " + e.getMessage();
        }
    }

    /**
     * Cria um novo evento no sistema.
     * 
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
     * 
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
     * Obtém detalhes de um evento específico.
     * 
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
     * Apaga um evento do sistema.
     * 
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
