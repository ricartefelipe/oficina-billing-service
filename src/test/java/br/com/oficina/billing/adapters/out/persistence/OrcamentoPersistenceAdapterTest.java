package br.com.oficina.billing.adapters.out.persistence;

import br.com.oficina.billing.domain.Orcamento;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OrcamentoPersistenceAdapterTest {

    private final OrcamentoJpaRepository repository = Mockito.mock(OrcamentoJpaRepository.class);
    private final OrcamentoPersistenceAdapter adapter = new OrcamentoPersistenceAdapter(repository);

    private Orcamento criarOrcamento() {
        return Orcamento.criar(UUID.randomUUID(), new BigDecimal("300.00"));
    }

    @Test
    void salvar_deveDelegar() {
        var orcamento = criarOrcamento();
        when(repository.save(orcamento)).thenReturn(orcamento);

        var resultado = adapter.salvar(orcamento);

        assertThat(resultado).isEqualTo(orcamento);
        verify(repository).save(orcamento);
    }

    @Test
    void buscarPorOsId_deveDelegar() {
        var osId = UUID.randomUUID();
        var orcamento = criarOrcamento();
        when(repository.findByOsId(osId)).thenReturn(Optional.of(orcamento));

        var resultado = adapter.buscarPorOsId(osId);

        assertThat(resultado).isPresent();
        verify(repository).findByOsId(osId);
    }

    @Test
    void buscarPorMpPaymentId_deveDelegar() {
        var orcamento = criarOrcamento();
        when(repository.findByMercadoPagoPaymentId("PAY-123")).thenReturn(Optional.of(orcamento));

        var resultado = adapter.buscarPorMpPaymentId("PAY-123");

        assertThat(resultado).isPresent();
        verify(repository).findByMercadoPagoPaymentId("PAY-123");
    }
}
