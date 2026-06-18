package br.com.oficina.billing.application.port.out;

import br.com.oficina.billing.domain.Orcamento;

import java.util.Optional;
import java.util.UUID;

public interface OrcamentoPersistencePort {
    Orcamento salvar(Orcamento orcamento);
    Optional<Orcamento> buscarPorOsId(UUID osId);
    Optional<Orcamento> buscarPorMpPaymentId(String paymentId);
}
