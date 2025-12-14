package gestaoeventos.client.service;

import gestaoeventos.dto.LoginRequestDTO;
import gestaoeventos.dto.LoginResponseDTO;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

/**
 * Serviço cliente para autenticação na API REST.
 * 
 * Responsável por:
 * - Autenticar utilizadores no sistema
 * - Validar credenciais
 * 
 */
public class AuthService extends ApiClient {

    /**
     * Realiza o login de um utilizador no sistema.
     * 
     */
    public LoginResponseDTO login(Integer numero, String password) throws Exception {
        LoginRequestDTO req = new LoginRequestDTO();
        req.setNumero(numero);
        req.setPassword(password);

        String jsonBody = mapper.writeValueAsString(req);

        HttpRequest request = postBuilder("/auth/login")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return mapper.readValue(response.body(), LoginResponseDTO.class);
        } else {
            // Tentar extrair a mensagem de erro da resposta da API
            String mensagemErro = extrairMensagemErro(response.body(), response.statusCode());
            throw new Exception(mensagemErro);
        }
    }

    /**
     * Extrai a mensagem de erro da resposta JSON da API.
     */
    private String extrairMensagemErro(String responseBody, int statusCode) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> errorBody = mapper.readValue(responseBody, Map.class);
            Object message = errorBody.get("message");
            if (message != null) {
                return message.toString();
            }
        } catch (Exception e) {
            // Ignora erros de parse
        }

        // Fallback para mensagens genéricas
        return switch (statusCode) {
            case 400 -> "Credenciais inválidas";
            case 401 -> "Credenciais inválidas";
            case 403 -> "Conta desativada. Contacte o administrador.";
            case 404 -> "Utilizador não encontrado";
            default -> "Erro no servidor: " + statusCode;
        };
    }
}