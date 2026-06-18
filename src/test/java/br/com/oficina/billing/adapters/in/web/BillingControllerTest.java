package br.com.oficina.billing.adapters.in.web;

import br.com.oficina.billing.application.BillingService;
import br.com.oficina.billing.domain.Orcamento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BillingControllerTest {

    private MockMvc mockMvc;
    private BillingService billingService;

    @BeforeEach
    void setUp() {
        billingService = Mockito.mock(BillingService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new BillingController(billingService)).build();
    }

    @Test
    void aprovar_deveRetornarNoContent() throws Exception {
        doNothing().when(billingService).aprovarOrcamento(any());

        mockMvc.perform(post("/admin/orcamentos/{osId}/aprovar", UUID.randomUUID()))
            .andExpect(status().isNoContent());
    }

    @Test
    void recusar_deveRetornarNoContent() throws Exception {
        doNothing().when(billingService).recusarOrcamento(any());

        mockMvc.perform(post("/admin/orcamentos/{osId}/recusar", UUID.randomUUID()))
            .andExpect(status().isNoContent());
    }

    @Test
    void orcamentoResponse_from_deveMapearCampos() {
        var orcamento = Orcamento.criar(UUID.randomUUID(), new BigDecimal("500.00"));
        var response = BillingController.OrcamentoResponse.from(orcamento);

        assertThat(response.osId()).isEqualTo(orcamento.osId());
        assertThat(response.valorTotal()).isEqualByComparingTo(new BigDecimal("500.00"));
    }
}
