package gestaoeventos.repository;

import gestaoeventos.entity.Certificado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CertificadoRepository extends JpaRepository<Certificado, Integer> {

    @Query("SELECT c FROM Certificado c WHERE c.inscricao.utilizador.numero = :numero")
    List<Certificado> findByUtilizadorNumero(Integer numero);

    @Query("SELECT c FROM Certificado c WHERE c.inscricao.evento.id = :eventoId")
    List<Certificado> findByEventoId(Integer eventoId);

    Optional<Certificado> findByCodigoVerificacao(String codigoVerificacao);

    @Query("SELECT c FROM Certificado c WHERE c.inscricao.id = :inscricaoId")
    Optional<Certificado> findByInscricaoId(Integer inscricaoId);

    boolean existsByInscricaoId(Integer inscricaoId);
}
