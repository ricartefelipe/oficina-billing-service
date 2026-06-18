package br.com.oficina.billing.adapters.in.web;

import br.com.oficina.billing.application.BillingService;
import br.com.oficina.billing.domain.Orcamento;
import br.com.oficina.billing.domain.StatusOrcamento;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/admin/orcamentos")
@Tag(name = "Orçamentos")
public class BillingController {

    private final BillingService billingService;

    public BillingController(BillingService billingService) {
        this.billingService = billingService;
    }

    @PostMapping("/{osId}/aprovar")
    @Operation(summary = "Aprova o orçamento e inicia pagamento via Mercado Pago")
    public ResponseEntity<Void> aprovar(@PathVariable UUID osId) {
        billingService.aprovarOrcamento(osId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{osId}/recusar")
    @Operation(summary = "Recusa o orçamento e cancela a OS")
    public ResponseEntity<Void> recusar(@PathVariable UUID osId) {
        billingService.recusarOrcamento(osId);
        return ResponseEntity.noContent().build();
    }

    public record OrcamentoResponse(
        UUID id,
        UUID osId,
        BigDecimal valorTotal,
        StatusOrcamento status,
        String mercadoPagoPreferenceId,
        Instant criadoEm
    ) {
        public static OrcamentoResponse from(Orcamento o) {
            return new OrcamentoResponse(o.id(), o.osId(), o.valorTotal(), o.status(),
                o.mercadoPagoPreferenceId(), o.criadoEm());
        }
    }
}
