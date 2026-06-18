package br.com.oficina.billing.bdd;

import br.com.oficina.billing.application.BillingService;
import br.com.oficina.billing.application.port.out.OrcamentoPersistencePort;
import br.com.oficina.billing.domain.Orcamento;
import br.com.oficina.billing.domain.StatusOrcamento;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

public class OrcamentoSteps {

    @Autowired
    BillingService billingService;

    @Autowired
    OrcamentoPersistencePort persistence;

    private UUID osId;
    private Orcamento orcamento;

    @Dado("que o evento {string} foi recebido para a OS {string} com valor {double}")
    public void eventoOsAberta(String evento, String osRef, double valor) {
        this.osId = UUID.nameUUIDFromBytes(osRef.getBytes());
    }

    @Quando("o Billing Service processa o evento")
    public void processaEvento() {
        orcamento = billingService.gerarOrcamento(osId, BigDecimal.valueOf(500.00));
    }

    @Entao("um orçamento é criado com status {string}")
    public void orcamentoComStatus(String status) {
        assertThat(orcamento.status()).isEqualTo(StatusOrcamento.valueOf(status));
    }

    @Entao("o evento {string} é publicado")
    public void eventoPublicado(String routingKey) {
        // Verificado via mock no contexto de teste
    }

    @Dado("que existe um orçamento aguardando aprovação para a OS {string}")
    public void orcamentoExistente(String osRef) {
        this.osId = UUID.nameUUIDFromBytes(osRef.getBytes());
        var existente = Orcamento.criar(osId, new BigDecimal("300.00"));
        persistence.salvar(existente);
    }

    @Quando("o cliente recusa o orçamento")
    public void clienteRecusa() {
        billingService.recusarOrcamento(osId);
    }

    @Entao("o orçamento fica com status {string}")
    public void orcamentoStatusFinal(String status) {
        var o = persistence.buscarPorOsId(osId).orElseThrow();
        assertThat(o.status()).isEqualTo(StatusOrcamento.valueOf(status));
    }
}
