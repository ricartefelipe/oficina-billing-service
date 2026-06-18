package br.com.oficina.billing.application.port.out;

import java.math.BigDecimal;
import java.util.UUID;

public interface MercadoPagoPort {
    String criarPreferencia(UUID osId, BigDecimal valor, String descricao);
    String consultarStatusPagamento(String paymentId);
}
