package br.com.oficina.billing.application;

import br.com.oficina.billing.application.port.out.BillingEventPublisherPort;
import br.com.oficina.billing.application.port.out.MercadoPagoPort;
import br.com.oficina.billing.application.port.out.OrcamentoPersistencePort;
import br.com.oficina.billing.domain.Orcamento;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class BillingService {

    private static final Logger log = LoggerFactory.getLogger(BillingService.class);

    private final OrcamentoPersistencePort persistence;
    private final BillingEventPublisherPort events;
    private final MercadoPagoPort mercadoPago;

    public BillingService(
        OrcamentoPersistencePort persistence,
        BillingEventPublisherPort events,
        MercadoPagoPort mercadoPago
    ) {
        this.persistence = persistence;
        this.events = events;
        this.mercadoPago = mercadoPago;
    }

    @Transactional
    public Orcamento gerarOrcamento(UUID osId, BigDecimal valorTotal) {
        var orcamento = Orcamento.criar(osId, valorTotal);
        var salvo = persistence.salvar(orcamento);
        log.info("Orcamento gerado para OS {}: R$ {}", osId, valorTotal);
        events.publish("orcamento.gerado", buildEvent(osId, "osId", osId.toString(), "valor", valorTotal.toString()));
        return salvo;
    }

    @Transactional
    public void aprovarOrcamento(UUID osId) {
        var orcamento = buscarPorOsIdOuFalhar(osId);
        orcamento.aprovar();

        try {
            String preferenceId = mercadoPago.criarPreferencia(osId, orcamento.valorTotal(), "Servico Oficina " + osId);
            orcamento.registrarPreferenciaMercadoPago(preferenceId);
            log.info("Preferencia MercadoPago criada para OS {}: {}", osId, preferenceId);
        } catch (Exception e) {
            log.error("Erro ao criar preferencia MercadoPago para OS {}: {}", osId, e.getMessage());
            orcamento.cancelar();
            persistence.salvar(orcamento);
            events.publish("orcamento.falhou", buildEvent(osId, "osId", osId.toString(), "motivo", e.getMessage()));
            return;
        }

        persistence.salvar(orcamento);
        events.publish("orcamento.aprovado", buildEvent(osId, "osId", osId.toString(), "preferenceId", orcamento.mercadoPagoPreferenceId()));
    }

    @Transactional
    public void recusarOrcamento(UUID osId) {
        var orcamento = buscarPorOsIdOuFalhar(osId);
        orcamento.recusar();
        persistence.salvar(orcamento);
        log.info("Orcamento recusado para OS {}", osId);
        events.publish("orcamento.recusado", buildEvent(osId, "osId", osId.toString()));
    }

    @Transactional
    public void processarPagamento(String paymentId) {
        var orcamento = persistence.buscarPorMpPaymentId(paymentId)
            .orElseThrow(() -> new NoSuchElementException("Orcamento nao encontrado para pagamento: " + paymentId));

        String status = mercadoPago.consultarStatusPagamento(paymentId);
        if ("approved".equals(status)) {
            orcamento.registrarPagamento(paymentId);
            persistence.salvar(orcamento);
            log.info("Pagamento confirmado para OS {}", orcamento.osId());
            events.publish("pagamento.confirmado", buildEvent(orcamento.osId(), "osId", orcamento.osId().toString(), "paymentId", paymentId));
        } else {
            log.warn("Pagamento recusado para OS {}: status={}", orcamento.osId(), status);
            events.publish("pagamento.falhou", buildEvent(orcamento.osId(), "osId", orcamento.osId().toString(), "status", status));
        }
    }

    @Transactional
    public void processarWebhookMercadoPago(String paymentId) {
        processarPagamento(paymentId);
    }

    @Transactional
    public void encerrarCobranca(UUID osId) {
        persistence.buscarPorOsId(osId).ifPresent(o -> {
            log.info("Cobranca encerrada para OS {}", osId);
        });
    }

    private Orcamento buscarPorOsIdOuFalhar(UUID osId) {
        return persistence.buscarPorOsId(osId)
            .orElseThrow(() -> new NoSuchElementException("Orcamento nao encontrado para OS: " + osId));
    }

    private Map<String, Object> buildEvent(UUID osId, String... pairs) {
        Map<String, Object> event = new HashMap<>();
        event.put("osId", osId.toString());
        for (int i = 0; i + 1 < pairs.length; i += 2) {
            event.put(pairs[i], pairs[i + 1]);
        }
        return event;
    }
}
