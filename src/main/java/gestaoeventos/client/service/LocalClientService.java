package gestaoeventos.client.service;

import com.fasterxml.jackson.core.type.TypeReference;
import gestaoeventos.dto.LocalDTO;
import gestaoeventos.dto.LocalCreateDTO;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;

/**
 * Serviço cliente para operações de Locais na API REST.
 * 
 * Esta classe permite gerir locais (espaços) onde os eventos são realizados:
 * - Listar todos os locais disponíveis
 * - Obter detalhes de um local específico
 * - Criar novos locais (requer permissão Admin)
 * - Atualizar locais existentes
 * - Apagar locais
 * 
 */
public class LocalClientService extends ApiClient {

    /**
     * Lista todos os locais disponíveis no sistema.
     * 
     */
    public List<LocalDTO> listarTodos() {
        try {
            HttpRequest request = getBuilder("/locais").build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return mapper.readValue(response.body(), new TypeReference<List<LocalDTO>>() {
                });
            }
            logError("listarTodos", response.statusCode(), response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    /**
     * Obtém os detalhes de um local específico.
     * 
     */
    public LocalDTO obterPorId(Integer id) {
        try {
            HttpRequest request = getBuilder("/locais/" + id).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return mapper.readValue(response.body(), LocalDTO.class);
            }
            logError("obterPorId", response.statusCode(), response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Cria um novo local no sistema.
     * Requer permissões de administrador.
     * 
     */
    public LocalDTO criar(LocalCreateDTO dto) {
        try {
            String json = mapper.writeValueAsString(dto);
            HttpRequest request = postBuilder("/locais")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200 || response.statusCode() == 201) {
                return mapper.readValue(response.body(), LocalDTO.class);
            }
            logError("criar", response.statusCode(), response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Atualiza um local existente.
     * 
     */
    public LocalDTO atualizar(Integer id, LocalCreateDTO dto) {
        try {
            String json = mapper.writeValueAsString(dto);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(java.net.URI.create(BASE_URL + "/locais/" + id))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return mapper.readValue(response.body(), LocalDTO.class);
            }
            logError("atualizar", response.statusCode(), response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Apaga um local do sistema.
     * Um local só pode ser apagado se não tiver eventos associados.
     * 
     */
    public boolean apagar(Integer id) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(java.net.URI.create(BASE_URL + "/locais/" + id))
                    .DELETE()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 204 || response.statusCode() == 200) {
                return true;
            }
            logError("apagar", response.statusCode(), response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
