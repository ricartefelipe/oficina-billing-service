package br.com.oficina.billing.adapters.in.web;

import br.com.oficina.billing.application.BillingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MercadoPagoWebhookControllerTest {

    private MockMvc mockMvc;
    private BillingService billingService;
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        billingService = Mockito.mock(BillingService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new MercadoPagoWebhookController(billingService)).build();
    }

    @Test
    void handleWebhook_comPaymentCreated_deveProcessar() throws Exception {
        var payload = Map.of(
            "action", "payment.created",
            "data", Map.of("id", "12345")
        );

        mockMvc.perform(post("/webhooks/mercadopago")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(payload)))
            .andExpect(status().isOk());

        verify(billingService).processarWebhookMercadoPago("12345");
    }

    @Test
    void handleWebhook_comPaymentUpdated_deveProcessar() throws Exception {
        var payload = Map.of(
            "action", "payment.updated",
            "data", Map.of("id", "99999")
        );

        mockMvc.perform(post("/webhooks/mercadopago")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(payload)))
            .andExpect(status().isOk());

        verify(billingService).processarWebhookMercadoPago("99999");
    }

    @Test
    void handleWebhook_comOutraAction_naoDeveProcessar() throws Exception {
        var payload = Map.of("action", "other.event");

        mockMvc.perform(post("/webhooks/mercadopago")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(payload)))
            .andExpect(status().isOk());

        verifyNoInteractions(billingService);
    }
}
