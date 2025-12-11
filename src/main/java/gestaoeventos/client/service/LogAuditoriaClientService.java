package gestaoeventos.client.service;

import com.fasterxml.jackson.core.type.TypeReference;
import gestaoeventos.dto.LogAuditoriaDTO;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;

/**
 * Serviço cliente para operações de Logs de Auditoria na API
 * 
 * Permite consultar o histórico de ações realizadas no sistema:
 * - Listar todos os logs
 * - Filtrar por entidade
 * - Filtrar por autor
 * 
 * Apenas utilizadores com perfil Admin têm acesso a estes recursos
 * 
 */
public class LogAuditoriaClientService extends ApiClient {

    /**
     * Lista todos os logs de auditoria do sistema.
     * 
     */
    public List<LogAuditoriaDTO> listarTodos() {
        try {
            HttpRequest request = getBuilder("/logs").build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return mapper.readValue(response.body(), new TypeReference<List<LogAuditoriaDTO>>() {
                });
            }
            logError("listarTodos", response.statusCode(), response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    /**
     * Lista logs relacionados a uma entidade específica.
     * 
     */
    public List<LogAuditoriaDTO> listarPorEntidade(String entidade, Integer id) {
        try {
            HttpRequest request = getBuilder("/logs/entidade/" + entidade + "/" + id).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return mapper.readValue(response.body(), new TypeReference<List<LogAuditoriaDTO>>() {
                });
            }
            logError("listarPorEntidade", response.statusCode(), response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    /**
     * Lista logs criados por um autor específico.
     * r
     */
    public List<LogAuditoriaDTO> listarPorAutor(Integer numero) {
        try {
            HttpRequest request = getBuilder("/logs/autor/" + numero).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return mapper.readValue(response.body(), new TypeReference<List<LogAuditoriaDTO>>() {
                });
            }
            logError("listarPorAutor", response.statusCode(), response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }
}
