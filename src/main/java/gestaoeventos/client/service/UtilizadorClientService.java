package gestaoeventos.client.service;

import com.fasterxml.jackson.core.type.TypeReference;
import gestaoeventos.dto.UtilizadorDTO;
import gestaoeventos.dto.UtilizadorCreateDTO;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;

/**
 * Serviço cliente para operações de Utilizadores na API REST.
 * 
 * Esta classe permite gerir utilizadores do sistema:
 * - Listar todos os utilizadores
 * - Obter detalhes de um utilizador específico
 * - Criar novos utilizadores (requer permissão Admin)
 * - Atualizar utilizadores existentes
 * - Ativar/Desativar utilizadores
 * - Apagar utilizadores
 * 
 */
public class UtilizadorClientService extends ApiClient {

    /**
     * Lista todos os utilizadores do sistema.
     * 
     */
    public List<UtilizadorDTO> listarTodos() {
        try {
            HttpRequest request = getBuilder("/utilizadores").build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return mapper.readValue(response.body(), new TypeReference<List<UtilizadorDTO>>() {
                });
            }
            logError("listarTodos", response.statusCode(), response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    /**
     * Obtém os detalhes de um utilizador específico.
     * 
     */
    public UtilizadorDTO obterPorNumero(Integer numero) {
        try {
            HttpRequest request = getBuilder("/utilizadores/" + numero).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return mapper.readValue(response.body(), UtilizadorDTO.class);
            }
            logError("obterPorNumero", response.statusCode(), response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Cria um novo utilizador no sistema.
     * Requer permissões de administrador.
     * 
     */
    public UtilizadorDTO criar(UtilizadorCreateDTO dto) {
        try {
            String json = mapper.writeValueAsString(dto);
            HttpRequest request = postBuilder("/utilizadores")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200 || response.statusCode() == 201) {
                return mapper.readValue(response.body(), UtilizadorDTO.class);
            }
            logError("criar", response.statusCode(), response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Atualiza um utilizador existente.
     * 
     */
    public UtilizadorDTO atualizar(Integer numero, UtilizadorCreateDTO dto) {
        try {
            String json = mapper.writeValueAsString(dto);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(java.net.URI.create(BASE_URL + "/utilizadores/" + numero))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return mapper.readValue(response.body(), UtilizadorDTO.class);
            }
            logError("atualizar", response.statusCode(), response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Apaga um utilizador do sistema.
     * 
     */
    public boolean apagar(Integer numero) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(java.net.URI.create(BASE_URL + "/utilizadores/" + numero))
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

    /**
     * Ativa um utilizador que estava desativado.
     * Um utilizador ativo pode fazer login e utilizar o sistema.
     * 
     */
    public UtilizadorDTO ativar(Integer numero) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(java.net.URI.create(BASE_URL + "/utilizadores/" + numero + "/ativar"))
                    .method("PATCH", HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return mapper.readValue(response.body(), UtilizadorDTO.class);
            }
            logError("ativar", response.statusCode(), response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Desativa um utilizador.
     * Um utilizador desativado não pode fazer login no sistema.
     * 
     */
    public UtilizadorDTO desativar(Integer numero) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(java.net.URI.create(BASE_URL + "/utilizadores/" + numero + "/desativar"))
                    .method("PATCH", HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return mapper.readValue(response.body(), UtilizadorDTO.class);
            }
            logError("desativar", response.statusCode(), response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
