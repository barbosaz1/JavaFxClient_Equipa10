package gestaoeventos.client.service;

import gestaoeventos.dto.LoginRequestDTO;
import gestaoeventos.dto.LoginResponseDTO;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

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
        } else if (response.statusCode() == 401) {
            throw new Exception("Credenciais inválidas");
        } else if (response.statusCode() == 403) {
            throw new Exception("Conta desativada. Contacte o administrador.");
        } else {
            throw new Exception("Erro no servidor: " + response.statusCode());
        }
    }
}