package gestaoeventos.entity;

/**
 * Enumeração que define os tipos de certificado
 * 
 * O tipo de certificado influencia o "valor" e a autoridade do documento:
 * - PRESENCA: Certificado básico para quem fez check-in
 * - DOCENTE: Certificado emitido manualmente por um docente (maior autoridade)
 * - ORGANIZADOR: Certificado emitido pelo organizador do evento
 * 
 */
public enum TipoCertificado {

    PRESENCA("Certificado de Presença", 1),

    DOCENTE("Certificado de Participação (Docente)", 2),

    ORGANIZADOR("Certificado de Participação (Organizador)", 2);

    private final String descricao;

    private final int nivelAutoridade;

    TipoCertificado(String descricao, int nivelAutoridade) {
        this.descricao = descricao;
        this.nivelAutoridade = nivelAutoridade;
    }

    /**
     * Obtém a descrição legível do tipo de certificado.
     */
    public String getDescricao() {
        return descricao;
    }

    /**
     * Obtém o nível de autoridade do certificado.
     * Quanto maior o valor, maior a autoridade
     */
    public int getNivelAutoridade() {
        return nivelAutoridade;
    }
}
