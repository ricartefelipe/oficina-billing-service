package br.com.oficina.billing.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "orcamento")
public class Orcamento {

    @Id
    private UUID id;

    @Column(name = "os_id", nullable = false, unique = true)
    private UUID osId;

    @Column(name = "valor_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal valorTotal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatusOrcamento status;

    @Column(name = "mp_preference_id")
    private String mercadoPagoPreferenceId;

    @Column(name = "mp_payment_id")
    private String mercadoPagoPaymentId;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    @Column(name = "atualizado_em")
    private Instant atualizadoEm;

    protected Orcamento() {}

    public static Orcamento criar(UUID osId, BigDecimal valorTotal) {
        var o = new Orcamento();
        o.id = UUID.randomUUID();
        o.osId = osId;
        o.valorTotal = valorTotal;
        o.status = StatusOrcamento.AGUARDANDO_APROVACAO;
        o.criadoEm = Instant.now();
        return o;
    }

    public void aprovar() {
        if (status != StatusOrcamento.AGUARDANDO_APROVACAO) {
            throw new IllegalStateException("Orcamento nao esta aguardando aprovacao: " + status);
        }
        this.status = StatusOrcamento.APROVADO;
        this.atualizadoEm = Instant.now();
    }

    public void recusar() {
        if (status != StatusOrcamento.AGUARDANDO_APROVACAO) {
            throw new IllegalStateException("Orcamento nao esta aguardando aprovacao: " + status);
        }
        this.status = StatusOrcamento.RECUSADO;
        this.atualizadoEm = Instant.now();
    }

    public void registrarPreferenciaMercadoPago(String preferenceId) {
        this.mercadoPagoPreferenceId = preferenceId;
        this.status = StatusOrcamento.PAGAMENTO_PENDENTE;
        this.atualizadoEm = Instant.now();
    }

    public void registrarPagamento(String paymentId) {
        if (status != StatusOrcamento.PAGAMENTO_PENDENTE && status != StatusOrcamento.APROVADO) {
            throw new IllegalStateException("Orcamento nao esta aguardando pagamento: " + status);
        }
        this.mercadoPagoPaymentId = paymentId;
        this.status = StatusOrcamento.PAGO;
        this.atualizadoEm = Instant.now();
    }

    public void cancelar() {
        this.status = StatusOrcamento.CANCELADO;
        this.atualizadoEm = Instant.now();
    }

    public UUID id() { return id; }
    public UUID osId() { return osId; }
    public BigDecimal valorTotal() { return valorTotal; }
    public StatusOrcamento status() { return status; }
    public String mercadoPagoPreferenceId() { return mercadoPagoPreferenceId; }
    public String mercadoPagoPaymentId() { return mercadoPagoPaymentId; }
    public Instant criadoEm() { return criadoEm; }
    public Instant atualizadoEm() { return atualizadoEm; }
}
