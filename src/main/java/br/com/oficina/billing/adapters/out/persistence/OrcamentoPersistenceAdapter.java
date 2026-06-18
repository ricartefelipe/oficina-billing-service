package br.com.oficina.billing.adapters.out.persistence;

import br.com.oficina.billing.application.port.out.OrcamentoPersistencePort;
import br.com.oficina.billing.domain.Orcamento;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
class OrcamentoPersistenceAdapter implements OrcamentoPersistencePort {

    private final OrcamentoJpaRepository repository;

    OrcamentoPersistenceAdapter(OrcamentoJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Orcamento salvar(Orcamento orcamento) {
        return repository.save(orcamento);
    }

    @Override
    public Optional<Orcamento> buscarPorOsId(UUID osId) {
        return repository.findByOsId(osId);
    }

    @Override
    public Optional<Orcamento> buscarPorMpPaymentId(String paymentId) {
        return repository.findByMercadoPagoPaymentId(paymentId);
    }
}
