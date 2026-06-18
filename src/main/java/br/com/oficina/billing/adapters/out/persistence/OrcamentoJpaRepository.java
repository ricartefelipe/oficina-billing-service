package br.com.oficina.billing.adapters.out.persistence;

import br.com.oficina.billing.domain.Orcamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface OrcamentoJpaRepository extends JpaRepository<Orcamento, UUID> {
    Optional<Orcamento> findByOsId(UUID osId);
    Optional<Orcamento> findByMercadoPagoPaymentId(String paymentId);
}
