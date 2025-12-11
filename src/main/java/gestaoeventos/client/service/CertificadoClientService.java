package gestaoeventos.client.service;

import com.fasterxml.jackson.core.type.TypeReference;
import gestaoeventos.dto.CertificadoDTO;
import gestaoeventos.entity.TipoCertificado;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;

/**
 * Serviço cliente para operações de Certificados na API REST.
 * Esta classe permite gerir certificados emitidos para participantes de eventos:
 * 
 * Suporta diferentes tipos de certificados:
 * - PRESENCA: Certificado básico automático
 * - DOCENTE: Certificado emitido por docente (maior autoridade)
 * - ORGANIZADOR: Certificado emitido pelo organizador
 * 
 */
public class CertificadoClientService extends ApiClient {

    /**
     * Lista todos os certificados de um utilizador.
     * 
     */
    public List<CertificadoDTO> listarPorUtilizador(Integer utilizadorNumero) {
        try {
            HttpRequest request = getBuilder("/certificados/utilizador/" + utilizadorNumero).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return mapper.readValue(response.body(), new TypeReference<List<CertificadoDTO>>() {
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    /**
     * Lista todos os certificados emitidos para um evento.
     * 
     */
    public List<CertificadoDTO> listarPorEvento(Integer eventoId) {
        try {
            HttpRequest request = getBuilder("/certificados/evento/" + eventoId).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return mapper.readValue(response.body(), new TypeReference<List<CertificadoDTO>>() {
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    /**
     * Emite um certificado básico de presença.
     * 
     */
    public CertificadoDTO emitir(Integer inscricaoId, Integer emitidoPorNumero) {
        return emitirComTipo(inscricaoId, emitidoPorNumero, TipoCertificado.PRESENCA);
    }

    /**
     * Emite um certificado com tipo específico.
     * 
     */
    public CertificadoDTO emitirComTipo(Integer inscricaoId, Integer emitidoPorNumero, TipoCertificado tipo) {
        try {
            String url = "/certificados/emitir?inscricaoId=" + inscricaoId +
                    "&emitidoPorNumero=" + emitidoPorNumero +
                    "&tipo=" + tipo.name();

            HttpRequest request = postBuilder(url)
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200 || response.statusCode() == 201) {
                return mapper.readValue(response.body(), CertificadoDTO.class);
            }
            System.err.println("Erro ao emitir certificado: " + response.statusCode());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Emite certificados em massa para todos os participantes com check-in.
     * 
     */
    public String emitirEmMassa(Integer eventoId, Integer emitidoPorNumero) {
        return emitirEmMassaComTipo(eventoId, emitidoPorNumero, TipoCertificado.PRESENCA);
    }

    /**
     * Emite certificados em massa com tipo específico.
     * 
     */
    public String emitirEmMassaComTipo(Integer eventoId, Integer emitidoPorNumero, TipoCertificado tipo) {
        try {
            String url = "/certificados/emitir-massa?eventoId=" + eventoId +
                    "&emitidoPorNumero=" + emitidoPorNumero +
                    "&tipo=" + tipo.name();

            HttpRequest request = postBuilder(url)
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String tipoDescricao = tipo.getDescricao();
                return "Certificados (" + tipoDescricao + ") emitidos com sucesso!";
            }
            return "Erro ao emitir certificados: HTTP " + response.statusCode();
        } catch (Exception e) {
            e.printStackTrace();
            return "Erro ao emitir certificados: " + e.getMessage();
        }
    }

    /**
     * Verifica a autenticidade de um certificado pelo código.
     * 
     */
    public CertificadoDTO verificar(String codigo) {
        try {
            HttpRequest request = getBuilder("/certificados/verificar/" + codigo).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return mapper.readValue(response.body(), CertificadoDTO.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
