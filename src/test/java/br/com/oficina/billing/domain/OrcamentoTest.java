package br.com.oficina.billing.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class OrcamentoTest {

    @Test
    void deveCriarComStatusAguardandoAprovacao() {
        var orcamento = Orcamento.criar(UUID.randomUUID(), new BigDecimal("500.00"));
        assertThat(orcamento.status()).isEqualTo(StatusOrcamento.AGUARDANDO_APROVACAO);
        assertThat(orcamento.valorTotal()).isEqualByComparingTo("500.00");
        assertThat(orcamento.criadoEm()).isNotNull();
    }

    @Test
    void deveAprovar() {
        var orcamento = Orcamento.criar(UUID.randomUUID(), new BigDecimal("500.00"));
        orcamento.aprovar();
        assertThat(orcamento.status()).isEqualTo(StatusOrcamento.APROVADO);
        assertThat(orcamento.atualizadoEm()).isNotNull();
    }

    @Test
    void deveRecusar() {
        var orcamento = Orcamento.criar(UUID.randomUUID(), new BigDecimal("500.00"));
        orcamento.recusar();
        assertThat(orcamento.status()).isEqualTo(StatusOrcamento.RECUSADO);
    }

    @Test
    void deveFalharAoAprovarSemEstarAguardando() {
        var orcamento = Orcamento.criar(UUID.randomUUID(), new BigDecimal("500.00"));
        orcamento.aprovar();
        assertThatThrownBy(orcamento::aprovar)
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("aguardando aprovacao");
    }

    @Test
    void deveRegistrarPreferenciaMercadoPago() {
        var orcamento = Orcamento.criar(UUID.randomUUID(), new BigDecimal("500.00"));
        orcamento.aprovar();
        orcamento.registrarPreferenciaMercadoPago("MP-PREF-123");
        assertThat(orcamento.mercadoPagoPreferenceId()).isEqualTo("MP-PREF-123");
        assertThat(orcamento.status()).isEqualTo(StatusOrcamento.PAGAMENTO_PENDENTE);
    }

    @Test
    void deveRegistrarPagamento() {
        var orcamento = Orcamento.criar(UUID.randomUUID(), new BigDecimal("500.00"));
        orcamento.aprovar();
        orcamento.registrarPreferenciaMercadoPago("MP-PREF-123");
        orcamento.registrarPagamento("PAY-456");
        assertThat(orcamento.status()).isEqualTo(StatusOrcamento.PAGO);
        assertThat(orcamento.mercadoPagoPaymentId()).isEqualTo("PAY-456");
    }

    @Test
    void deveCancelar() {
        var orcamento = Orcamento.criar(UUID.randomUUID(), new BigDecimal("500.00"));
        orcamento.cancelar();
        assertThat(orcamento.status()).isEqualTo(StatusOrcamento.CANCELADO);
    }
}
