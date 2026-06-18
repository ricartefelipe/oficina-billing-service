package br.com.oficina.billing.application;

import br.com.oficina.billing.application.port.out.BillingEventPublisherPort;
import br.com.oficina.billing.application.port.out.MercadoPagoPort;
import br.com.oficina.billing.application.port.out.OrcamentoPersistencePort;
import br.com.oficina.billing.domain.Orcamento;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillingServiceTest {

    @Mock OrcamentoPersistencePort persistence;
    @Mock BillingEventPublisherPort events;
    @Mock MercadoPagoPort mercadoPago;
    @InjectMocks BillingService service;

    @Test
    void deveGerarOrcamentoEPublicarEvento() {
        var osId = UUID.randomUUID();
        var orcamento = Orcamento.criar(osId, new BigDecimal("300.00"));
        when(persistence.salvar(any())).thenReturn(orcamento);

        var resultado = service.gerarOrcamento(osId, new BigDecimal("300.00"));

        assertThat(resultado).isNotNull();
        verify(persistence).salvar(any());
        verify(events).publish(eq("orcamento.gerado"), any());
    }

    @Test
    void deveAprovarOrcamentoECriarPreferenciaMp() {
        var osId = UUID.randomUUID();
        var orcamento = Orcamento.criar(osId, new BigDecimal("300.00"));
        when(persistence.buscarPorOsId(osId)).thenReturn(Optional.of(orcamento));
        when(mercadoPago.criarPreferencia(any(), any(), any())).thenReturn("MP-PREF-TESTE");
        when(persistence.salvar(any())).thenReturn(orcamento);

        service.aprovarOrcamento(osId);

        verify(mercadoPago).criarPreferencia(eq(osId), any(), any());
        verify(events).publish(eq("orcamento.aprovado"), any());
    }

    @Test
    void devePublicarOrcamentoFalhouSeMercadoPagoFalha() {
        var osId = UUID.randomUUID();
        var orcamento = Orcamento.criar(osId, new BigDecimal("300.00"));
        when(persistence.buscarPorOsId(osId)).thenReturn(Optional.of(orcamento));
        when(mercadoPago.criarPreferencia(any(), any(), any())).thenThrow(new RuntimeException("MP error"));
        when(persistence.salvar(any())).thenReturn(orcamento);

        service.aprovarOrcamento(osId);

        verify(events).publish(eq("orcamento.falhou"), any());
    }

    @Test
    void deveRecusarOrcamentoEPublicarEvento() {
        var osId = UUID.randomUUID();
        var orcamento = Orcamento.criar(osId, new BigDecimal("300.00"));
        when(persistence.buscarPorOsId(osId)).thenReturn(Optional.of(orcamento));
        when(persistence.salvar(any())).thenReturn(orcamento);

        service.recusarOrcamento(osId);

        verify(events).publish(eq("orcamento.recusado"), any());
    }

    @Test
    void deveProcessarPagamentoAprovado() {
        var osId = UUID.randomUUID();
        var orcamento = Orcamento.criar(osId, new BigDecimal("300.00"));
        orcamento.aprovar();
        orcamento.registrarPreferenciaMercadoPago("MP-PREF");
        when(persistence.buscarPorMpPaymentId("PAY-123")).thenReturn(Optional.of(orcamento));
        when(mercadoPago.consultarStatusPagamento("PAY-123")).thenReturn("approved");
        when(persistence.salvar(any())).thenReturn(orcamento);

        service.processarPagamento("PAY-123");

        verify(events).publish(eq("pagamento.confirmado"), any());
    }

    @Test
    void devePublicarPagamentoFalhouSeRecusado() {
        var osId = UUID.randomUUID();
        var orcamento = Orcamento.criar(osId, new BigDecimal("300.00"));
        orcamento.aprovar();
        orcamento.registrarPreferenciaMercadoPago("MP-PREF");
        when(persistence.buscarPorMpPaymentId("PAY-123")).thenReturn(Optional.of(orcamento));
        when(mercadoPago.consultarStatusPagamento("PAY-123")).thenReturn("rejected");

        service.processarPagamento("PAY-123");

        verify(events).publish(eq("pagamento.falhou"), any());
    }
}
