package gestaoeventos.client.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;

/**
 * Classe base para serviços cliente que comunicam com a API REST.
 * 
 * Fornece configuração comum do HttpClient, ObjectMapper e métodos
 * auxiliares para construir requisições HTTP.
 * 
 * Todos os serviços cliente devem estender esta classe para herdar
 * a configuração base e os métodos utilitários.
 * 
 */
public class ApiClient {

    /** URL base */
    protected static final String BASE_URL = "http://localhost:8080/api";

    /** Cliente HTTP para enviar requisições */
    protected final HttpClient client;

    protected final ObjectMapper mapper;

    /**
     * Construtor que inicializa o HttpClient e ObjectMapper.
     */
    public ApiClient() {
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule()); // Para suporte a LocalDateTime
    }

    /**
     * Cria um builder para requisições POST.
     * 
     */
    protected HttpRequest.Builder postBuilder(String endpoint) {
        return HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(30));
    }

    /**
     * Cria um builder para GET
     * 
     */
    protected HttpRequest.Builder getBuilder(String endpoint) {
        return HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(30))
                .GET();
    }

    /**
     * Cria um builder para PUT
     * 
     */
    protected HttpRequest.Builder putBuilder(String endpoint) {
        return HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(30));
    }

    /**
     * Cria um builder para DELETE
     * 
     */
    protected HttpRequest.Builder deleteBuilder(String endpoint) {
        return HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .timeout(Duration.ofSeconds(30))
                .DELETE();
    }

    /**
     * Faz log de erro com informação útil para debugging
     * 
     */
    protected void logError(String operacao, int statusCode, String body) {
        System.err.println("[API ERROR] " + operacao + " - Status: " + statusCode);
        if (body != null && !body.isBlank()) {
            System.err.println("[API ERROR] Response: " + body.substring(0, Math.min(body.length(), 500)));
        }
    }
}