package gestaoeventos.repository;

import gestaoeventos.entity.Notificacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificacaoRepository extends JpaRepository<Notificacao, Integer> {

    List<Notificacao> findByDestinatarioNumeroOrderByDataCriacaoDesc(Integer destinatarioNumero);
}

