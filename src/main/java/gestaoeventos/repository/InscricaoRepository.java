package gestaoeventos.repository;

import gestaoeventos.entity.EstadoInscricao;
import gestaoeventos.entity.Inscricao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InscricaoRepository extends JpaRepository<Inscricao, Integer> {

    List<Inscricao> findByEventoId(Integer eventoId);

    List<Inscricao> findByUtilizadorNumero(Integer numero);

    int countByEventoIdAndEstado(Integer eventoId, EstadoInscricao estado);

    Optional<Inscricao> findByQrCodeCheckin(String qrCodeCheckin);

    List<Inscricao> findByEventoIdAndCheckInTrue(Integer eventoId);
}
