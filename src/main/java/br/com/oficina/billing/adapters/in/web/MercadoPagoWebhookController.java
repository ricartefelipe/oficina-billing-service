package br.com.oficina.billing.adapters.in.web;

import br.com.oficina.billing.application.BillingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/webhooks/mercadopago")
@Tag(name = "Webhooks")
public class MercadoPagoWebhookController {

    private static final Logger log = LoggerFactory.getLogger(MercadoPagoWebhookController.class);

    private final BillingService billingService;

    public MercadoPagoWebhookController(BillingService billingService) {
        this.billingService = billingService;
    }

    @PostMapping
    @Operation(summary = "Recebe notificações de pagamento do Mercado Pago")
    public ResponseEntity<Void> handleWebhook(@RequestBody Map<String, Object> payload) {
        String action = (String) payload.get("action");
        log.info("Webhook MercadoPago recebido: action={}", action);

        if ("payment.updated".equals(action) || "payment.created".equals(action)) {
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) payload.get("data");
            if (data != null && data.get("id") != null) {
                String paymentId = data.get("id").toString();
                billingService.processarWebhookMercadoPago(paymentId);
            }
        }
        return ResponseEntity.ok().build();
    }
}
