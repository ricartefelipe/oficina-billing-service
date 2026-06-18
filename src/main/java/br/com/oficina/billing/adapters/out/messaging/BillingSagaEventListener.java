package br.com.oficina.billing.adapters.out.messaging;

import br.com.oficina.billing.application.BillingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Component
public class BillingSagaEventListener {

    private static final Logger log = LoggerFactory.getLogger(BillingSagaEventListener.class);

    private final BillingService billingService;

    public BillingSagaEventListener(BillingService billingService) {
        this.billingService = billingService;
    }

    @RabbitListener(queues = "billing-service.os.aberta")
    public void onOsAberta(Map<String, Object> event) {
        UUID osId = UUID.fromString((String) event.get("osId"));
        BigDecimal valorEstimado = new BigDecimal(event.get("valorEstimado").toString());
        log.info("Saga: OS aberta recebida para billing, osId={}", osId);
        billingService.gerarOrcamento(osId, valorEstimado);
    }

    @RabbitListener(queues = "billing-service.execucao.finalizada")
    public void onExecucaoFinalizada(Map<String, Object> event) {
        UUID osId = UUID.fromString((String) event.get("osId"));
        log.info("Saga: execucao finalizada, encerrando cobranca para OS {}", osId);
        billingService.encerrarCobranca(osId);
    }
}
