package gestaoeventos.client.model;

/**
 * Modelo para guardar o resultado de uma inscrição.
 * Contém o QR code necessário para fazer check-in no evento.
 */
public class InscricaoResultado {

    private String resultado;
    private Integer inscricaoId;
    private String qrCodeToken;
    private String qrCodeUrl;

    public InscricaoResultado() {
    }

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

    public boolean isSucesso() {
        return "INSCRICAO_OK".equals(resultado);
    }

    public boolean isListaEspera() {
        return "EVENTO_LOTADO_LISTA_ESPERA".equals(resultado);
    }
}
