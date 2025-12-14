package gestaoeventos.dto;

/**
 * DTO para devolver o resultado de uma inscrição.
 * Contém informação sobre se a inscrição foi bem sucedida e o QR code para
 * check-in.
 */
public class InscricaoResultadoDTO {

    private String resultado;
    private Integer inscricaoId;
    private String qrCodeToken;
    private String qrCodeUrl;

    public InscricaoResultadoDTO() {
    }

    public InscricaoResultadoDTO(String resultado, Integer inscricaoId, String qrCodeToken, String qrCodeUrl) {
        this.resultado = resultado;
        this.inscricaoId = inscricaoId;
        this.qrCodeToken = qrCodeToken;
        this.qrCodeUrl = qrCodeUrl;
    }

    // Getters e Setters
    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public Integer getInscricaoId() {
        return inscricaoId;
    }

    public void setInscricaoId(Integer inscricaoId) {
        this.inscricaoId = inscricaoId;
    }

    public String getQrCodeToken() {
        return qrCodeToken;
    }

    public void setQrCodeToken(String qrCodeToken) {
        this.qrCodeToken = qrCodeToken;
    }

    public String getQrCodeUrl() {
        return qrCodeUrl;
    }

    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
    }
}
