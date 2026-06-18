package br.com.oficina.billing.adapters.out.messaging;

import br.com.oficina.billing.application.BillingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.verify;

class BillingSagaEventListenerTest {

    private BillingService billingService;
    private BillingSagaEventListener listener;

    @BeforeEach
    void setUp() {
        billingService = Mockito.mock(BillingService.class);
        listener = new BillingSagaEventListener(billingService);
    }

    @Test
    void onOsAberta_deveGerarOrcamento() {
        UUID osId = UUID.randomUUID();
        listener.onOsAberta(Map.of("osId", osId.toString(), "valorEstimado", "250.00"));
        verify(billingService).gerarOrcamento(osId, new BigDecimal("250.00"));
    }

    @Test
    void onExecucaoFinalizada_deveEncerrarCobranca() {
        UUID osId = UUID.randomUUID();
        listener.onExecucaoFinalizada(Map.of("osId", osId.toString()));
        verify(billingService).encerrarCobranca(osId);
    }
}
